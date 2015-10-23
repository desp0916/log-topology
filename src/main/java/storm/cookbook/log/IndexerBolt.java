package storm.cookbook.log;

//import org.elasticsearch.node.NodeBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;

import storm.cookbook.log.model.LogEntry;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class IndexerBolt extends BaseRichBolt {

	private static final long serialVersionUID = 7996013724003895484L;
	private Client client;
	public static Logger LOG = Logger.getLogger(LogRulesBolt.class);
	private OutputCollector collector;

	public static final String INDEX_NAME = "logstorm";
	public static final String INDEX_TYPE = "logentry";

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		// Node node;
		if ((Boolean) stormConf.get(backtype.storm.Config.TOPOLOGY_DEBUG) == true) {
			// node = NodeBuilder.nodeBuilder().local(true).node();
			Node node = nodeBuilder().local(true).node();
			client = node.client();
		} else {
			String clusterName = (String) stormConf
					.get(Conf.ELASTIC_CLUSTER_NAME);
			if (clusterName == null)
				clusterName = Conf.DEFAULT_ELASTIC_CLUSTER;
			Settings settings = ImmutableSettings.settingsBuilder()
					.put("cluster.name", clusterName).build();
			client = new TransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(
							Conf.ELASTIC_HOST, 9300));
			// node = NodeBuilder.nodeBuilder().clusterName(clusterName).node();
			// client = node.client();
			LOG.error("GARYABC: " + clusterName);
		}
	}

	public void execute(Tuple input)  {
		LogEntry entry = (LogEntry) input.getValueByField(FieldNames.LOG_ENTRY);
		if (entry == null) {
			LOG.fatal("Received null or incorrect value from tuple");
			return;
		}
		String toBeIndexed = entry.toJSON().toJSONString();
		LOG.error("GARYAAAAA: " + toBeIndexed);
		IndexResponse response = client.prepareIndex(INDEX_NAME, INDEX_TYPE)
				.setSource(toBeIndexed).execute().actionGet();
		if (response == null)
			LOG.error("Failed to index Tuple: " + input.toString());
		else {
			// LOG.error("GARYZZZ: "+input.toString());
			if (response.getId() == null)
				LOG.error("Failed to index Tuple: " + input.toString());
			else {
				LOG.debug("Indexing success on Tuple: " + input.toString());
				collector.emit(new Values(entry, response.getId()));
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(FieldNames.LOG_ENTRY,
				FieldNames.LOG_INDEX_ID));
	}

	@Override
	public void cleanup() {
		client.close();
	}
}
