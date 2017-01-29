package Alt_F4;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public strictfp class RobotPlayer {
    public static void run(RobotController rc) throws GameActionException {
        Base.init(rc);
        try {
            switch (rc.getType()) {
                case ARCHON:
                    Archon.run();
                    break;
                case GARDENER:
                    Gardener.run();
                    break;
                case LUMBERJACK:
                    Lumberjack.run();
                    break;
                case SCOUT:
                    Scout.run();
                    break;
                case SOLDIER:
                    Soldier.run();
                    break;
                case TANK:
                    Tank.run();
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
