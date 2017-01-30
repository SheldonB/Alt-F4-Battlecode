package Alt_F4;

import battlecode.common.*;

public class Broadcasting extends Base {

    static boolean tryBroadcastPacket(Packet packet) throws GameActionException {
        int emptyStartingChannel = findEmptyChannel();

        if (emptyStartingChannel == GameConstants.BROADCAST_MAX_CHANNELS) {
            return false;
        }

        return tryBroadcastPacket(packet, emptyStartingChannel);
    }

    static boolean tryBroadcastPacket(Packet packet, int startingChannel) throws GameActionException {
        rc.broadcast(startingChannel, packet.getPacketType());
        rc.broadcast(startingChannel + 1, packet.getRobotId());
        rc.broadcast(startingChannel + 2, packet.getRoundSent());
        rc.broadcastFloat(startingChannel + 3, packet.getXCord());
        rc.broadcastFloat(startingChannel + 4, packet.getYCord());
        return true;
    }

    static int findEmptyChannel() throws GameActionException {
        int channelStart = getRobotChannelStart(rc.senseRobot(rc.getID()));
        int channelEnd = getRobotChannelEnd(rc.senseRobot(rc.getID()));

        return findEmptyChannel(channelStart, channelEnd);
    }

    static int findEmptyChannel(int channelStart, int channelEnd) throws GameActionException {
        for (int currChannel = channelStart; currChannel <= channelEnd; currChannel += Packet.PACKET_SIZE) {
            int data = rc.readBroadcast(currChannel);
            if (data == 0) {
                return currChannel;
            }
        }

        return GameConstants.BROADCAST_MAX_CHANNELS;
    }


    private static int getRobotChannelStart(RobotInfo robot) {
        switch (robot.getType()) {
            case ARCHON:
                return Message.ARCHON_GENERAL_CHANNEL_START;
            case GARDENER:
                return Message.GARDENER_GENERAL_CHANNEL_START;
            case SCOUT:
                return Message.SCOUT_GENERAL_CHANNEL_START;
            case LUMBERJACK:
                return Message.LUMBERJACK_GENERAL_CHANNEL_START;
            case SOLDIER:
                return Message.SOLDIER_GENERAL_CHANNEL_START;
            case TANK:
                return Message.TANK_GENERAL_CHANNEL_START;
        }
        return 0;
    }

    private static int getRobotChannelEnd(RobotInfo robot) {
        switch (robot.getType()) {
            case ARCHON:
                return Message.ARCHON_GENERAL_CHANNEL_END;
            case GARDENER:
                return Message.GARDENER_GENERAL_CHANNEL_END;
            case SCOUT:
                return Message.SCOUT_GENERAL_CHANNEL_END;
            case LUMBERJACK:
                return Message.LUMBERJACK_GENERAL_CHANNEL_END;
            case SOLDIER:
                return Message.SOLDIER_GENERAL_CHANNEL_END;
            case TANK:
                return Message.TANK_GENERAL_CHANNEL_END;
        }
        return 0;
    }

    static void broadcastVisibleEnemyLocations() throws GameActionException {
        int channelStart = Message.ENEMY_LOCATION_CHANNEL_START;
        int channelEnd = Message.ENEMY_LOCATION_CHANNEL_END;

        for (RobotInfo robot : visibleEnemyUnits) {
            int emptyChannel = findEmptyChannel(channelStart, channelEnd);
            if (emptyChannel == GameConstants.BROADCAST_MAX_CHANNELS) {
               return;
            }
            System.out.println("Broadcasting at location " + emptyChannel);
            Packet packet = new Packet(Message.TARGET_PACKET, robot.getID(), robot.getLocation(), rc.getRoundNum());
            tryBroadcastPacket(packet, emptyChannel);
        }
    }

    static void cleanUpEnemyLocations() throws GameActionException {
        int channelStart = Message.ENEMY_LOCATION_CHANNEL_START;
        int channelEnd = Message.ENEMY_LOCATION_CHANNEL_END;

        for (int i = channelStart; i <= channelEnd; i += Packet.PACKET_SIZE) {
            int packetType = rc.readBroadcast(i);
            int roundSent = rc.readBroadcast(i + 2);

            if (packetType == Message.TARGET_PACKET && rc.getRoundNum() - roundSent > 30) {
                System.out.println("Cleaning up packet at " + i + " from round " + roundSent);
                rc.broadcast(i, 0);
                rc.broadcast(i + 1, 0);
                rc.broadcast(i + 2, 0);
                rc.broadcast(i + 3, 0);
                rc.broadcast(i + 4, 0);
            }
        }

    }

    static Packet tryReadPacket(int startChannel) throws GameActionException {
        int packetType = rc.readBroadcast(startChannel);
        if (packetType == 0) {
            return null;
        }
        int robotId = rc.readBroadcast(startChannel + 1);
        int roundSent = rc.readBroadcast(startChannel + 2);
        float xLoc = rc.readBroadcastFloat(startChannel + 3);
        float yLoc = rc.readBroadcastFloat(startChannel + 4);
        MapLocation loc = new MapLocation(xLoc, yLoc);
        return new Packet(packetType, robotId, loc, roundSent);
    }
}
