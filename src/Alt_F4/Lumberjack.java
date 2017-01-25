package Alt_F4;

import battlecode.common.*;

public class Lumberjack extends Base {
    public static void run() throws GameActionException {
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

    public static void clearTrees() throws GameActionException {
        if (visibleFriendlyUnits.length > 0 && ((rc.getLocation().distanceTo(visibleFriendlyUnits[0].getLocation()) - visibleFriendlyUnits[0].getType().bodyRadius) < GameConstants.LUMBERJACK_STRIKE_RADIUS)) {
            Direction awayFromFriendly = rc.getLocation().directionTo(visibleFriendlyUnits[0].getLocation());
            awayFromFriendly = awayFromFriendly.rotateRightDegrees(180);
            Pathing.tryMove(awayFromFriendly);
        } else if (!rc.hasMoved() && nearbyBullets.length > 0) {
            Pathing.tryDodgeBullet();
        } else if (visibleEnemyUnits.length > 0 && rc.getLocation().distanceTo(visibleEnemyUnits[0].getLocation()) > GameConstants.LUMBERJACK_STRIKE_RADIUS) {
            Direction toEnemy = rc.getLocation().directionTo(visibleEnemyUnits[0].getLocation());
            Pathing.tryMove(toEnemy);
        } else if (visibleNeutralTrees.length == 0) {
            if (rc.getLocation().distanceTo(enemyArchonLocations[0]) > GameConstants.LUMBERJACK_STRIKE_RADIUS + RobotType.ARCHON.bodyRadius) {
                Direction toEnemy = rc.getLocation().directionTo(enemyArchonLocations[0]);
                Pathing.tryMove(toEnemy);
            } else {
                Pathing.tryMove(Pathing.randomDirection());
            }
        } else {
            if (visibleNeutralTrees[0].getRadius() + GameConstants.LUMBERJACK_STRIKE_RADIUS > rc.getLocation().distanceTo(visibleNeutralTrees[0].getLocation())) {
                if (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam()).length == 0) {
                    rc.strike();
                }
            } else {
                Direction toTree = rc.getLocation().directionTo(visibleNeutralTrees[0].getLocation());
                Pathing.tryMove(toTree);
            }
        }

        if (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam().opponent()).length > 0) {
            if (rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam()).length == 0) {
                rc.strike();
            }
        }
    }
}
