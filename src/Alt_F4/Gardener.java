package Alt_F4;

import battlecode.common.*;

public class Gardener extends Base {
    public static void run() throws GameActionException {
        System.out.println("Gardener spawned");
        boolean foundBuildLocation = false;

        while (true) {
           try {
               Utils.CheckWinConditions();

               if (!foundBuildLocation) {
                   System.out.println("Trying to find better place to build");

                   if (isValidFullTreeCircle()) {
                       foundBuildLocation = true;
                   } else {
                       Pathing.tryMove(Pathing.randomDirection());
                   }
               } else {
                   tryBuildHexagon();
                   tryWaterNearbyTrees();
               }

               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    public static boolean isValidFullTreeCircle() throws GameActionException {
        Direction buildDirection = Direction.getEast();
        int treesToTry = 6;
        int offset = 60;

        int treesTried = 0;

        while (treesTried <= treesToTry) {
            if (!rc.canPlantTree(buildDirection.rotateRightDegrees(treesTried * offset))) {
                return false;
            }
            treesTried++;
        }
        return true;
    }

    public static void tryBuildHexagon() throws GameActionException {
        Direction buildDirection = Direction.getEast();
        int treesToTry = 6;
        int offset = 60;

        int treesTried = 0;

        while (treesTried <= treesToTry) {
            if (rc.canPlantTree(buildDirection.rotateRightDegrees(treesTried * offset))) {
                rc.plantTree(buildDirection.rotateRightDegrees(treesTried * offset));
                System.out.println("Gardener is planting new tree.");
            }
            treesTried++;
        }
    }

    public static void tryWaterNearbyTrees() throws GameActionException {
        TreeInfo[] sensedTrees = rc.senseNearbyTrees();

        for (TreeInfo tree : sensedTrees) {
            if(rc.canWater(tree.getID()) && tree.getHealth() <= 25) {
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
