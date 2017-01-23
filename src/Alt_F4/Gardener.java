package Alt_F4;

import battlecode.common.*;

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

    private static int builtTreeCount;
    private static State currentState;
    private static boolean hasTriedBuildThisTurn;
    private static TreeInfo wateringTarget;
    private static List<Tree> builtTrees = new ArrayList<>();

    static void run() throws GameActionException {
        System.out.println("Gardener spawned");
        currentState = null;
        while (true) {
           try {
               Base.update();
               Utils.CheckWinConditions();
               hasTriedBuildThisTurn = false;

               while (currentState != State.END_TURN) {
                   executeCurrentState();
                   determineNextState();
                   if (rc.getRoundNum() > 100) {
                       rc.resign();
                   }
               }

               currentState = null;
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

        if (currentState == State.WATERING) {
            tryWaterTargetTree();
            return;
        }

        if (currentState == State.PLANTING) {
            tryPlantTree();
            return;
        }

        if (currentState == State.MOVING && !rc.hasMoved()) {
            if (wateringTarget != null) {
                Pathing.tryMove(rc.getLocation().directionTo(wateringTarget.getLocation()));
                return;
            }
            Pathing.tryMove(Pathing.randomDirection());
        }
    }

    private static void determineNextState() throws GameActionException {

        if(rc.isBuildReady() && !hasTriedBuildThisTurn) {
            if (builtTreeCount < 5 && rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
                currentState = State.PLANTING;
                System.out.println("Switching State to Planting");
                return;
            }
        }

        if (builtTrees.size() > 0 && rc.canWater()) {
            for (Tree tree : builtTrees) {
                if (tree.isFullyMatured() && tree.shouldBeWatered()) {
                    if (tree.getTreeInfo().getLocation().isWithinStrideDistance(rc.senseRobot(rc.getID()))) {
                        //wateringTarget = tree;
                        currentState = State.WATERING;
                        System.out.println("Switching state to watering");
                        return;
                    } else {
                        //wateringTarget = tree;
                        currentState = State.MOVING;
                        return;
                    }
                }
            }
        }

        if (!rc.hasMoved()) {
            currentState = State.MOVING;
            System.out.println("Switching State to Moving");
            return;
        }

        // If we cant go to any other states, we are done
        // with this turn
        currentState = State.END_TURN;
        System.out.println("Switching state to End Turn");
    }

    private static boolean tryPlantTree() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Pathing.randomDirection(), 15);
        TreeInfo[] sensedTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam());

        hasTriedBuildThisTurn = true;

        for (Direction dir : directions) {
            if (sensedTrees.length == 0 && rc.canPlantTree(dir)) {
                return tryPlantTree(dir);
            } else {
                for (TreeInfo tree : sensedTrees) {
                    if (tree.getLocation().distanceTo(rc.getLocation().add(dir, rc.getType().bodyRadius
                            + GameConstants.BULLET_TREE_RADIUS)) <= GameConstants.BULLET_TREE_RADIUS * 4F) {
                        return false;
                    }
                }
                return tryPlantTree(dir);
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
            return true;
        }
        return false;
    }

    private static boolean tryWaterTargetTree() throws GameActionException {
        if (rc.canWater()) {
            rc.water(wateringTarget.getID());
            wateringTarget = null;
            return true;
        }
        return false;
    }

    static boolean tryBuildLumberJack() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 6);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
                return true;
            }
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
}
