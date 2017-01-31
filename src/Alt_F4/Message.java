package Alt_F4;

public class Message {
    /*
    Define message channels that will be used.
    The broadcasting array will be memory mapped using
    a range of channels, for a unit group. Specific channels
    will also be used for specific a specific purpose.
     */
    public static final int ARCHON_GENERAL_CHANNEL_START = 1;
    public static final int ARCHON_GENERAL_CHANNEL_END = 1000;

    public static final int GARDENER_GENERAL_CHANNEL_START = 1001;
    public static final int GARDENER_GENERAL_CHANNEL_END = 2000;

    public static final int SCOUT_GENERAL_CHANNEL_START = 2001;
    public static final int SCOUT_GENERAL_CHANNEL_END = 3000;

    public static final int LUMBERJACK_GENERAL_CHANNEL_START = 3001;
    public static final int LUMBERJACK_GENERAL_CHANNEL_END = 4000;

    public static final int SOLDIER_GENERAL_CHANNEL_START = 4001;
    public static final int SOLDIER_GENERAL_CHANNEL_END = 5000;

    public static final int TANK_GENERAL_CHANNEL_START = 5001;
    public static final int TANK_GENERAL_CHANNEL_END = 6000;

    public static final int ENEMY_LOCATION_CHANNEL_START = 6001;
    public static final int ENEMY_LOCATION_CHANNEL_END = 7000;

    public static final int STRATEGY_CHANNEL = 7001;

    public static final int TOTAL_TREE_COUNT_CHANNEL = 7002;
    public static final int GARDENER_COUNT_CHANNEL = 7003;
    public static final int LUMBERJACK_COUNT_CHANNEL = 7004;
    public static final int SOLDIER_COUNT_CHANNEL = 7005;
    public static final int SCOUT_COUNT_CHANNEL = 7006;
    public static final int TANK_COUNT_CHANNEL = 7007;

    public static final int LAST_KNOW_ENEMY_ARCHON_CHANNEL = 7008;

    public static final int MAX_KNOWN_X_INT_LOCATION_CHANNEL = 7009;
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
    public static final int SOLDIER_HARRASS_MESSAGE = 1;
    public static final int SCOUT_HARRASS_MESSAGE = 2;


    /*
    Deine Packet Types;
    */
    public static final int TARGET_PACKET = 1;
    public static final int DESTROY_TREE_PACKET = 2;
}
