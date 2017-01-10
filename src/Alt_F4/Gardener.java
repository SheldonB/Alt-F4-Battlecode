package Alt_F4;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public class Gardener extends Base {
    public static void run() throws GameActionException {
        System.out.println("Gardener spawned");

        while (true) {
           try {
               Pathing.tryMove(Pathing.randomDirection());
               tryBuildLumberJack();
               Clock.yield();
           } catch (Exception e) {
               System.out.print(e.getMessage());
           }
        }
    }

    public static boolean tryBuildLumberJack() throws GameActionException {
        if(rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getSouth())) {
            rc.buildRobot(RobotType.LUMBERJACK, Direction.getSouth());
            return true;
        }
        return false;
    }
}
