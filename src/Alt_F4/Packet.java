package Alt_F4;

import battlecode.common.MapLocation;

public class Packet {
    public static final int PACKET_SIZE = 3;

    private int packetType;
    private int robotId;
    private MapLocation loc;

    public Packet(int packetType, int robotId, MapLocation loc) {
        this.packetType = packetType;
        this.robotId = robotId;
        this.loc = loc;
    }

    public int getPacketType() {
        return this.packetType;
    }

    public int getRobotId() {
        return this.robotId;
    }

    public int getLocation() {
        return Utils.mapLocationToInt(loc);
    }
}
