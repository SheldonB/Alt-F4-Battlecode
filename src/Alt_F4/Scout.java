package Alt_F4;

import battlecode.common.*;

public class Scout extends Base {
    private static MapLocation targetLocation;
    private static boolean wandering = false;

    public static void run() throws GameActionException {
        while (true) {
            try {
                if (!wandering) {
                    coordinateAttack();
                } else {
                    wander();
                }

                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    static void coordinateAttack() throws GameActionException {
        if (rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL) == 0) {
            targetLocation = determineRushLocation();
            rc.broadcast(Message.SCOUT_ATTACK_COORD_CHANNEL, Utils.mapLocationToInt(targetLocation));
        }

        if (targetLocation == null) {
            targetLocation = updateTargetLocation();

            if (targetLocation == null) {
                wandering = true;
            }
        } else {
            rc.setIndicatorLine(rc.getLocation(), targetLocation, 0, 255, 0);
        }

        if (rc.getLocation().distanceTo(targetLocation) > GameConstants.BULLET_SPAWN_OFFSET) {
            Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
        }

        collectBullets();
        targetLocation = determinePriorityTarget();
        tryFireOnTarget(targetLocation);
    }

    static void tryFireOnTarget(MapLocation loc) throws GameActionException {
        if (rc.senseNearbyRobots(loc, rc.getType().sensorRadius, rc.getTeam().opponent()).length != 0) {
            if (rc.canFireSingleShot()) {
                rc.fireSingleShot(rc.getLocation().directionTo(targetLocation));
            }
        } else {
            targetLocation = null;
        }
    }

    static MapLocation updateTargetLocation() throws GameActionException {
        MapLocation broadcastLocation = Utils.mapLocationFromInt(rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL));
        if (rc.getLocation().distanceTo(broadcastLocation) > rc.getType().strideRadius) {
            return broadcastLocation;
        }

        RobotInfo[] newTargets = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
        if (newTargets.length != 0) {
            return newTargets[0].getLocation();
        }

        return null;
    }

    static MapLocation determinePriorityTarget() throws GameActionException {
        RobotInfo[] sensedRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());

        for (RobotInfo robot : sensedRobots) {
            if (robot.getType() == RobotType.GARDENER) {
                return robot.getLocation();
            } else if (robot.getType() == RobotType.SCOUT) {
                return robot.getLocation();
            } else if (robot.getType() == RobotType.SOLDIER) {
                return robot.getLocation();
            } else if (robot.getType() == RobotType.LUMBERJACK) {
                return robot.getLocation();
            } else if (robot.getType() == RobotType.ARCHON) {
                return robot.getLocation();
            } else if (robot.getType() == RobotType.TANK) {
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

    static void wander() throws GameActionException {
        Pathing.tryMove(Pathing.randomDirection());

        RobotInfo[] targets = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
        if (targets.length > 0) {
            targetLocation = targets[0].getLocation();
            wandering = false;
        }
    }
}
