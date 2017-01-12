package Alt_F4;

import battlecode.common.*;

public class Gardener extends Base {
    public static void run() throws GameActionException {
        System.out.println("Gardener spawned");

        while (true) {
           try {
               //Pathing.tryMove(Pathing.randomDirection());
               //tryBuildLumberJack();
               //tryBuildTree(Pathing.randomDirection());
               //tryShakeTrees();
               tryBuildHexagon();
               tryShakeWaterHexagon();
               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    public static void tryBuildHexagon() throws GameActionException {
        Direction buildDirection = Direction.getEast();
        int treesToTry = 6;
        int offset = 60;

        int treesTried = 0;

        while (treesTried <= treesToTry) {
            if (rc.canPlantTree(buildDirection.rotateRightDegrees(treesTried * offset))) {
                rc.plantTree(buildDirection.rotateRightDegrees(treesTried * offset));
            }
            treesTried++;
        }
    }

    public static void tryShakeWaterHexagon() throws GameActionException {
        TreeInfo[] sensedTrees = rc.senseNearbyTrees();

        for (TreeInfo tree : sensedTrees) {
            if (rc.canShake(tree.getID())) {
                rc.shake(tree.getID());
            }
            if(rc.canWater(tree.getID()) && tree.getHealth() <= 15) {
                rc.water(tree.getID());
            }
        }
    }

    public static boolean tryBuildLumberJack() throws GameActionException {
        if(rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getSouth())) {
            rc.buildRobot(RobotType.LUMBERJACK, Direction.getSouth());
            return true;
        }
        return false;
    }
}
