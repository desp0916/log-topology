package storm.cookbook.log;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.junit.Test;

import storm.cookbook.log.model.LogEntry;
import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


public class TestIndexerBolt extends StormTestCase{

	/**
	 * The storing matcher helps with an unusual case that we have.
	 * Usually, in unit testing, you know what the expected value up front,
	 * fare enough. However in this case we don't know the id of the indexed
	 * value until after it is indexed, because that id is generated by
	 * elastic search, an external entity. The bolt will emit the id along
	 * with the log entry, we need to create expectations of values being
	 * emitted, but we also need to get the unknown value of the ID. This
	 * custom matcher achieves that, allows us to check that values are
	 * returned and allows us to get the id for later checking and use.
	 *
	 */
	private static class StoringMatcher extends BaseMatcher<Values> {
	    private final List<Values> objects = new ArrayList<Values>();
	    public boolean matches(Object item) {
	      if (item instanceof Values) {
	        objects.add((Values) item);
	        return true;
	      }
	      return false;
	    }

	    public void describeTo(Description description) {
	      description.appendText("any integer");
	    }

	    public Values getLastValue() {
	      return objects.remove(0);
	    }
	  }

	@Test
	public void test() throws IOException {
		//Config, ensure we are in debug mode
		Map config = new HashMap();
        config.put(backtype.storm.Config.TOPOLOGY_DEBUG, true);
        Node node = NodeBuilder.nodeBuilder().local(true).node();
        Client client = node.client();

        final OutputCollector collector = context.mock(OutputCollector.class);
        IndexerBolt bolt = new IndexerBolt();
        bolt.prepare(config, null, collector);

        final LogEntry entry = getEntry();

        final Tuple tuple = getTuple();
        final StoringMatcher matcher = new StoringMatcher();
        context.checking(new Expectations(){{
        	oneOf(tuple).getValueByField(FieldNames.LOG_ENTRY);will(returnValue(entry));
        	oneOf(collector).emit(with(matcher));
        }});

        bolt.execute(tuple);
        context.assertIsSatisfied();

        //get the ID for the index
        String id = (String) matcher.getLastValue().get(1);

        //Check that the indexing working
        GetResponse response = client.prepareGet(IndexerBolt.INDEX_NAME,IndexerBolt.INDEX_TYPE,id)
                .execute()
                .actionGet();
        assertTrue(response.isExists());
	}

}
