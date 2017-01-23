package Alt_F4;

import battlecode.common.*;

import java.util.List;

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
    private static List<TreeInfo> builtTrees;

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
                   //if (rc.getRoundNum() > ) {
                   //    rc.resign();
                   //}
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
            return;
        }

        if (currentState == State.PLANTING) {
            tryPlantTree();
            return;
        }

        if (currentState == State.MOVING) {
            Pathing.tryMove(Pathing.randomDirection());
        }
    }

    private static void determineNextState() throws GameActionException {
        // Check if current target is within stride radius
        //if (currentTarget != null && currentTarget.getLocation().isWithinStrideDistance(rc.senseRobot(rc.getID()))) {
        //    if (currentTarget.getHealth() < GameConstants.BULLET_TREE_MAX_HEALTH - GameConstants.BULLET_TREE_DECAY_RATE) {
        //        currentState = State.WATERING;
        //        System.out.println("Switching State to Watering");
        //        return;
        //    }
        //}

        if (!rc.hasMoved()) {
            currentState = State.MOVING;
            System.out.println("Switching State to Moving");
            return;
        }

        if(rc.isBuildReady() && !hasTriedBuildThisTurn) {
            if (builtTreeCount < 5 && rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
                currentState = State.PLANTING;
                System.out.println("Switching State to Planting");
                return;
            }
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
            return true;
        }
        return false;
    }

    static boolean tryBuildLumberJack() throws GameActionException {
        if (rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getSouth())) {
            rc.buildRobot(RobotType.LUMBERJACK, Direction.getSouth());
            return true;
        }
        return false;
    }

    static boolean tryBuildScout() throws GameActionException {
        int angle = 0;
        int offset = 30;
        Direction dir = Direction.getEast();

        while (angle <= 360) {
            if (rc.canBuildRobot(RobotType.SCOUT, dir.rotateRightDegrees(angle))) {
                rc.buildRobot(RobotType.SCOUT, dir.rotateRightDegrees(angle));
                rc.broadcast(Message.SCOUT_COUNT_CHANNEL, rc.readBroadcast(Message.SCOUT_COUNT_CHANNEL) + 1);
                return true;
            }
            angle += offset;
        }

        return false;
    }
}
