package Alt_F4;

import battlecode.common.*;

class Soldier extends Base {
    private static Direction movingDirection;

    private static MapLocation targetLocation;


    static void run() throws GameActionException {
        System.out.println("Soldier has spawned.");

        while (true) {
            try {
                Utils.CheckWinConditions();
                Base.update();

                if (!rc.hasMoved() && nearbyBullets.length > 0) {
                    Pathing.tryDodgeBullet();
                }

                tryDetermineLocation();
                tryFireOnEnemy();

                if (!rc.hasAttacked() && targetLocation != null) {
                    tryMoveToLocation();
                } else if(rc.hasAttacked() && targetLocation != null) {
                    Pathing.tryMove(rc.getLocation().directionTo(targetLocation).opposite());
                } else {
                    wander();
                    tryClearTrees();
                }

                Utils.collectBullets();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static boolean tryDetermineLocation() throws GameActionException {
        if (visibleEnemyUnits.length > 0) {
            targetLocation = visibleEnemyUnits[0].getLocation();
            return true;
        }

        if (rc.getRoundNum() < 100) {
            targetLocation = Utils.closestEnemyArchonLocation();
            return false;
        }

        targetLocation = null;
        return false;
    }

    private static boolean tryFireOnEnemy() throws GameActionException {
        for (RobotInfo robot : visibleEnemyUnits) {
            if (!willShotCollideWithBody(robot)) {
                return tryFireOnTarget(robot.getLocation());
            }
        }

        return false;
    }

    private static boolean tryMoveToLocation() throws GameActionException {
        if (rc.getLocation().distanceTo(targetLocation) >= rc.getType().sensorRadius) {
            return Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
        }
        return false;
    }

    private static boolean willShotCollideWithBody(BodyInfo robotTarget) {
        Vector toEnemy = new Vector(rc.getLocation(), robotTarget.getLocation());

        for (RobotInfo friendlyRobot : visibleFriendlyUnits) {
            Vector toFriendly = new Vector(rc.getLocation(), friendlyRobot.getLocation());
            Vector projection = Vector.projection(toFriendly, toEnemy);

            if (projection.getEndPoint().distanceTo(friendlyRobot.getLocation()) <= friendlyRobot.getRadius()){
                return true;
            }
        }
        return false;
    }

    private static boolean tryFireOnTarget(MapLocation loc) throws GameActionException {
        Direction firingDirection = rc.getLocation().directionTo(loc);

        if (rc.canFirePentadShot() && shouldFirePentadShot(targetLocation)) {
            rc.firePentadShot(firingDirection);
            return true;
        }

        if (rc.canFireSingleShot()) {
            rc.fireSingleShot(firingDirection);
            return true;
        }

        return false;
    }

    private static boolean tryClearTrees() throws GameActionException {
        TreeInfo[] treesInStrideRadius = rc.senseNearbyTrees(rc.getLocation(), rc.getType().bodyRadius + 1, Team.NEUTRAL);
        if (treesInStrideRadius.length >= 3) {
            if (rc.canFirePentadShot()) {
                Direction dir = rc.getLocation().directionTo(treesInStrideRadius[0].getLocation());
                rc.firePentadShot(dir);

                if(!rc.hasMoved()) {
                    Pathing.tryMove(dir.opposite());
                }

                return true;
            }
        }
        return false;
    }

    private static boolean shouldFirePentadShot(MapLocation loc) {
        int robotsInPentadRange = 0;

        Direction firingDirection = rc.getLocation().directionTo(loc);
        Direction directionRangeRight = firingDirection.rotateRightDegrees(30);
        Direction directionRangeLeft = firingDirection.rotateLeftDegrees(30);

        for (RobotInfo robot : visibleEnemyUnits) {
            Direction robotDirection = rc.getLocation().directionTo(robot.getLocation());
            if (directionRangeLeft.getAngleDegrees() >= robotDirection.getAngleDegrees()
                    && directionRangeRight.getAngleDegrees() <= robotDirection.getAngleDegrees()) {
                robotsInPentadRange++;
            }
        }

        return robotsInPentadRange > 1;
    }

    private static void wander() throws GameActionException {
        if (movingDirection == null) {
            movingDirection = Pathing.randomDirection();
        }

        if (!rc.onTheMap(rc.getLocation().add(movingDirection, rc.getType().sensorRadius - 0.1F))) {
            movingDirection = Pathing.randomDirection();
        } else {
            if (!rc.hasMoved()) {
                Pathing.tryMove(movingDirection);
            }
        }
    }
}

