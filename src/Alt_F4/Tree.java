package Alt_F4;

import battlecode.common.*;

public class Tree {
    private TreeInfo treeInfo;
    private int spawnedRoundNumber;
    private int lastRoundWatered;

    public Tree(TreeInfo tree, int spawnedRoundNumber) {
        this.treeInfo = tree;
        this.spawnedRoundNumber = spawnedRoundNumber;
    }

    public TreeInfo getTreeInfo() {
        return this.treeInfo;
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

    public boolean isFullyMatured() {
        return this.spawnedRoundNumber >= 80;
    }

    public boolean shouldBeWatered() {
    }
}
