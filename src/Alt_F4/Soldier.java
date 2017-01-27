package Alt_F4;

import battlecode.common.*;

class Soldier extends Base {
    private static Direction movingDirection;
    private static boolean hasMovedToInitalLocation = false;

    private static BodyInfo target;
    private static MapLocation targetLocation;


    static void run() throws GameActionException {
        System.out.println("Soldier has spawned.");

        while (true) {
            try {
                Utils.CheckWinConditions();
                Base.update();
                Debug.debug_drawSensorRadius();
                if (!rc.hasMoved() && nearbyBullets.length > 0) {
                    Pathing.tryDodgeBullet();
                }

                tryDetermineLocation();
                tryFireOnEnemy();
                if (!rc.hasAttacked() && targetLocation != null) {
                    tryMoveToLocation();
                } else {
                    wander();
                }
                /*

                if (shouldBeAggressive()) {
                    tryBeAggressive();
                }

                if (!rc.hasMoved()) {
                    Pathing.tryMove(Pathing.randomDirection());
                }

                //if (determineTarget() != null) {
                //    tryFireOnTarget();
                //}
                */

                Clock.yield();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static boolean tryDetermineLocation() throws GameActionException {
        if (visibleEnemyUnits.length > 0) {
            targetLocation = visibleEnemyUnits[0].getLocation();
            return true;
        }

        if (rc.getRoundNum() < 100) {
            targetLocation = enemyArchonLocations[0];
            return false;
        }


        if(visibleNeutralTrees.length > 0) {
            targetLocation = visibleNeutralTrees[0].getLocation();
            return true;
        }

        return false;
    }

    private static boolean tryFireOnEnemy() throws GameActionException {
        for (RobotInfo robot : visibleEnemyUnits) {
            if (!willShotCollideWithBody(robot)) {
                return tryFireOnTarget(robot.getLocation());
            }
        }

        for (TreeInfo tree : visibleNeutralTrees) {
            if (!willShotCollideWithBody(tree)) {
                return tryFireOnTarget(tree.getLocation());
            }
        }
        return false;
    }

    private static boolean tryMoveToLocation() throws GameActionException {
        if (rc.getLocation().distanceTo(targetLocation) >= rc.getType().sensorRadius) {
            return Pathing.tryMove(rc.getLocation().directionTo(targetLocation));
        }
        return false;
    }

    private static BodyInfo tryDetermineTarget() throws GameActionException {
        for (RobotInfo enemyRobot : visibleEnemyUnits) {
            return enemyRobot;
        }

        if (rc.getRoundNum() < 100) {
        }

        for (TreeInfo tree : visibleNeutralTrees) {
            if(!willShotCollideWithBody(tree)) {
                return tree;
            }
        }

        return null;
    }

    private static boolean willShotCollideWithBody(BodyInfo robotTarget) {
        Vector toEnemy = new Vector(rc.getLocation(), robotTarget.getLocation());

        for (RobotInfo friendlyRobot : visibleFriendlyUnits) {
            Vector toFriendly = new Vector(rc.getLocation(), friendlyRobot.getLocation());
            Vector projection = Vector.projection(toFriendly, toEnemy);

            if (projection.getEndPoint().distanceTo(friendlyRobot.getLocation()) <= friendlyRobot.getRadius()){
                return true;
            }
        }
        return false;
    }

    private static boolean tryFireOnTarget(MapLocation loc) throws GameActionException {
        Direction firingDirection = rc.getLocation().directionTo(loc);

        if (rc.canFirePentadShot() && shouldFirePentadShot(targetLocation)) {
            rc.firePentadShot(firingDirection);
            return true;
        }

        if (rc.canFireSingleShot()) {
            rc.fireSingleShot(firingDirection);
            return true;
        }

        return false;
    }

    private static boolean shouldFirePentadShot(MapLocation loc) {
        Direction firingDirection = rc.getLocation().directionTo(loc);
        Direction directionRangeRight = firingDirection.rotateRightDegrees(30);
        Direction directionRangeLeft = firingDirection.rotateLeftDegrees(30);
        rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(directionRangeLeft, 5), 255, 0, 0);
        rc.setIndicatorLine(rc.getLocation(), rc.getLocation().add(directionRangeRight, 5), 255, 0, 0);
        int robotsInPentadRange = 0;

        for (RobotInfo robot : visibleEnemyUnits) {
            Direction robotDirection = rc.getLocation().directionTo(robot.getLocation());
            if (directionRangeLeft.getAngleDegrees() >= robotDirection.getAngleDegrees()
                    && directionRangeRight.getAngleDegrees() <= robotDirection.getAngleDegrees()) {
                robotsInPentadRange++;
            }
        }

        return robotsInPentadRange > 1;
    }

    private static boolean shouldBeAggressive() {
        return numberOfSoldiers >= Constants.SOLDIERS_FOR_AGGRESSION;
    }

    private static void tryBeAggressive() throws GameActionException {
        MapLocation loc = null;

        if (visibleEnemyUnits.length > 0) {
            loc = visibleEnemyUnits[0].getLocation();
        }

        if (loc != null && !rc.hasAttacked() && rc.getLocation().distanceTo(loc) <= GameConstants.LUMBERJACK_STRIKE_RADIUS * 2) {
            tryFireOnTarget(loc);
        }

        if (loc != null && rc.hasAttacked() && !rc.hasMoved()) {
            Pathing.tryMove(rc.getLocation().directionTo(loc).opposite());
        }

        if (loc != null && !rc.hasMoved()) {
            Pathing.tryMove(rc.getLocation().directionTo(loc));
        } else if(loc == null && !rc.hasMoved()) {
            if (rc.getRoundNum() < 200 && movingDirection == null)  {
                movingDirection = rc.getLocation().directionTo(enemyArchonLocations[0]);
            }

            if (movingDirection == null && !hasMovedToInitalLocation) {
                movingDirection = Pathing.randomDirection();
            }

            if (!rc.canMove(movingDirection) && !hasMovedToInitalLocation) {
                movingDirection = Pathing.randomDirection();
            }

            Pathing.tryMove(movingDirection);
        }
    }

    private static void wander() throws GameActionException {
        if (movingDirection == null) {
            movingDirection = Pathing.randomDirection();
        }

        if (!rc.onTheMap(rc.getLocation().add(movingDirection, rc.getType().sensorRadius - 0.1F))) {
            movingDirection = Pathing.randomDirection();
        } else {
            if (!rc.hasMoved()) {
                Pathing.tryMove(movingDirection);
            }
        }
    }
}

