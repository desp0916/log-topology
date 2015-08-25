package storm.cookbook.log;


public class Conf {
    public static final String REDIS_HOST_KEY = "hdp01.localdomain";
    public static final String REDIS_PORT_KEY = "6379";
    public static final String ELASTIC_CLUSTER_NAME = "elasticsearch";
    public static final String DEFAULT_ELASTIC_CLUSTER = "elasticsearch";

    public static final String COUNT_CF_NAME = "LogVolumeByMinute";
    public static final String LOGGING_KEYSPACE = "Logging";

    public static final String DEFAULT_JEDIS_PORT = "6379";
}
