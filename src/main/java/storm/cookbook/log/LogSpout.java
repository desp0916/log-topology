package storm.cookbook.log;

import java.util.Map;

import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import redis.clients.jedis.Jedis;
import storm.cookbook.log.model.LogEntry;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class LogSpout extends BaseRichSpout {

	private static final long serialVersionUID = 121212144L;
	public static Logger LOG = Logger.getLogger(LogSpout.class);
    public static final String LOG_CHANNEL = "rawLogs";
    private Jedis jedis;
    private String host;
    private int port;
    private SpoutOutputCollector collector;


	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(FieldNames.LOG_ENTRY));
    }

	public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        host = conf.get(Conf.REDIS_HOST_KEY).toString();
        port = Integer.valueOf(conf.get(Conf.REDIS_PORT_KEY).toString());
        this.collector = spoutOutputCollector;
        connectToRedis();
    }

    private void connectToRedis() {
        jedis = new Jedis(host, port);
    }

	public void nextTuple() {
        String content = jedis.rpop(LOG_CHANNEL);
        if(content==null || "nil".equals(content)) {
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        } else {
            JSONObject obj=(JSONObject) JSONValue.parse(content);
            LogEntry entry = new LogEntry(obj);
            collector.emit(new Values(entry));
        }
    }
}
