package Alt_F4;

import battlecode.common.Clock;
import battlecode.common.GameActionException;

public class Scout extends Base {
    public static void run() throws GameActionException {
        while (true) {
            try {
                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }
}
