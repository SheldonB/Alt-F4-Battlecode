package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;

public class Gardener extends Base {

    private static boolean movingAway = true;
    private static boolean foundBuildLocation = false;

    public static void run() throws GameActionException {
        System.out.println("Gardener spawned");
        while (true) {
           try {
               Utils.CheckWinConditions();

               if (rc.senseNearbyRobots(-1, rc.getTeam().opponent()).length > 0) {
                   tryBuildScout();
               } else {
                   hexagonBuild();
               }
               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    public static boolean isValidFullTreeCircle() throws GameActionException {
        return !rc.isCircleOccupiedExceptByThisRobot(rc.getLocation(), RobotType.GARDENER.bodyRadius + (float) (3.05 * GameConstants.BULLET_TREE_RADIUS));
    }

    public static void tryBuildHexagon() throws GameActionException {
        Direction buildDirection = Direction.getEast();
        int treesToTry = 6;
        int offset = 60;

        int treesTried = 0;

        while (treesTried < treesToTry) {
            if (rc.canPlantTree(buildDirection.rotateRightDegrees(treesTried * offset))) {
                rc.plantTree(buildDirection.rotateRightDegrees(treesTried * offset));
                System.out.println("Gardener is planting new tree.");
                break;
            }
            treesTried++;
        }
    }

    public static void tryWaterNearbyTrees() throws GameActionException {
        TreeInfo[] sensedTrees = rc.senseNearbyTrees();
        Arrays.sort(sensedTrees, (o1, o2) -> Float.compare(o1.getHealth(), o2.getHealth()));

        for (TreeInfo tree : sensedTrees) {
            if (rc.canWater(tree.getID()) && tree.getHealth() <= GameConstants.BULLET_TREE_MAX_HEALTH - GameConstants.BULLET_TREE_DECAY_RATE) {
                rc.water(tree.getID());
                break;
            }
        }
    }

    public static boolean tryBuildLumberJack() throws GameActionException {
        if (rc.canBuildRobot(RobotType.LUMBERJACK, Direction.getSouth())) {
            rc.buildRobot(RobotType.LUMBERJACK, Direction.getSouth());
            return true;
        }
        return false;
    }

    public static boolean tryBuildScout() throws GameActionException {
        if (rc.canBuildRobot(RobotType.SCOUT, Direction.getEast())) {
            rc.buildRobot(RobotType.SCOUT, Direction.getEast());
            return true;
        } else if (rc.canBuildRobot(RobotType.SCOUT, Direction.getWest())) {
            rc.buildRobot(RobotType.SCOUT, Direction.getWest());
            return true;
        }else if (rc.canBuildRobot(RobotType.SCOUT, Direction.getNorth())) {
            rc.buildRobot(RobotType.SCOUT, Direction.getNorth());
            return true;
        }else if (rc.canBuildRobot(RobotType.SCOUT, Direction.getSouth())) {
            rc.buildRobot(RobotType.SCOUT, Direction.getSouth());
            return true;
        }
        return false;
    }

    public static void hexagonBuild() throws GameActionException {
        if (!foundBuildLocation) {
            System.out.println("Trying to find better place to build");

            if (isValidFullTreeCircle()) {
                foundBuildLocation = true;
            } else {
                if (movingAway) {
                    MapLocation closestArchon = archonLocations[0];

                    for (MapLocation location : archonLocations) {
                        if (rc.getLocation().distanceTo(location) < rc.getLocation().distanceTo(closestArchon)) {
                            closestArchon = location;
                        }
                    }

                    Pathing.tryMove(rc.getLocation().directionTo(closestArchon).rotateRightDegrees(180));
                } else {
                    Pathing.tryMove(Pathing.randomDirection());
                }
            }
        } else {
            tryBuildHexagon();
            tryWaterNearbyTrees();
        }
    }
}
