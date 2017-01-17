package Alt_F4;

import battlecode.common.*;

public class Base {
    public static int robotID;
    public static int numberOfArchons;
    public static int numberOfEnemyArchons;
    public static MapLocation[] archonLocations;
    public static MapLocation[] enemyArchonLocations;

    protected static RobotController rc;

    public static void init(RobotController rc)
    {
        Base.rc = rc;
        robotID = rc.getID();
        archonLocations = rc.getInitialArchonLocations(rc.getTeam());
        enemyArchonLocations = rc.getInitialArchonLocations(rc.getTeam().opponent());

        numberOfArchons = archonLocations.length;
        numberOfEnemyArchons = enemyArchonLocations.length;
    }

    protected static boolean trySpawnUnit(Direction dir, RobotType type) throws GameActionException {
        return trySpawnUnit(dir, type, 20, 9);
    }

    protected static boolean trySpawnUnit(Direction dir, RobotType type, int offset, int checksPerSide) throws GameActionException {
        if (rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        }

        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            if (rc.canBuildRobot(type, dir.rotateLeftDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir);
                return true;
            }

            if (rc.canBuildRobot(type, dir.rotateRightDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir);
                return true;
            }

            currentCheck++;
        }

        return false;
    }
}
