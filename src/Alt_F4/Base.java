package Alt_F4;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

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
}
