package Alt_F4;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;

public class Utils extends Base {
    public static boolean CheckWinConditions() throws GameActionException {
        if (rc.getTeamBullets() >= GameConstants.VICTORY_POINTS_TO_WIN * GameConstants.BULLET_EXCHANGE_RATE) {
            rc.donate(rc.getTeamBullets());
            return true;
        }
        return false;
    }
}
