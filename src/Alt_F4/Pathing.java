package Alt_F4;

import battlecode.common.*;

import java.util.ArrayList;

class Pathing extends Base {

    private static ArrayList<MapLocation> previousLocations = new ArrayList<>();

    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     *
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,8, rc.getType().strideRadius);
    }

    static boolean tryMove(Direction dir, float distance) throws GameActionException {
        return tryMove(dir,20,3, distance);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     *
     */
    private static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide, float distance) throws GameActionException {
        if(rc.hasMoved())  {
            return false;
        }

        if (rc.canMove(dir) && !hasBeenToThisLocationRecently(rc.getLocation().add(dir, distance))) {
            doMove(dir, distance);
            return true;
        }

        int currentCheck = 1;

        while(currentCheck <= checksPerSide) {
            Direction leftOffset = dir.rotateLeftDegrees(degreeOffset * currentCheck);
            Direction rightOffset = dir.rotateRightDegrees(degreeOffset * currentCheck);

            if(rc.canMove(leftOffset) && !hasBeenToThisLocationRecently(rc.getLocation().add(leftOffset, distance))) {
                doMove(leftOffset, distance);
                return true;
            }

            if(rc.canMove(rightOffset) && !hasBeenToThisLocationRecently(rc.getLocation().add(leftOffset, distance))) {
                doMove(rightOffset, distance);
                return true;
            }

            currentCheck++;
        }

        return false;
    }

    private static void doMove(Direction dir, float distance) throws GameActionException {
        rc.move(dir, distance);
        previousLocations.add(rc.getLocation());
        truncatePreviousLocations();
    }

    private static boolean hasBeenToThisLocationRecently(MapLocation loc) {
        for (MapLocation previousLoc : previousLocations) {
            if (previousLoc.distanceTo(loc) <= 0.10) {
                return true;
            }
        }
        return false;
    }

    private static void truncatePreviousLocations() {
        if (previousLocations.size() > 100) {
            previousLocations.remove(0);
        }
    }

    private static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    private static boolean trySideStep(BulletInfo bullet) throws GameActionException{

        Direction towards = bullet.getDir();
        MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
        MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);

        return (Pathing.tryMove(towards.rotateRightDegrees(90)) || Pathing.tryMove(towards.rotateLeftDegrees(90)));
    }

    static void tryDodgeBullet() throws GameActionException {
        for (BulletInfo bullet : nearbyBullets) {
            if (willCollideWithMe(bullet)) {
                trySideStep(bullet);
                return;
            }
        }
    }
}
