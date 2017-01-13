package Alt_F4;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;

public class Scout extends Base {
    public static void run() throws GameActionException {
        while (true) {
            try {
                harassClosestEnemy();

                Clock.yield();
            } catch (Exception e) {
               System.out.println(e.getMessage());
            }
        }
    }

    static void harassClosestEnemy() throws GameActionException {
        RobotInfo enemies[] = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemies.length > 0) {
            RobotInfo closestEnemy = enemies[0];
            for (RobotInfo enemy : enemies) {
                if (rc.getLocation().distanceTo(enemy.getLocation()) < rc.getLocation().distanceTo(closestEnemy.getLocation())) {
                    closestEnemy = enemy;
                }
            }

            Pathing.tryMove(rc.getLocation().directionTo(closestEnemy.getLocation()));
            rc.fireSingleShot(rc.getLocation().directionTo(closestEnemy.getLocation()));
        }
    }
}
