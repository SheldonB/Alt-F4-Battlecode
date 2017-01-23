package Alt_F4;

import battlecode.common.GameActionException;

class Soldier extends Base {
    static void run() throws GameActionException {
        System.out.println("Solder has spawned.");

        while (true) {
            try {
                Utils.CheckWinConditions();
                Base.update();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
