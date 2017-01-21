package Alt_F4;

import battlecode.common.*;

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

                tryDodgeBullet();

                if (strategy == Message.SCOUT_RUSH_MESSAGE) {
                    tryScoutRush();
                }

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
        for (TreeInfo tree : nearbyTrees) {
            Direction treeToEnemy = tree.getLocation().directionTo(targetLocation);
            MapLocation offsetLocation = tree.getLocation().add(treeToEnemy, tree.getRadius());

            if (rc.canMove(offsetLocation)) {
                rc.move(offsetLocation);
                break;
            }
        }
    }

    static boolean willCollideWithMe(BulletInfo bullet) {
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

    static boolean trySideStep(BulletInfo bullet) throws GameActionException{

        Direction towards = bullet.getDir();
        MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
        MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);

        return(Pathing.tryMove(towards.rotateRightDegrees(90)) || Pathing.tryMove(towards.rotateLeftDegrees(90)));
    }

    static void tryDodgeBullet() throws GameActionException {
        for (BulletInfo bullet : nearbyBullets) {
            if (willCollideWithMe(bullet)) {
                trySideStep(bullet);
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