package Alt_F4;

import battlecode.common.*;

public class Base {
    static int robotID;
    static int numberOfArchons;
    static int numberOfEnemyArchons;

    static MapLocation[] archonLocations;
    static MapLocation[] enemyArchonLocations;

    static MapLocation lastKnownEnemyArchonLocation;

    static RobotInfo[] visibleFriendlyUnits;
    static RobotInfo[] visibleEnemyUnits;

    static TreeInfo[] visibleNeutralTrees;
    static TreeInfo[] visibleFriendlyTrees;
    static TreeInfo[] visibleEnemyTrees;

    static BulletInfo[] nearbyBullets;

    protected static RobotController rc;

    static void init(RobotController rc)
    {
        Base.rc = rc;
        robotID = rc.getID();
        archonLocations = rc.getInitialArchonLocations(rc.getTeam());
        enemyArchonLocations = rc.getInitialArchonLocations(rc.getTeam().opponent());

        numberOfArchons = archonLocations.length;
        numberOfEnemyArchons = enemyArchonLocations.length;
    }

    static void update() {
        visibleFriendlyUnits = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
        visibleEnemyUnits = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());

        visibleNeutralTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL);
        visibleFriendlyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam());
        visibleEnemyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam().opponent());

        nearbyBullets = rc.senseNearbyBullets();

        tryUpdateEnemyArchonLocation();
    }

    static void tryUpdateEnemyArchonLocation() {
        for (RobotInfo robot : visibleEnemyUnits) {
            if (robot.getType() == RobotType.ARCHON) {
                lastKnownEnemyArchonLocation = robot.getLocation();
                break;
            }
        }
    }

    static boolean trySpawnUnit(Direction dir, RobotType type) throws GameActionException {
        return trySpawnUnit(dir, type, 20, 9);
    }

    static boolean trySpawnUnit(Direction dir, RobotType type, int offset, int checksPerSide) throws GameActionException {
        if (rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        }

        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            if (rc.canBuildRobot(type, dir.rotateLeftDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir.rotateLeftDegrees(currentCheck*offset));
                return true;
            }

            if (rc.canBuildRobot(type, dir.rotateRightDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir.rotateRightDegrees(currentCheck*offset));
                return true;
            }

            currentCheck++;
        }

        return false;
    }
}
