package Alt_F4;

import battlecode.common.*;
import java.util.List;
import java.util.ArrayList;

class Utils extends Base {
    public static final float PI = 3.1415927F;

    static MapLocation closestEnemyArchonLocation() {
        MapLocation closestArchonLocation = enemyArchonLocations[0];

        for (MapLocation loc : enemyArchonLocations) {
            if (rc.getLocation().distanceTo(loc) < rc.getLocation().distanceTo(closestArchonLocation)) {
                closestArchonLocation = loc;
            }
        }

        return closestArchonLocation;
    }

    static boolean CheckWinConditions() throws GameActionException {
        if (rc.getRoundNum() == 3000) {
            rc.donate(rc.getTeamBullets());
        }

        if (rc.getTeamBullets() / rc.getVictoryPointCost() >= GameConstants.VICTORY_POINTS_TO_WIN) {
            rc.donate(rc.getTeamBullets());
            return true;
        }
        return false;
    }

    static void collectBullets() throws GameActionException {
        for (TreeInfo tree : visibleNeutralTrees) {
            if (rc.canShake(tree.getID())) {
                rc.shake(tree.getID());
                break;
            }
        }
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

    static strictfp List<Direction> computeDirections(Direction initDirection, float offset) {
        List<Direction> computedDirections = new ArrayList<>();
        float rotationAngle = 0F;

        while (rotationAngle < 2F * Utils.PI) {
            computedDirections.add(initDirection.rotateRightRads(rotationAngle));
            rotationAngle += offset;
        }

        return computedDirections;
    }
}
