package Alt_F4;

import battlecode.common.*;

import java.util.Arrays;


public class Archon extends Base {
    private static final int GARDENERS_TO_SPAWN = 100;
    private static int spawnedUnitCount = 0;

    public static void run() throws GameActionException {
        System.out.println("Archon Spawned");

        while (true) {
            try {
                Base.update();
                if (rc.getRoundNum() == 1) {
                    VoteMapStrategy();
                } else if (rc.getRoundNum() == 2) {
                    DetermineMapStrategy();
                }

                if (isGardenerSpawnRound()) {
                    spawnGardener();
                }

                tryMaintainDistance();

                Utils.CheckWinConditions();
                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /*
    This function checks to see if the current map has a valid
    turtle strategy. A valid turtle startegy comes from a map
    that has 2 or more archons, and a proper turtle gardner
    hexagon can be built.
     */
    public static boolean CheckValidTurtleMap() {
        if (numberOfArchons == 1) {
            return false;
        }

        TreeInfo[] sensedTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL);
        return sensedTrees.length <= 1;
    }


    private static void VoteMapStrategy() throws GameActionException {
        int[] hashedLocations = new int[numberOfArchons];
        for (int i = 0; i < numberOfArchons; i++) {
            hashedLocations[i] = Utils.mapLocationToInt(archonLocations[i]);
        }

        Arrays.sort(hashedLocations);

        int offset = 0;
        for (int i = 0; i < numberOfArchons; i++) {
            if (hashedLocations[i] == Utils.mapLocationToInt(rc.getLocation())) {
                offset = i;
                break;
            }
        }

        if (CheckValidTurtleMap()) {
            rc.broadcast(Message.ARCHON_VOTING_START_CHANNEL + offset, Message.TURTLE_MESSAGE);
        } else {
            rc.broadcast(Message.ARCHON_VOTING_START_CHANNEL + offset, Message.SCOUT_RUSH_MESSAGE);
        }
    }

    private static void DetermineMapStrategy() throws GameActionException {
        int aggregate = 0;
        for (int i = 0; i < numberOfArchons; i++) {
            aggregate += rc.readBroadcast(Message.ARCHON_VOTING_START_CHANNEL + i);
        }

        if (aggregate != numberOfArchons) {
            rc.broadcast(Message.STRATEGY_CHANNEL, Message.SCOUT_RUSH_MESSAGE);
            System.out.println("Archons decided to scout rush");
            return;
        }

        System.out.println("Archons decided to turtle");
        rc.broadcast(Message.STRATEGY_CHANNEL, Message.TURTLE_MESSAGE);
    }

    private static boolean isGardenerSpawnRound() {
        return rc.getRoundNum() % 100 == 0 || rc.getRoundNum() == 1;
    }

    public static void spawnGardener() throws GameActionException {
        Direction buildDirection = Direction.getNorth();
        buildDirection = buildDirection.rotateRightDegrees(60 * spawnedUnitCount);

        if (spawnedUnitCount < GARDENERS_TO_SPAWN && Base.trySpawnUnit(buildDirection, RobotType.GARDENER)) {
            rc.broadcast(Message.GARDENER_COUNT_CHANNEL, rc.readBroadcast(Message.GARDENER_COUNT_CHANNEL));
            spawnedUnitCount++;
        }
    }

    private static void tryMaintainDistance() throws GameActionException {
        for (RobotInfo robot : visibleFriendlyUnits) {
            if (rc.getLocation().distanceTo(robot.getLocation()) < GameConstants.BULLET_TREE_RADIUS * 4) {
                MapLocation newLoc = rc.getLocation().add(rc.getLocation().directionTo(robot.getLocation()).opposite());
                if (!rc.hasMoved() && rc.canMove(newLoc)) {
                    rc.move(newLoc);
                }
            }
        }
    }
}
