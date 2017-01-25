package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;

public class Lumberjack extends Base {
    public static void run() throws GameActionException {
        while (true) {
            try {
                Base.update();
                clearTrees();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void clearTrees() throws GameActionException {
        Arrays.sort(visibleNeutralTrees, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(rc.getLocation()), o2.getLocation().distanceTo(rc.getLocation())));
        Arrays.sort(visibleFriendlyUnits, (o1, o2) -> Float.compare(o1.getLocation().distanceTo(rc.getLocation()), o2.getLocation().distanceTo(rc.getLocation())));

        if (rc.getLocation().distanceTo(visibleFriendlyUnits[0].getLocation()) < GameConstants.LUMBERJACK_STRIKE_RADIUS * 1.5) {
            Direction awayFromFriendly = rc.getLocation().directionTo(visibleFriendlyUnits[0].getLocation());
            awayFromFriendly = awayFromFriendly.rotateRightDegrees(180);
            Pathing.tryMove(awayFromFriendly);
        } else {
            if (visibleNeutralTrees[0].getRadius() + GameConstants.LUMBERJACK_STRIKE_RADIUS > rc.getLocation().distanceTo(visibleNeutralTrees[0].getLocation())) {
                rc.strike();
            } else {
                Direction toTree = rc.getLocation().directionTo(visibleNeutralTrees[0].getLocation());
                Pathing.tryMove(toTree);
                rc.setIndicatorLine(rc.getLocation(), visibleNeutralTrees[0].getLocation(), 0, 240, 251);
            }
        }
    }
}
