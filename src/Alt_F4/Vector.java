package Alt_F4;

import battlecode.common.*;

public class Vector {
    private MapLocation startPoint;
    private MapLocation endPoint;
    private Direction direction;

    public Vector(MapLocation startPoint, MapLocation endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.direction = new Direction(startPoint, endPoint);
    }

    public MapLocation getStartPoint() {
        return this.startPoint;
    }

    public MapLocation getEndPoint() {
        return this.endPoint;
    }

    public float getX() {
        return endPoint.x - startPoint.x;
    }

    public float getY() {
        return endPoint.y - startPoint.y;
    }

    public float getMagnitude() {
        return (float)Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
    }

    public static float dotProduct(Vector v1, Vector v2) {
        return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY());
    }

    public static Vector projection(Vector v1, Vector v2) {
        float dotProduct = dotProduct(v1, v2);
        float magnitudeSquared = (float)Math.pow(v2.getMagnitude(), 2);

        float scalar = dotProduct / magnitudeSquared;

        float xComp = (scalar * v1.getX()) + v1.getStartPoint().x;
        float yComp = (scalar * v1.getY()) + v1.getStartPoint().y;

        return new Vector(v1.getStartPoint(), new MapLocation(xComp,yComp));
    }
}
