package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;

class Scout extends Base {
    private static BodyInfo currentTarget;
    private static MapLocation currentTargetLocation;

    private static Direction exploringDirection;

    private static boolean hasReachedInitialLocation = false;

    static void run() throws GameActionException {
        int strategy = rc.readBroadcast(Message.STRATEGY_CHANNEL);

        while (true) {
            try {
                Base.update();
                runRound();
                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    private static void runRound() throws GameActionException {
        Pathing.tryDodgeBullet();
        setPriorityTarget();

        if (!hasReachedInitialLocation && numberOfScouts > 1) {
            Pathing.tryMove(rc.getLocation().directionTo(currentTargetLocation));
            if (rc.canSenseLocation(currentTargetLocation)) {
                hasReachedInitialLocation = true;
            }
        }

        if (currentTarget != null) {
            if(!tryHideInTree()) {
                tryMoveToTarget();
            }
            tryAttackTarget();
        } else {
            explore();
        }

        Utils.collectBullets();
    }

    private static boolean tryHideInTree() throws GameActionException {
        TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
        Arrays.sort(nearbyTrees, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(currentTargetLocation), o2.getLocation().distanceTo(currentTargetLocation)));

        for (TreeInfo tree : nearbyTrees) {
            Direction treeToEnemy = tree.getLocation().directionTo(currentTargetLocation);
            MapLocation offsetLocation = tree.getLocation().add(treeToEnemy, tree.getRadius() - rc.getType().bodyRadius);

            if (!rc.hasMoved() && rc.canMove(offsetLocation)) {
                rc.move(offsetLocation);
                return true;
            }
        }

        return false;
    }

    private static boolean tryMoveToTarget() throws GameActionException {
        if (!rc.hasMoved() && rc.getLocation().distanceTo(currentTargetLocation) > GameConstants.LUMBERJACK_STRIKE_RADIUS * 2) {
            Pathing.tryMove(rc.getLocation().directionTo(currentTargetLocation));
        }
        return false;
    }

    private static void tryAttackTarget() throws GameActionException {
        if (rc.canSenseRobot(currentTarget.getID()) && rc.canFireSingleShot() && numberOfLumberjacks > 0) {
            RobotInfo target = rc.senseRobot(currentTarget.getID());
            if (target.getTeam() != rc.getTeam()) {
                Direction dir = rc.getLocation().directionTo(target.getLocation());
                rc.fireSingleShot(dir);
            }
        }
    }

    private static void setPriorityTarget() throws GameActionException {
        if (!hasReachedInitialLocation) {
            currentTargetLocation = determineRushLocation();
            return;
        }

        for (RobotInfo robot : visibleEnemyUnits) {
            if (robot.getType() == RobotType.GARDENER) {
                currentTarget = robot;
                currentTargetLocation = robot.getLocation();
                return;
            }
        }

        for (TreeInfo tree : visibleNeutralTrees) {
            if (tree.getContainedBullets() > 0) {
                currentTarget = tree;
                currentTargetLocation = tree.getLocation();
                return;
            }
        }

        currentTarget = null;
        currentTargetLocation = null;
    }

    private static MapLocation determineRushLocation() throws GameActionException {
        return Utils.closestEnemyArchonLocation();
    }

    private static void explore() throws GameActionException {
        if (exploringDirection == null) {
            exploringDirection = Pathing.randomDirection();
        }

        if (!rc.onTheMap(rc.getLocation().add(exploringDirection, rc.getType().sensorRadius - 0.1F))) {
            exploringDirection = Pathing.randomDirection();
        } else {
            if (!rc.hasMoved()) {
                Pathing.tryMove(exploringDirection);
            }
        }
    }
}