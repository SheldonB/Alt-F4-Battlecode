package Alt_F4;

import battlecode.common.*;

public class Scout extends Base {
    private static MapLocation targetLocation;

    public static void run() throws GameActionException {
        while (true) {
            try {
                if (rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL) == 0) {
                    targetLocation = determineRushLocation();
                    rc.broadcast(Message.SCOUT_ATTACK_COORD_CHANNEL, Utils.mapLocationToInt(targetLocation));
                }

                if (targetLocation == null) {
                    targetLocation = Utils.mapLocationFromInt(rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL));
                }

                if (targetLocation != null) {
                    rc.setIndicatorLine(rc.getLocation(), targetLocation, 255, 0, 0);
                }

                if (rc.getLocation().distanceTo(targetLocation) > GameConstants.BULLET_SPAWN_OFFSET) {
                    Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
                }

                collectBullets();
                targetLocation = determinePriorityTarget();
                tryFireOnTarget(targetLocation);
                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    static void tryFireOnTarget(MapLocation loc) throws GameActionException {
        if (rc.canFireSingleShot()) {
            rc.fireSingleShot(rc.getLocation().directionTo(targetLocation));
        }
    }

    static MapLocation determinePriorityTarget() throws GameActionException {
        RobotInfo[] sensedRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());

        for (RobotInfo robot : sensedRobots) {
            if (robot.getType() == RobotType.ARCHON) {
                return robot.getLocation();
            }
        }
        return targetLocation;
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
        TreeInfo[] sensedTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL);

        for (TreeInfo tree : sensedTrees) {
            if (rc.canShake(tree.getID())) {
                rc.shake(tree.getID());
                break;
            }
        }
    }

    static void wander() {
        Direction dir = enemyArchonLocations[0].directionTo(rc.getLocation());
        rc.setIndicatorLine(rc.getLocation(), enemyArchonLocations[0], 255, 0, 0);
    }

    static void harassClosestEnemy() throws GameActionException {
        RobotInfo enemies[] = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemies.length > 0) {
            RobotInfo closestEnemy = enemies[0];
            for (RobotInfo enemy : enemies) {
                if (rc.getLocation().distanceTo(enemy.getLocation()) < rc.getLocation().distanceTo(closestEnemy.getLocation())) {
                    closestEnemy = enemy;
                }
            }
            Pathing.tryMove(rc.getLocation().directionTo(closestEnemy.getLocation()));
            rc.fireSingleShot(rc.getLocation().directionTo(closestEnemy.getLocation()));
        }
    }
}
