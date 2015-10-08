package storm.cookbook.log;

public class Conf {

	/**
	 * Redis
	 */
    public static final String REDIS_HOST_KEY = "redisHost";
    public static final String REDIS_PORT_KEY = "redisPort";
    public static final String DEFAULT_JEDIS_PORT = "6379";

    /**
     * ElasticSearch
     */
    public static final String ELASTIC_HOST = "hdp01.localdomain";
    public static final String ELASTIC_CLUSTER_NAME = "cluster.name";
    public static final String DEFAULT_ELASTIC_CLUSTER = "elasticsearch";

    /**
     * Cassnadra
     */
    public static final String CASSANDRA_HOST_KEY = "cassandra.host";
    public static final String CASSANDRA_PORT_KEY = "cassandra.port";
    public static final String CASSANDRA_KEYSPACE_KEY = "cassandra.keyspace";
    public static final String COUNT_CF_NAME = "LogVolumeByMinute";
    public static final String LOGGING_KEYSPACE = "logstorm";
    public static final String LOGGING_TABLE = "log";

}
