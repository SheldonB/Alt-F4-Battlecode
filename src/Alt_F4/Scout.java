package Alt_F4;

import battlecode.common.*;

public class Scout extends Base {
    private static MapLocation targetLocation;
    private static int targetID = 0;
    private static boolean wandering = false;

    public static void run() throws GameActionException {

        while (true) {
            try {
                if (rc.readBroadcast(Message.SCOUT_COUNT_CHANNEL) >= 3) {

                    updateTarget();
                    tryDodgeBullet();
                    if (targetLocation == null) {
                        wander();
                        System.out.println("Trying to wander");
                    } else {
                        System.out.println("Has target. Moving toward and trying to fire.");
                        if (rc.getLocation().distanceTo(targetLocation) > GameConstants.LUMBERJACK_STRIKE_RADIUS) {
                            Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
                        }
                        tryAttackTargetUnit();
                    }

                    collectBullets();
                    Clock.yield();
                }
            } catch (Exception e) {
               System.out.println(e.getMessage());
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
        BulletInfo[] bulletInfo = rc.senseNearbyBullets();

        for (BulletInfo bullet : bulletInfo) {
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
        } else {
            targetLocation = null;
            targetID = 0;
        }
    }

    /*
    static void coordinateAttack() throws GameActionException {
        //if (rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL) == 0) {
        //    targetLocation = determineRushLocation();
        //    rc.broadcast(Message.SCOUT_ATTACK_COORD_CHANNEL, Utils.mapLocationToInt(targetLocation));
        //}

        //if (targetLocation == null) {
        //    targetLocation = updateTarget();

        //    if (targetLocation == null) {
        //        wandering = true;
        //    }
        } else {
            rc.setIndicatorLine(rc.getLocation(), targetLocation, 0, 255, 0);
        }

        if (rc.getLocation().distanceTo(targetLocation) > GameConstants.LUMBERJACK_STRIKE_RADIUS) {
            Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
        }

        //targetLocation = determinePriorityTarget();
        //tryFireOnTarget(targetLocation);
    }
    */

    static void tryFireOnTarget(MapLocation loc) throws GameActionException {
        if (rc.senseNearbyRobots(loc, rc.getType().sensorRadius, rc.getTeam().opponent()).length != 0) {
            if (rc.canFireSingleShot()) {
                rc.fireSingleShot(rc.getLocation().directionTo(targetLocation));
            }
        } else {
            targetLocation = null;
        }
    }

    static void updateTarget() throws GameActionException {
        // Try and read the swarm target location
        int swarmsTargetLocation = rc.readBroadcast(Message.SCOUT_ATTACK_COORD_CHANNEL);
        int swarmsTargetId = rc.readBroadcast(Message.SCOUT_ATTACK_TARGET_ID_CHANNEL);

        // If the data stored is 0, then we know
        // that we have yet to get the initial locations
        // so lets make the target location the initial archon
        // location. Else, set the target locaton to the swarm
        // target.
        if (swarmsTargetLocation == 0 && targetLocation == null
                && swarmsTargetId == 0 && targetID == 0) {
            targetLocation = determineRushLocation();
            broadcastTargetLocation(targetLocation);
            System.out.println("No swarm data. Setting locations to initial archon locations");
        } else if (targetLocation == null && targetID == 0) {
            targetLocation = Utils.mapLocationFromInt(swarmsTargetLocation);
            targetID = swarmsTargetId;
        }

        RobotInfo target = determinePriorityTarget();

        if (target != null) {
            broadcastTargetLocation(target.getLocation());
            broadcastTargetID(target.getID());
        }

    }

    static void broadcastTargetLocation(MapLocation loc) throws GameActionException {
        int codedLocation = Utils.mapLocationToInt(loc);
        rc.broadcast(Message.SCOUT_ATTACK_COORD_CHANNEL, codedLocation);
    }

    static void broadcastTargetID(int id) throws GameActionException {
        rc.broadcast(Message.SCOUT_ATTACK_TARGET_ID_CHANNEL, id);
    }

    /*
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
    */

    static RobotInfo determinePriorityTarget() throws GameActionException {
        RobotInfo[] sensedRobots = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());

        for (RobotInfo robot : sensedRobots) {
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

        //RobotInfo[] targets = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
        //if (targets.length > 0) {
        //    targetLocation = targets[0].getLocation();
        //    wandering = false;
        //}
    }
}
