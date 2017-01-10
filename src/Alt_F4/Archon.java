package Alt_F4;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class Archon extends Base {
    public static void run() throws GameActionException {
        while (true) {
            try {
                if (rc.canHireGardener(Direction.getNorth())) {
                    rc.hireGardener(Direction.getNorth());
                }

                Pathing.tryMove(Pathing.randomDirection());

                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
