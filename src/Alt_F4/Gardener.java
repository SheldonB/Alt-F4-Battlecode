package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class Gardener extends Base {

    private static final int TREES_TO_SPAWN = 5;
    private static final int TIMES_TO_TRY_BEFORE_SETTLING = 100;

    private static int builtTreeCount;
    private static int timesTriedToBuild;

    private static boolean hasFoundBuildLocation = false;

    private static boolean shouldSpawnLumberJackWhenCan = false;
    private static boolean shouldSpawnSoldierWhenCan = false;
    private static boolean crampedLumberjack = false;

    private static List<Tree> builtTrees = new ArrayList<>();

    static void run() throws GameActionException {
        System.out.println("Gardener spawned");

        while (true) {
           try {
               Debug.debug_drawSensorRadius();
               Base.update();
               Utils.CheckWinConditions();
               runRound();
               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    private static void runRound() throws GameActionException  {
        if (crampedMap()) {
            if (!crampedLumberjack) {
                tryBuildLumberJack();
                crampedLumberjack = true;
            }
        } else {
            if (shouldBuildScout()) {
                tryBuildScout();
            }

            if (shouldBuildLumberJack() || shouldSpawnLumberJackWhenCan) {
                tryBuildLumberJack();
            }

            if (shouldBuildSoldier() || shouldSpawnSoldierWhenCan) {
                tryBuildSolider();
            }

            if (!hasFoundBuildLocation && !rc.hasMoved()) {
                tryMoveToValidBuildLocation();
            }

            if (hasFoundBuildLocation && builtTreeCount < TREES_TO_SPAWN && numberOfLumberjacks > 0) {
                tryPlantTree();
            }

            if (rc.canWater()) {
                tryWaterTree();
            }
        }
    }

    private static boolean crampedMap() throws GameActionException {
        System.out.println("I can sense " + visibleNeutralTrees.length + " trees!");
        if (visibleNeutralTrees.length >= 5 && rc.getRoundNum() <= 150) {
            return true;
        }
        return false;
    }

    private static boolean tryMoveToValidBuildLocation() throws GameActionException {
        if (!isValidBuildLocation(rc.getLocation())) {
            timesTriedToBuild++;
            return Pathing.tryRandomSmartMove();
            //return Pathing.tryMove(Pathing.randomDirection());
            //return Pathing.trySmartMove();
        }
        hasFoundBuildLocation = true;
        return false;
    }

    private static boolean isValidBuildLocation(MapLocation loc) throws GameActionException {
        return !rc.isCircleOccupiedExceptByThisRobot(loc, rc.getType().bodyRadius + (GameConstants.BULLET_TREE_RADIUS * 4)) || timesTriedToBuild > TIMES_TO_TRY_BEFORE_SETTLING;
    }

    private static boolean tryPlantTree() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (tryPlantTree(dir)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tryPlantTree(Direction dir) throws GameActionException {
        if (rc.canPlantTree(dir)) {
            rc.plantTree(dir);
            builtTreeCount++;
            // Add the closest tree to the built trees list
            builtTrees.add(new Tree(rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam())[0], rc.getRoundNum()));
            rc.broadcast(Message.TOTAL_TREE_COUNT_CHANNEL, numberOfTrees + 1);
            return true;
        }
        return false;
    }

    private static boolean tryWaterTree() throws GameActionException {
        TreeInfo[] sensedTrees = rc.senseNearbyTrees();
        Arrays.sort(sensedTrees, (o1, o2) -> Float.compare(o1.getHealth(), o2.getHealth()));

        for (TreeInfo tree : sensedTrees) {
            if (rc.canWater(tree.getID())
                    && tree.getHealth() <= tree.getMaxHealth() - GameConstants.BULLET_TREE_DECAY_RATE) {
                rc.water(tree.getID());
                return true;
            }
        }
        return false;
    }

    static boolean shouldBuildUnit() throws GameActionException {
        return shouldBuildScout() || shouldBuildLumberJack(); //|| shouldBuildScout();
    }

    static void tryBuildPriorityUnit() throws GameActionException {
        if (shouldBuildScout()) {
            tryBuildScout();
        } else if (shouldBuildLumberJack()) {
            tryBuildLumberJack();
        } else if (shouldBuildSoldier()) {
            tryBuildSolider();
        }
    }

    static boolean shouldBuildScout() throws GameActionException {
        if (numberOfScouts < 2) {
            return true;
        }
        return false;
    }

    static boolean tryBuildScout() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
                rc.buildRobot(RobotType.SCOUT, dir);
                rc.broadcast(Message.SCOUT_COUNT_CHANNEL, numberOfScouts + 1);
                return true;
            }
        }
        return false;
    }

    static boolean shouldBuildLumberJack() throws GameActionException {

        if ((!shouldBuildScout() && numberOfTrees == 0) || (builtTreeCount > 3 && numberOfLumberjacks < 10) || isLumberJackSpawnRound()) {
            if (rc.getTeamBullets() < RobotType.LUMBERJACK.bulletCost) {
                shouldSpawnLumberJackWhenCan = true;
            }
            return true;
        }

        return false;
    }

    static boolean tryBuildLumberJack() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
                rc.broadcast(Message.LUMBERJACK_COUNT_CHANNEL, numberOfLumberjacks + 1);
                shouldSpawnLumberJackWhenCan = false;
                return true;
            }
        }
        return false;
    }

    static boolean shouldBuildSoldier() throws GameActionException {
        if (isSoldierSpawnRound()) {
            if (rc.getTeamBullets() < RobotType.SOLDIER.bulletCost) {
                shouldSpawnSoldierWhenCan = true;
            }
            return true;
        }
        return false;
    }

    static boolean tryBuildSolider() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                rc.broadcast(Message.SOLDIER_COUNT_CHANNEL, numberOfSoldiers + 1);
                shouldSpawnSoldierWhenCan = false;
                return true;
            }
        }
        return false;
    }

    static boolean isLumberJackSpawnRound() {
        return rc.getRoundNum() % 50 == 0;
    }

    static boolean isSoldierSpawnRound() {
        return rc.getRoundNum() % 101 == 0;
    }
}
