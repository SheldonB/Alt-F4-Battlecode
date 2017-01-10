package Alt_F4;

import battlecode.common.RobotController;

public class Base {
    public static int numberOfArchons;
    public static int numberOfEnemyArchons;
    protected static RobotController rc;

    public static void init(RobotController rc)
    {
        Base.rc = rc;
        numberOfArchons = rc.getInitialArchonLocations(rc.getTeam()).length;
        numberOfEnemyArchons = rc.getInitialArchonLocations(rc.getTeam().opponent()).length;
    }
}
