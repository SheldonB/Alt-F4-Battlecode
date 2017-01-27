package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class Gardener extends Base {

    private static Direction movingDirection;

    private static final int TREES_TO_SPAWN = 5;

    private static int builtTreeCount;

    private static boolean hasFoundBuildLocation = false;

    private static boolean crampedLumberjack = false;

    private static int lastSpawnedLumberjackRound;
    private static int lastSpawnedSoldierRound;

    private static List<Tree> builtTrees = new ArrayList<>();

    static void run() throws GameActionException {
        System.out.println("Gardener spawned");

        while (true) {
           try {
               //Debug.debug_drawSensorRadius();
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
        if (canRush()) {
            tryBuildSolider();
        }
        if (crampedMap()) {
            if (!crampedLumberjack) {
                tryBuildLumberJack();
                crampedLumberjack = true;
            }
        } else {
            if (shouldBuildScout()) {
                tryBuildScout();
            }

            if (shouldBuildLumberJack()) {
                tryBuildLumberJack();
            }

            if (shouldBuildSoldier()) {
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
        if (visibleNeutralTrees.length >= 5 && rc.getRoundNum() <= 150) {
            return true;
        }
        return false;
    }

    private static boolean canRush() throws GameActionException {
        Arrays.sort(enemyArchonLocations, (loc1, loc2) -> Float.compare(rc.getLocation().distanceTo(loc1), rc.getLocation().distanceTo(loc2)));
        return rc.getLocation().distanceTo(enemyArchonLocations[0]) <= 20;
    }

    private static boolean tryMoveToValidBuildLocation() throws GameActionException {
        if (!isValidBuildLocation(rc.getLocation())) {
            if (movingDirection == null) {
                movingDirection = rc.getLocation().directionTo(enemyArchonLocations[0]);
            }

            if (!rc.canMove(movingDirection)) {
                movingDirection = Pathing.randomDirection();
            }

            return Pathing.tryMove(movingDirection);
        }
        hasFoundBuildLocation = true;
        return false;
    }

    private static boolean isValidBuildLocation(MapLocation loc) throws GameActionException {
        float circleRadius = rc.getType().bodyRadius + (GameConstants.BULLET_TREE_RADIUS * 4);
        return rc.senseNearbyTrees(circleRadius).length == 0 && rc.onTheMap(loc, circleRadius);
    }

    private static boolean tryPlantTree() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 3);
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

    static boolean shouldBuildScout() throws GameActionException {
        if (numberOfScouts < 2) {
            return true;
        }
        return false;
    }

    static boolean tryBuildScout() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 3);

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
        if (numberOfSoldiers < 2)  {
            return false;
        }

        if (numberOfLumberjacks > 30)  {
            return false;
        }

        if (isLumberJackSpawnRound()) {
            return true;
        }

        if (numberOfScouts >= 2) {
            return true;
        }

        if (builtTreeCount > 2) {
            return true;
        }

        return false;
    }

    static boolean tryBuildLumberJack() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 3);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK, dir);
                rc.broadcast(Message.LUMBERJACK_COUNT_CHANNEL, numberOfLumberjacks + 1);
                lastSpawnedLumberjackRound = rc.getRoundNum();
                return true;
            }
        }
        return false;
    }

    static boolean shouldBuildSoldier() throws GameActionException {
        if (isSoldierSpawnRound()) {
            return true;
        }

        if (numberOfSoldiers < Constants.SOLDIERS_FOR_AGGRESSION) {
            return true;
        }

        return false;
    }

    static boolean tryBuildSolider() throws GameActionException {
        List<Direction> directions = Utils.computeDirections(Direction.getEast(), Utils.PI / 3);

        for (Direction dir : directions) {
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                rc.broadcast(Message.SOLDIER_COUNT_CHANNEL, numberOfSoldiers + 1);
                lastSpawnedSoldierRound = rc.getRoundNum();
                return true;
            }
        }
        return false;
    }

    static boolean isLumberJackSpawnRound() {
        return (rc.getRoundNum() - lastSpawnedLumberjackRound) >= Constants.LUMBERJACK_SPAWN_OFFSET;
    }

    static boolean isSoldierSpawnRound() {
        return (rc.getRoundNum() - lastSpawnedSoldierRound) >= Constants.SOLDIER_SPAWN_OFFSET;
    }
}
