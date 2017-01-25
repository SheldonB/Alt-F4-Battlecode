package Alt_F4;

import battlecode.common.*;

class Soldier extends Base {
    private static BodyInfo target;

    static void run() throws GameActionException {
        System.out.println("Soldier has spawned.");

        while (true) {
            try {
                Utils.CheckWinConditions();
                Base.update();

                if (!rc.hasMoved() && nearbyBullets.length > 0) {
                    Pathing.tryDodgeBullet();
                }

                target = determineTarget();

                if (!rc.hasMoved()) {
                    Pathing.tryMove(Pathing.randomDirection());
                }

                if (determineTarget() != null) {
                    tryFireOnTarget();
                }

                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    private static BodyInfo determineTarget() throws GameActionException {
        for (RobotInfo enemyRobot : visibleEnemyUnits) {
            if(!willShotCollideWithBody(enemyRobot)) {
                rc.setIndicatorLine(rc.getLocation(), enemyRobot.getLocation(), 0, 255, 0);
                return enemyRobot;
            }
        }

        for (TreeInfo tree : visibleNeutralTrees) {
            if(!willShotCollideWithBody(tree)) {
                rc.setIndicatorLine(rc.getLocation(), tree.getLocation(), 0, 255, 0);
                return tree;
            }
        }
        return null;
    }

    private static boolean willShotCollideWithBody(BodyInfo robotTarget) {
        Vector toEnemy = new Vector(rc.getLocation(), robotTarget.getLocation());

        for (RobotInfo friendlyRobot : visibleFriendlyUnits) {
            Vector toFriendly = new Vector(rc.getLocation(), friendlyRobot.getLocation());
            Vector projection = Vector.projection(toFriendly, toEnemy);

            if (projection.getEndPoint().distanceTo(friendlyRobot.getLocation()) <= friendlyRobot.getRadius()){
                System.out.println("Not firing, because will hit unit");
                return true;
            }
        }
        return false;
    }

    private static boolean tryFireOnTarget() throws GameActionException {
        if (rc.canFireSingleShot()) {
            rc.fireSingleShot(rc.getLocation().directionTo(target.getLocation()));
            System.out.println("Firing bullet");
            return true;
        }
        return false;
    }

}

