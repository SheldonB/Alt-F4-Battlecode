package Alt_F4;

public class Message {
    /*
    Define message channels that will be used.
    The broadcasting array will be memory mapped using
    a range of channels, for a unit group. Specific channels
    will also be used for specific a specific purpose.
     */
    public static final int STRATEGY_CHANNEL = 0;

    public static final int ARCHON_VOTING_START_CHANNEL = 1;
    public static final int ARCHON_VOTING_END_CHANNEL = 4;

    public static final int GARDENER_COUNT_CHANNEL = 5;

    public static final int SCOUT_COUNT_CHANNEL = 6;
    public static final int SCOUT_ATTACK_COORD_CHANNEL = 7;
    public static final int SCOUT_ATTACK_TARGET_ID_CHANNEL = 8;

    public static final int LAST_KNOW_ENEMY_ARCHON_CHANNEL = 9;

    /*
    Define message constants that are to be passed into
    the message array.
     */
    public static final int TURTLE_MESSAGE = 1;
    public static final int SCOUT_RUSH_MESSAGE = 2;
}
