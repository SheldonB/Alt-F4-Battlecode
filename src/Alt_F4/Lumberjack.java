package Alt_F4;

import battlecode.common.*;

class Lumberjack extends Base {
    static void run() throws GameActionException {
        while (true) {
            try {
                Base.update();
                clearTrees();
                Utils.collectBullets();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static boolean needMoveAwayFriendlies() throws GameActionException {
        if (visibleFriendlyUnits.length > 0) {
            float distanceToFriendly = rc.getLocation().distanceTo(visibleFriendlyUnits[0].getLocation());
            distanceToFriendly -= visibleFriendlyUnits[0].getType().bodyRadius;
            return (distanceToFriendly < GameConstants.LUMBERJACK_STRIKE_RADIUS);
        }
        return false;
    }

    private static void moveAwayFriendlies() throws GameActionException {
        Direction awayFromFriendly = rc.getLocation().directionTo(visibleFriendlyUnits[0].getLocation());
        awayFromFriendly = awayFromFriendly.rotateRightDegrees(180);
        Pathing.tryMove(awayFromFriendly);
    }

    private static boolean canMoveTowardEnemy() throws GameActionException {
        if (visibleEnemyUnits.length > 0) {
            float distanceToEnemy = rc.getLocation().distanceTo(visibleEnemyUnits[0].getLocation());
            return (distanceToEnemy > GameConstants.LUMBERJACK_STRIKE_RADIUS);
        }
        return false;
    }

    private static void moveTowardEnemy() throws GameActionException {
        Direction toEnemy = rc.getLocation().directionTo(visibleEnemyUnits[0].getLocation());
        Pathing.tryMove(toEnemy);
    }

    private static boolean canGetTarget() throws GameActionException {
        return rc.readBroadcast(Message.LUMBERJACK_GENERAL_CHANNEL_START) != 0;
    }

    private static void moveTowardTarget() throws GameActionException {
        int targetLoc = rc.readBroadcast(Message.LUMBERJACK_GENERAL_CHANNEL_START);
        MapLocation targetLocation = Utils.mapLocationFromInt(targetLoc);
        Direction toTarget = rc.getLocation().directionTo(targetLocation);
        Pathing.tryMove(toTarget);

        if (rc.getLocation().distanceTo(targetLocation) <= rc.getType().sensorRadius && visibleEnemyUnits.length == 0) {
            rc.broadcast(Message.LUMBERJACK_GENERAL_CHANNEL_START, 0);
        }
    }

    private static void moveTowardEnemyArchons() throws GameActionException {
        float distanceToArchon = rc.getLocation().distanceTo(enemyArchonLocations[0]);
        if (distanceToArchon > GameConstants.LUMBERJACK_STRIKE_RADIUS + RobotType.ARCHON.bodyRadius) {
            Direction toEnemy = rc.getLocation().directionTo(enemyArchonLocations[0]);
            Pathing.tryMove(toEnemy);
        } else {
            Pathing.tryMove(Pathing.randomDirection());
        }
    }

    private static void moveTowardNeutralTrees() throws GameActionException {
        float distanceToTree = rc.getLocation().distanceTo(visibleNeutralTrees[0].getLocation());
        if (visibleNeutralTrees[0].getRadius() + GameConstants.LUMBERJACK_STRIKE_RADIUS > distanceToTree) {
            if (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam()).length == 0) {
                rc.strike();
            }
        } else {
            Direction toTree = rc.getLocation().directionTo(visibleNeutralTrees[0].getLocation());
            Pathing.tryMove(toTree);
        }
    }

    private static boolean enemyInStrikeDistance() throws GameActionException {
        return (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam().opponent()).length > 0);
    }

    private static void safeStrike() throws GameActionException {
        if (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam()).length == 0) {
            rc.broadcast(Message.LUMBERJACK_GENERAL_CHANNEL_START, Utils.mapLocationToInt(visibleEnemyUnits[0].getLocation()));
            rc.strike();
        }
    }

    private static void smartMove() throws GameActionException {
        if (needMoveAwayFriendlies()) {
            moveAwayFriendlies();
        } else if (nearbyBullets.length > 0) {
            Pathing.tryDodgeBullet();
        } else if (canMoveTowardEnemy()) {
            moveTowardEnemy();
        } else if (visibleNeutralTrees.length != 0) {
            moveTowardNeutralTrees();
        }  else if (canGetTarget()) {
            moveTowardTarget();
        } else {
            moveTowardEnemyArchons();
        }
    }

    private static void clearTrees() throws GameActionException {
        smartMove();

        if (enemyInStrikeDistance()) {
            safeStrike();
        }
    }
}
