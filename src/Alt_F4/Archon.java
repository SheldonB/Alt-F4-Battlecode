package Alt_F4;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Archon extends Base {
    private static int spawnedUnitCount = 0 ;

    public static void run() throws GameActionException {
        System.out.println("Archon Spawned");

        while (true) {
            try {
                spawnGardener();
                Pathing.tryMove(Pathing.randomDirection());
                Utils.CheckWinConditions();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void spawnGardener() throws GameActionException {
        if (rc.canHireGardener(Direction.getNorth()) && spawnedUnitCount < 6) {
            rc.hireGardener(Direction.getNorth());
            spawnedUnitCount++;
        }
    }
}
