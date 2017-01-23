package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;

public class Scout extends Base {
    private static MapLocation targetLocation;
    private static int targetID = 0;
    private static boolean completedRush = false;
    private static boolean wandering = false;

    public static void run() throws GameActionException {
        int strategy = rc.readBroadcast(Message.STRATEGY_CHANNEL);

        while (true) {
            try {
                Base.update();

                Pathing.tryDodgeBullet();

                tryScoutRush();

                collectBullets();
                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    static void tryScoutRush() throws GameActionException {
        if (targetLocation == null && rc.getRoundNum() < 100) {
            targetLocation = determineRushLocation();
        } else {
            RobotInfo target = determinePriorityTarget();

            if (target != null) {
                targetLocation = target.getLocation();
                targetID = target.getID();
            } else {
                targetLocation = lastKnownEnemyArchonLocation;

                RobotInfo[] potentialTargets = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
                if (potentialTargets.length == 0) {
                    targetLocation = null;
                }
            }
        }

        if (targetLocation == null) {
            wander();
        } else {
            if (rc.getRoundNum() > 100) {
                hideInNearbyTree();
            } else {
                if (rc.getLocation().distanceTo(targetLocation) > GameConstants.LUMBERJACK_STRIKE_RADIUS * 2) {
                    Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
                }
            }
        }
        tryAttackTargetUnit();

    }

    static void hideInNearbyTree() throws GameActionException {
        TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
        Arrays.sort(nearbyTrees, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(targetLocation), o2.getLocation().distanceTo(targetLocation)));

        for (TreeInfo tree : nearbyTrees) {
            Direction treeToEnemy = tree.getLocation().directionTo(targetLocation);
            MapLocation offsetLocation = tree.getLocation().add(treeToEnemy, tree.getRadius() - rc.getType().bodyRadius);

            if (rc.canMove(offsetLocation)) {
                rc.move(offsetLocation);
                break;
            }
        }
    }

    static void tryAttackTargetUnit() throws GameActionException {
        if (rc.canSenseRobot(targetID) && rc.canFireSingleShot()) {
            RobotInfo target = rc.senseRobot(targetID);
            if (target.getTeam() != rc.getTeam()) {
                Direction dir = rc.getLocation().directionTo(target.getLocation());
                rc.fireSingleShot(dir);
            }
        }
    }

    static void tryFireOnTarget(MapLocation loc) throws GameActionException {
        if (visibleEnemyUnits.length != 0) {
            if (rc.canFireSingleShot()) {
                rc.fireSingleShot(rc.getLocation().directionTo(targetLocation));
            }
        }
    }

    static RobotInfo determinePriorityTarget() throws GameActionException {
        for (RobotInfo robot : visibleEnemyUnits) {
            if (robot.getType() == RobotType.GARDENER) {
                return robot;
            } else if (robot.getType() == RobotType.SCOUT) {
                return robot;
            } else if (robot.getType() == RobotType.SOLDIER) {
                return robot;
            } else if (robot.getType() == RobotType.LUMBERJACK) {
                return robot;
            } else if (robot.getType() == RobotType.ARCHON) {
                return robot;
            } else if (robot.getType() == RobotType.TANK) {
                return robot;
            }
        }

        return null;
    }

    static MapLocation determineRushLocation() throws GameActionException {
        MapLocation closestArchonLocation = enemyArchonLocations[0];
        for (MapLocation loc : enemyArchonLocations) {
            if (rc.getLocation().distanceTo(loc) < rc.getLocation().distanceTo(closestArchonLocation)) {
                closestArchonLocation = loc;
            }
        }

        return closestArchonLocation;
    }

    static void collectBullets() throws GameActionException {
        for (TreeInfo tree : visibleNeutralTrees) {
            if (rc.canShake(tree.getID())) {
                rc.shake(tree.getID());
                break;
            }
        }
    }

    static void wander() throws GameActionException {
        Pathing.tryMove(Pathing.randomDirection());
    }
}