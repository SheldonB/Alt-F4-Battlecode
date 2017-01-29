package Alt_F4;

import battlecode.common.*;

public class Tank extends Base {

    public static void run() {
        while (true) {
            try {
                Base.update();
                Utils.CheckWinConditions();
                runRound();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void runRound() throws GameActionException {
        Pathing.tryMove(Pathing.randomDirection());
    }

}
