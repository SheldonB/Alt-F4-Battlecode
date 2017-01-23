package Alt_F4;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

class Debug extends Base {

    static void debug_drawSensorRadius() {
        debug_drawCircle(15, rc.getType().sensorRadius, 255, 0, 0);
    }

    static void debug_drawStrideRadius() {
        debug_drawCircle(45, rc.getType().strideRadius, 0, 0, 255);
    }

    private static void debug_drawCircle(int degreeOffset, float radius, int red, int green, int blue) {
        Direction dir = Direction.getEast();
        float currentDegrees = dir.getAngleDegrees();

        while (currentDegrees <= 360) {
            MapLocation loc = rc.getLocation().add(dir.rotateRightDegrees(currentDegrees), radius);
            rc.setIndicatorDot(loc, red, green, blue);
            currentDegrees += degreeOffset;
        }
    }

}
