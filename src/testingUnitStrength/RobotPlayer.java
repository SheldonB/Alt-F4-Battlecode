package testingUnitStrength;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                break;
            case LUMBERJACK:
                break;
        }
    }

    static void runArchon() throws GameActionException {
        while (true) {
            try {
                if (rc.canHireGardener(Direction.getNorth())) {
                    rc.hireGardener(Direction.getNorth());
                } else if (rc.canHireGardener(Direction.getSouth())) {
                    rc.hireGardener(Direction.getSouth());
                } else if (rc.canHireGardener(Direction.getEast())) {
                    rc.hireGardener(Direction.getEast());
                } else if (rc.canHireGardener(Direction.getWest())) {
                    rc.canHireGardener(Direction.getWest());
                }

                tryMove(randomDirection());

                Clock.yield();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static void runGardener() throws GameActionException {
        while (true) {
            try {
                tryMove(randomDirection());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

}
