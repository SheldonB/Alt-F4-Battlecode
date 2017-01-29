package Alt_F4;

import battlecode.common.*;

public class Tank extends Base {
    private static MapLocation targetLocation;
    private static Direction movingDirection;

    private static boolean hasReachedArchon = false;

    public static void run() {
        System.out.println("Tank spawned.");

        while (true) {
            try {
                Base.update();
                Utils.CheckWinConditions();
                runRound();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void runRound() throws GameActionException {
        tryDetermineLocation();
        tryFire();

        if (!rc.hasAttacked() && targetLocation != null) {
            tryMoveToLocation();
        } else if(!rc.hasAttacked()) {
            wander();
        }
    }

    private static boolean tryDetermineLocation() {

        if (rc.canSenseLocation(Utils.closestEnemyArchonLocation())) {
            hasReachedArchon = true;
        }

        if (visibleEnemyUnits.length > 0) {
            targetLocation = visibleEnemyUnits[0].getLocation();
            return true;
        }

        if (!hasReachedArchon) {
            targetLocation = Utils.closestEnemyArchonLocation();
            return false;
        }

        targetLocation = null;
        return false;
    }

    private static boolean tryMoveToLocation() throws GameActionException {
        if (rc.getLocation().distanceTo(targetLocation) >= rc.getType().sensorRadius) {
            return Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
        }
        return false;
    }

    private static boolean tryFire() throws GameActionException {
        for (RobotInfo robot : visibleEnemyUnits) {
            if (rc.canFirePentadShot()) {
                rc.firePentadShot(rc.getLocation().directionTo(robot.getLocation()));
                return true;
            }
        }
        return false;
    }

    private static void wander() throws GameActionException {
        if (movingDirection == null) {
            movingDirection = Pathing.randomDirection();
        }

        if (!rc.canMove(movingDirection)) {
            movingDirection = Pathing.randomDirection();
        } else {
            if (!rc.hasMoved()) {
                Pathing.tryMove(movingDirection);
            }
        }
    }
}
