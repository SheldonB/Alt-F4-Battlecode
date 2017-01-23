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

    public static final int TOTAL_TREE_COUNT_CHANNEL = 20;

    public static final int GARDENER_COUNT_CHANNEL = 5;

    public static final int SCOUT_COUNT_CHANNEL = 6;
    public static final int SCOUT_ATTACK_COORD_CHANNEL = 7;
    public static final int SCOUT_ATTACK_TARGET_ID_CHANNEL = 8;

    public static final int LUMBERJACK_COUNT_CHANNEL = 30;

    public static final int SOLDIER_COUNT_CHANNEL = 35;

    public static final int LAST_KNOW_ENEMY_ARCHON_CHANNEL = 9;

    public static final int MAX_KNOWN_X_INT_LOCATION_CHANNEL = 10;
    public static final int MAX_KNOWN_X_FLOAT_LOCATION_CHANNEL = MAX_KNOWN_X_INT_LOCATION_CHANNEL + 1;

    public static final int MIN_KNOWN_X_INT_LOCATION_CHANNEL = MAX_KNOWN_X_FLOAT_LOCATION_CHANNEL + 1;
    public static final int MIN_KNOWN_X_FLOAT_LOCATION_CHANNEL = MIN_KNOWN_X_INT_LOCATION_CHANNEL + 1;

    public static final int MAX_KNOWN_Y_INT_LOCATION_CHANNEL = MIN_KNOWN_X_FLOAT_LOCATION_CHANNEL + 1;
    public static final int MAX_KNOWN_Y_FLOAT_LOCATION_CHANNEL = MAX_KNOWN_Y_INT_LOCATION_CHANNEL + 1;

    public static final int MIN_KNOWN_Y_INT_LOCATION_CHANNEL = MAX_KNOWN_Y_FLOAT_LOCATION_CHANNEL + 1;
    public static final int MIN_KNOWN_Y_FLOAT_LOCATION_CHANNEL = MIN_KNOWN_Y_INT_LOCATION_CHANNEL + 1;

    /*
    Define message constants that are to be passed into
    the message array.
     */
    public static final int TURTLE_MESSAGE = 1;
    public static final int SCOUT_RUSH_MESSAGE = 2;
}
