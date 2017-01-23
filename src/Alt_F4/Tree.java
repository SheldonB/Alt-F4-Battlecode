package Alt_F4;

import battlecode.common.*;

public class Tree {
    private TreeInfo treeInfo;
    private MapLocation treeLocation;
    private int spawnedRoundNumber;
    private int lastRoundWatered;

    public Tree(TreeInfo tree, int spawnedRoundNumber) {
        this.treeInfo = tree;
        this.spawnedRoundNumber = spawnedRoundNumber;
        this.treeLocation = tree.getLocation();
    }

    public TreeInfo getTreeInfo() {
        return this.treeInfo;
    }

    public MapLocation getTreeLocation() {
        return this.treeLocation;
    }

    public int getSpawnedRoundNumber() {
        return this.spawnedRoundNumber;
    }

    public void setLastRoundWatered(int roundNum) {
        this.lastRoundWatered = roundNum;
    }

    public int getLastRoundWatered() {
        return this.lastRoundWatered;
    }

    public boolean isFullyMatured(int currentRoundNumber) {
        return (currentRoundNumber - this.spawnedRoundNumber) >= 80;
    }

    public boolean shouldBeWatered(int currentRoundNumber) {
        return (currentRoundNumber - this.lastRoundWatered) >= 10;
    }
}
