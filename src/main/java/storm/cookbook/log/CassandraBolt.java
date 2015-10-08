package storm.cookbook.log;

import java.util.Map;

import org.apache.log4j.Logger;

import storm.cookbook.log.model.LogEntry;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraBolt implements IRichBolt {

	private static final long serialVersionUID = -4247881922349464807L;
	public static Logger LOG = Logger.getLogger(CassandraBolt.class);

	private Cluster cluster;
	private OutputCollector collector;
	private Session session;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		String node = (String) stormConf.get(Conf.CASSANDRA_HOST_KEY);
		cluster = Cluster.builder().addContactPoint(node).build();
		session = cluster.connect();
	}

	public void execute(Tuple input) {
		createSchema();
		LogEntry entry = (LogEntry)input.getValueByField(FieldNames.LOG_ENTRY);
		if(entry == null){
			LOG.fatal( "Received null or incorrect value from tuple" );
			return;
		}
		session.execute(statement);
	}

	private void createSchema() {
		session.execute("CREATE KEYSPACE IF NOT EXISTS "
				+ Conf.LOGGING_KEYSPACE + " WITH replication "
				+ "= {'class':'SimpleStrategy', 'replication_factor':1};");
		session.execute("CREATE TABLE IF NOT EXISTS " + Conf.LOGGING_TABLE
				+ "(host varchar, path varchar, counter_value counter,"
				+ "PRIMARY KEY (host, path))");
	}

	public void cleanup() {
		session.close();
		cluster.close();
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // we don't emit anything from here.
	}

	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}