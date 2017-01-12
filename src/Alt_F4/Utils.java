package Alt_F4;

import battlecode.common.GameActionException;

public class Utils extends Base {
    public static void TurnInBullets() throws GameActionException {
        if (1000 - rc.getTeamVictoryPoints() <= rc.getTeamBullets() / 10) {
            rc.donate(rc.getTeamBullets());
        } else if (rc.getTeamBullets() > 200) {
            rc.donate(rc.getTeamBullets() - 100);
        }
    }
}
