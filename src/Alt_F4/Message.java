package Alt_F4;

public class Message {
    /*
    Define message channels that will be used.
    The broadcasting array will be memory mapped using
    a range of channels, for a unit group. Specific channels
    will also be used for specific a specific purpose.
     */
    public static final int ARCHON_GENERAL_CHANNEL_START = 0;
    public static final int ARCHON_GENERAL_CHANNEL_END = 999;

    public static final int GARDENER_GENERAL_CHANNEL_START = 1000;
    public static final int GARDENER_GENERAL_CHANNEL_END = 1999;

    public static final int SCOUT_GENERAL_CHANNEL_START = 2000;
    public static final int SCOUT_GENERAL_CHANNEL_END = 2999;

    public static final int LUMBERJACK_GENERAL_CHANNEL_START = 3000;
    public static final int LUMBERJACK_GENERAL_CHANNEL_END = 3999;

    public static final int SOLDIER_GENERAL_CHANNEL_START = 4000;
    public static final int SOLDIER_GENERAL_CHANNEL_END = 4999;

    public static final int TANK_GENERAL_CHANNEL_START = 5000;
    public static final int TANK_GENERAl_CHANNEL_END = 5999;

    public static final int STRATEGY_CHANNEL = 6000;

    public static final int ARCHON_VOTING_START_CHANNEL = 6001;
    public static final int ARCHON_VOTING_END_CHANNEL = 6004;

    public static final int TOTAL_TREE_COUNT_CHANNEL = 6005;
    public static final int GARDENER_COUNT_CHANNEL = 6006;
    public static final int LUMBERJACK_COUNT_CHANNEL = 6007;
    public static final int SOLDIER_COUNT_CHANNEL = 6008;
    public static final int SCOUT_COUNT_CHANNEL = 6009;
    public static final int TANK_COUNT_CHANNEL = 60010;

    public static final int LAST_KNOW_ENEMY_ARCHON_CHANNEL = 6011;

    public static final int SCOUT_ATTACK_COORD_CHANNEL = 6012;
    public static final int SCOUT_ATTACK_TARGET_ID_CHANNEL = 6013;

    public static final int MAX_KNOWN_X_INT_LOCATION_CHANNEL = 6014;
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
