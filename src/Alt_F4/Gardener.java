package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class Gardener extends Base {

    private enum State {
        MOVING,
        PLANTING,
        WATERING,
        BUILDING,
        END_TURN
    }

    private static final int TREES_TO_SPAWN = 5;

    private static int builtTreeCount;
    private static State currentState;

    private static boolean hasFoundBuildLocation = false;
    private static boolean hasSpawnedScout = false;

    private static List<Tree> builtTrees = new ArrayList<>();

    static void run() throws GameActionException {
        System.out.println("Gardener spawned");


        while (true) {
           try {
               Debug.debug_drawSensorRadius();
               Base.update();
               Utils.CheckWinConditions();

               currentState = null;
               while (currentState != State.END_TURN) {
                   executeCurrentState();
                   determineNextState();
                   //if (rc.getRoundNum() > 500) {
                   //    rc.resign();
                   //}
               }

               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    private static void executeCurrentState() throws GameActionException {
        if (currentState == null || currentState == State.END_TURN) {
            return;
        }

        if (currentState == State.BUILDING) {
            tryBuildPriorityUnit();
        }

        if (currentState == State.WATERING) {
            tryWaterTree();
            return;
        }

        if (currentState == State.PLANTING) {
            tryPlantTree();
            return;
        }

        if (currentState == State.MOVING) {
            tryMoveToValidBuildLocation();
        }

    }

    private static void determineNextState() throws GameActionException {

        if(rc.isBuildReady()) {
            if (shouldBuildUnit()) {
                currentState = State.BUILDING;
                System.out.println("Switching State to Building");
                return;
            }

            if (hasFoundBuildLocation && builtTreeCount < TREES_TO_SPAWN && rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
                currentState = State.PLANTING;
                System.out.println("Switching State to Planting");
                return;
            }
        }

        if (builtTreeCount > 0 && rc.canWater()) {
            currentState =  State.WATERING;
            System.out.println("Switching state to watering");
            return;
        }

        if (!rc.hasMoved() && !hasFoundBuildLocation) {
            currentState = State.MOVING;
            System.out.println("Switching State to Moving");
            return;
        }

        // If we cant go to any other states, we are done
        // with this turn
        currentState = State.END_TURN;
        System.out.println("Switching state to End Turn");
    }

    private static boolean tryMoveToValidBuildLocation() throws GameActionException {
        if (!isValidBuildLocation(rc.getLocation())) {
            return Pathing.tryMove(Pathing.randomDirection());
        }

        hasFoundBuildLocation = true;
        return false;
    }

    private static boolean isValidBuildLocation(MapLocation loc) throws GameActionException {
        return !rc.isCircleOccupiedExceptByThisRobot(loc, rc.getType().bodyRadius + (GameConstants.BULLET_TREE_RADIUS * 2));
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
            rc.broadcast(Message.TOTAL_TREE_COUNT_CHANNEL, rc.readBroadcast(Message.TOTAL_TREE_COUNT_CHANNEL) + 1);
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
        return shouldBuildScout() || shouldBuildLumberJack() || shouldBuildScout();
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

    static boolean shouldBuildLumberJack() throws GameActionException {
        if (rc.readBroadcast(Message.LUMBERJACK_COUNT_CHANNEL) < 10) {
            return true;
        }
        return false;
    }

    static boolean tryBuildLumberJack() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
                rc.broadcast(Message.LUMBERJACK_COUNT_CHANNEL, rc.readBroadcast(Message.LUMBERJACK_COUNT_CHANNEL) + 1);
                return true;
            }
        }
        return false;
    }

    static boolean shouldBuildScout() throws GameActionException {
        if (rc.readBroadcast(Message.SCOUT_COUNT_CHANNEL) < 2) {
            return true;
        }
        return false;
    }

    static boolean tryBuildScout() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
                rc.buildRobot(RobotType.SCOUT, dir);
                rc.broadcast(Message.SCOUT_COUNT_CHANNEL, rc.readBroadcast(Message.SCOUT_COUNT_CHANNEL) + 1);
                return true;
            }
        }
        return false;
    }


    static boolean shouldBuildSoldier() throws GameActionException {
        return rc.readBroadcast(Message.SOLDIER_COUNT_CHANNEL) < 15;
    }

    static boolean tryBuildSolider() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                rc.broadcast(Message.SOLDIER_COUNT_CHANNEL, rc.readBroadcast(Message.SOLDIER_COUNT_CHANNEL) + 1);
                return true;
            }
        }
        return false;
    }
}
