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
        rc.broadcast(startingChannel + 1, packet.getLocation());
        return true;
    }

    static int findEmptyChannel() throws GameActionException {
        int channelStart = getRobotChannelStart(rc.senseRobot(rc.getID()));
        int channelEnd = getRobotChannelEnd(rc.senseRobot(rc.getID()));

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

}
