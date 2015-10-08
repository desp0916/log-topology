package storm.cookbook.log;

import java.util.HashMap;

import org.apache.cassandra.thrift.AuthorizationException;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
//import backtype.storm.generated.AuthorizationException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
//import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import com.hmsonline.storm.cassandra.bolt.AckStrategy;
import com.hmsonline.storm.cassandra.bolt.CassandraCounterBatchingBolt;

public class LogTopology {

	private TopologyBuilder builder = new TopologyBuilder();
	private Config conf = new Config();
	private LocalCluster cluster;
	String configKey = "cassandra-config";

	public LogTopology() {

		builder.setSpout("logSpout", new LogSpout(), 10);
		builder.setBolt("logRules", new LogRulesBolt(), 10).shuffleGrouping(
				"logSpout");
		builder.setBolt("indexer", new IndexerBolt(), 10).shuffleGrouping(
				"logRules");
		builder.setBolt("counter", new VolumeCountingBolt(), 10)
				.shuffleGrouping("logRules");
		builder.setBolt("countPersistor", new CassandraBolt(), 10)
				.shuffleGrouping("counter");
		// Create a CassandraBolt that writes to the "LogVolumeByMinute" column
		// family and uses the Tuple field "RowKey" as the row key
		CassandraCounterBatchingBolt<String, String, String> logPersistenceBolt =
				new CassandraCounterBatchingBolt<String, String, String>(
						Conf.LOGGING_KEYSPACE, configKey, Conf.COUNT_CF_NAME,
						VolumeCountingBolt.FIELD_ROW_KEY,
						VolumeCountingBolt.FIELD_INCREMENT);
		logPersistenceBolt.setAckStrategy(AckStrategy.ACK_ON_RECEIVE);
		builder.setBolt("countPersistor", logPersistenceBolt, 10)
				.shuffleGrouping("counter");

		// Maybe add:
		// Stem and stop word counting per file
		// The persister for the stem analysis (need to check the counting
		// capability first on storm-cassandra)

		conf.put(Conf.REDIS_PORT_KEY, Conf.DEFAULT_JEDIS_PORT);
	}

	public TopologyBuilder getBuilder() {
		return builder;
	}

	public LocalCluster getLocalCluster() {
		return cluster;
	}

	public Config getConf() {
		return conf;
	}

	public void runLocal(int runTime) {
		conf.setDebug(true);
		conf.put(Conf.REDIS_HOST_KEY, "localhost");
		conf.put(Conf.CASSANDRA_HOST_KEY, "localhost:9200");
		cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		if (runTime > 0) {
			Utils.sleep(runTime);
			shutDownLocal();
		}
	}

	public void shutDownLocal() {
		if (cluster != null) {
			cluster.killTopology("test");
			cluster.shutdown();
		}
	}

	public void runCluster(String name, String redisHost, String cassandraHost)
			throws AlreadyAliveException, InvalidTopologyException,
			AuthorizationException {
		// conf.setDebug(true);
		conf.setNumWorkers(20);
		conf.put(Conf.REDIS_HOST_KEY, redisHost);

		HashMap<String, Object> clientConfig = new HashMap<String, Object>();
		clientConfig.put(Conf.CASSANDRA_HOST_KEY, cassandraHost);
		clientConfig.put(Conf.CASSANDRA_PORT_KEY, 9160);
		clientConfig.put(Conf.CASSANDRA_KEYSPACE_KEY, Conf.LOGGING_KEYSPACE);
		conf.put(configKey, clientConfig);

		StormSubmitter.submitTopology(name, conf, builder.createTopology());
	}

	public static void main(String[] args) throws Exception {

		LogTopology topology = new LogTopology();

		if (args != null && args.length > 1) {
			topology.runCluster(args[0], args[1], args[2]);
		} else {
			if (args != null && args.length == 1)
				System.out
						.println("Running in local mode, redis ip missing for cluster run");
			topology.runLocal(10000);
		}

	}

}
