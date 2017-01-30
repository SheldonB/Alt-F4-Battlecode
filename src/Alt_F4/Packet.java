package Alt_F4;

import battlecode.common.MapLocation;

public class Packet {
    public static final int PACKET_SIZE = 5;

    private int packetType;
    private int robotId;
    private MapLocation loc;
    private int roundSent;

    public Packet(int packetType, int robotId, MapLocation loc, int roundSent) {
        this.packetType = packetType;
        this.robotId = robotId;
        this.loc = loc;
        this.roundSent = roundSent;
    }

    public int getPacketType() {
        return this.packetType;
    }

    public int getRobotId() {
        return this.robotId;
    }

    public MapLocation getLocation() {
        return this.loc;
    }

    public float getXCord() {
        return loc.x;
    }

    public float getYCord() {
        return loc.y;
    }

    public int getRoundSent() {
        return this.roundSent;
    }
}
