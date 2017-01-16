package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;

public class Gardener extends Base {

    private enum Role {
        FARMER,
        BUILDER
    }

    private static Role unitRole;
    private static boolean foundBuildLocation = false;

    public static void run() throws GameActionException {
        System.out.println("Gardener spawned");
        unitRole = determineRole();

        while (true) {
           try {
               Utils.CheckWinConditions();

               if (unitRole == Role.BUILDER) {
                   tryBuildScout();
               } else if (unitRole == Role.FARMER) {
                   hexagonBuild();
               }


               Clock.yield();
           } catch (Exception e) {
               System.out.println(e.getMessage());
           }
        }
    }

    private static Role determineRole() throws GameActionException {
        if (rc.readBroadcast(Message.STRATEGY_CHANNEL) == Message.SCOUT_RUSH_MESSAGE) {
            System.out.println("Gardener set to builder role");
            return Role.BUILDER;
        }
        System.out.println("Gardener set to farmer role");
        return Role.FARMER;
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
        int angle = 0;
        int offset = 90;
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

    public static void hexagonBuild() throws GameActionException {
        if (!foundBuildLocation) {
            System.out.println("Trying to find better place to build");

            if (isValidFullTreeCircle()) {
                foundBuildLocation = true;
            } else {
                boolean movingAway = true;
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
