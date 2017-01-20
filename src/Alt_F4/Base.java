package Alt_F4;

import battlecode.common.*;

public class Base {
    static int robotID;
    static int numberOfArchons;
    static int numberOfEnemyArchons;

    static MapLocation[] archonLocations;
    static MapLocation[] enemyArchonLocations;

    static MapLocation lastKnownEnemyArchonLocation;

    static RobotInfo[] visibleFriendlyUnits;
    static RobotInfo[] visibleEnemyUnits;

    static TreeInfo[] visibleNeutralTrees;
    static TreeInfo[] visibleFriendlyTrees;
    static TreeInfo[] visibleEnemyTrees;

    static BulletInfo[] nearbyBullets;


    static float maxKnownXLoc;
    static float minKnownXLoc;

    static float maxKnownYLoc;
    static float minKnownYLoc;

    protected static RobotController rc;

    static void init(RobotController rc)
    {
        Base.rc = rc;
        robotID = rc.getID();
        archonLocations = rc.getInitialArchonLocations(rc.getTeam());
        enemyArchonLocations = rc.getInitialArchonLocations(rc.getTeam().opponent());

        numberOfArchons = archonLocations.length;
        numberOfEnemyArchons = enemyArchonLocations.length;
    }

    static void update() throws GameActionException {
        visibleFriendlyUnits = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
        visibleEnemyUnits = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());

        visibleNeutralTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL);
        visibleFriendlyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam());
        visibleEnemyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam().opponent());

        nearbyBullets = rc.senseNearbyBullets();

        tryUpdateEnemyArchonLocation();
        tryUpdateKnownMapEdges();
    }

    static void tryUpdateEnemyArchonLocation() throws GameActionException {
        lastKnownEnemyArchonLocation = Utils.mapLocationFromInt(rc.readBroadcast(Message.LAST_KNOW_ENEMY_ARCHON_CHANNEL));

        for (RobotInfo robot : visibleEnemyUnits) {
            if (robot.getType() == RobotType.ARCHON) {
                lastKnownEnemyArchonLocation = robot.getLocation();
                break;
            }
        }

        rc.broadcast(Message.LAST_KNOW_ENEMY_ARCHON_CHANNEL, Utils.mapLocationToInt(lastKnownEnemyArchonLocation));
    }

    static void tryUpdateKnownMapEdges() throws GameActionException {
        float maxXLoc = rc.readBroadcast(Message.MAX_KNOWN_X_INT_LOCATION_CHANNEL)
                + Utils.toPostDecimal(rc.readBroadcast(Message.MAX_KNOWN_X_FLOAT_LOCATION_CHANNEL));
        float minXLoc = rc.readBroadcast(Message.MIN_KNOWN_X_INT_LOCATION_CHANNEL)
                + Utils.toPostDecimal(rc.readBroadcast(Message.MIN_KNOWN_X_FLOAT_LOCATION_CHANNEL  ));
        float maxYLoc = rc.readBroadcast(Message.MAX_KNOWN_Y_INT_LOCATION_CHANNEL)
                + Utils.toPostDecimal(Message.MAX_KNOWN_Y_FLOAT_LOCATION_CHANNEL);
        float minYLoc = rc.readBroadcast(Message.MIN_KNOWN_Y_INT_LOCATION_CHANNEL)
                + Utils.toPostDecimal(rc.readBroadcast(Message.MIN_KNOWN_Y_FLOAT_LOCATION_CHANNEL));

        float currentX = rc.getLocation().x;
        float currentY = rc.getLocation().y;

        if (currentX > maxXLoc) {
            rc.broadcast(Message.MAX_KNOWN_X_INT_LOCATION_CHANNEL, Utils.getPreDecimal(currentX));
            rc.broadcast(Message.MAX_KNOWN_X_FLOAT_LOCATION_CHANNEL, Utils.getPostDecimal(currentX));
            maxKnownXLoc = currentX;
        } else if (currentX < minXLoc) {
            rc.broadcast(Message.MIN_KNOWN_X_INT_LOCATION_CHANNEL, Utils.getPreDecimal(currentX));
            rc.broadcast(Message.MIN_KNOWN_X_FLOAT_LOCATION_CHANNEL, Utils.getPostDecimal(currentX));
            minKnownXLoc = currentX;
        }

        if (currentY > maxYLoc) {
            rc.broadcast(Message.MAX_KNOWN_Y_INT_LOCATION_CHANNEL, Utils.getPreDecimal(currentY));
            rc.broadcast(Message.MAX_KNOWN_Y_FLOAT_LOCATION_CHANNEL, Utils.getPostDecimal(currentY));
            maxKnownYLoc = currentY;
        } else if (currentY < minYLoc) {
            rc.broadcast(Message.MIN_KNOWN_Y_INT_LOCATION_CHANNEL, Utils.getPreDecimal(currentY));
            rc.broadcast(Message.MIN_KNOWN_Y_FLOAT_LOCATION_CHANNEL, Utils.getPostDecimal(currentY));
            minKnownYLoc = currentY;
        }
    }

    static boolean trySpawnUnit(Direction dir, RobotType type) throws GameActionException {
        return trySpawnUnit(dir, type, 20, 9);
    }

    static boolean trySpawnUnit(Direction dir, RobotType type, int offset, int checksPerSide) throws GameActionException {
        if (rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        }

        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            if (rc.canBuildRobot(type, dir.rotateLeftDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir.rotateLeftDegrees(currentCheck*offset));
                return true;
            }

            if (rc.canBuildRobot(type, dir.rotateRightDegrees(currentCheck*offset))) {
                rc.buildRobot(type, dir.rotateRightDegrees(currentCheck*offset));
                return true;
            }

            currentCheck++;
        }

        return false;
    }
}
