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
                if (rc.getRoundNum() % 16 == 0) {
                    Broadcasting.broadcastVisibleEnemyLocations();
                }
                runRound();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void runRound() throws GameActionException {
        if (!rc.hasMoved() && nearbyBullets.length > 0) {
            Pathing.tryDodgeBullet();
        }

        tryDetermineLocation();
        if (targetLocation != null) {
            System.out.println(targetLocation);
            rc.setIndicatorLine(rc.getLocation(), targetLocation, 255, 0, 0);
        }
        tryFireOnEnemy();

        if (!rc.hasAttacked() && targetLocation != null) {
            tryMoveToLocation();
            tryClearTrees();
        } else if(!rc.hasAttacked()) {
            wander();
            tryClearTrees();
        }

        Utils.collectBullets();
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

        if (tryReadFromCommonBroadcast()) {
            return true;
        }
        targetLocation = null;
        return false;
    }

    private static boolean tryReadFromCommonBroadcast() throws GameActionException {
        Packet packet = Broadcasting.tryReadPacket(Message.SOLDIER_GENERAL_CHANNEL_START);
        if (packet != null) {
            if (rc.canSenseLocation(packet.getLocation()) && visibleEnemyUnits.length == 0) {
                tryUpdateCommonBroadcast();
                return false;
            } else {
                targetLocation = packet.getLocation();
                return true;
            }
        } else {
            tryUpdateCommonBroadcast();
            return false;
        }

    }

    private static void tryUpdateCommonBroadcast() throws GameActionException {
        int startChannel = Message.ENEMY_LOCATION_CHANNEL_START;
        int endChannel = Message.ENEMY_LOCATION_CHANNEL_END;
        for (int i = startChannel; i <= endChannel; i += Packet.PACKET_SIZE) {
            Packet packet = Broadcasting.tryReadPacket(startChannel);
            if (packet != null && !rc.canSenseLocation(packet.getLocation()) && packet.getLocation().x != 0F && packet.getLocation().y != 0F) {
                Broadcasting.tryBroadcastPacket(packet, Message.SOLDIER_GENERAL_CHANNEL_START);
                return;
            }
        }
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

        if (rc.canFireSingleShot() && rc.getTeamBullets() > 100) {
            rc.fireSingleShot(firingDirection);
            return true;
        }

        return false;
    }

    private static boolean tryClearTrees() throws GameActionException {
        if (rc.getTeamBullets() < 200) {
            return false;
        }

        TreeInfo[] treesInStrideRadius = rc.senseNearbyTrees(rc.getLocation(), rc.getType().bodyRadius + 1, Team.NEUTRAL);
        if (treesInStrideRadius.length >= 1) {
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
        int bodiesInPentadRange = 0;

        Direction firingDirection = rc.getLocation().directionTo(loc);
        Direction directionRangeRight = firingDirection.rotateRightDegrees(30);
        Direction directionRangeLeft = firingDirection.rotateLeftDegrees(30);

        for (RobotInfo robot : visibleEnemyUnits) {
            if (Utils.isBodyInRange(robot, directionRangeRight, directionRangeLeft)) {
                bodiesInPentadRange++;
            }
        }

        for (TreeInfo tree : visibleEnemyTrees) {
            if (Utils.isBodyInRange(tree, directionRangeRight, directionRangeLeft)) {
                bodiesInPentadRange++;
            }
        }

        return bodiesInPentadRange > 1;
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

