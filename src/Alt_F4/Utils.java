package Alt_F4;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

class Utils extends Base {
    static boolean CheckWinConditions() throws GameActionException {
        if (rc.getTeamBullets() >= GameConstants.VICTORY_POINTS_TO_WIN * GameConstants.BULLET_EXCHANGE_RATE) {
            rc.donate(rc.getTeamBullets());
            return true;
        }
        return false;
    }

    static int getPreDecimal(float data) {
        return Math.round(data - (data % 1));
    }

    static int getPostDecimal(float data)  {
        return Math.toIntExact(Math.round((data % 1) * Math.pow(10, 6)));
    }

    static float toPostDecimal(int data) {
        return (float)(data / Math.pow(10, 6));
    }

    static int mapLocationToInt(MapLocation loc)  {
        if (loc == null) return 0xfffff;
        return (((int)loc.x) << 10) | ((int)loc.y);
    }

    static MapLocation mapLocationFromInt(int data) {
        if ((data & 0xfffff) == 0xfffff) return null;
        int x = ((data & 0xffc00) >>> 10);
        int y = (data & 0x003ff);
        return new MapLocation(x, y);
    }
}
