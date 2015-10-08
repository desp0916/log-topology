package storm.cookbook.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import storm.cookbook.log.model.LogEntry;

public class TestLogEntry extends StormTestCase{

	@Test
	public void testFromJSON() throws IOException, Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Date test = format.parse("2013-01-04T11:09:16.171");
		String testData = UnitTestUtils.readFile("/resources/testData1.json");
		JSONObject obj=(JSONObject) JSONValue.parse(testData);
		LogEntry entry = new LogEntry(obj);
		assertEquals("1",entry.getVersion());
		assertEquals(0,entry.getTags().size());
		assertEquals(0,entry.getFields().size());
		assertEquals(test, entry.getTimestamp());
		assertEquals("hdp01.localdomain",entry.getHost());
		assertEquals("/var/log/messages",entry.getPath());
		assertNotNull(entry.getMessage());
		assertEquals("syslog", entry.getType());
	}

	@Test
	public void testToJSON() throws IOException{
		String testData = UnitTestUtils.readFile("/resources/testData1.json");
		JSONObject obj=(JSONObject) JSONValue.parse(testData);
		LogEntry entry = new LogEntry(obj);
		JSONObject test = entry.toJSON();
		assertEquals(obj.get("message"), test.get("message"));
		assertEquals(obj.get("@version"), test.get("@version"));
		assertEquals(DateFormat.getDateInstance().format(entry.getTimestamp()), test.get("@timestamp"));
		assertEquals(obj.get("host"), test.get("host"));
		assertEquals(obj.get("path"), test.get("path"));
		assertEquals(obj.get("type"), test.get("type"));
	}

	@Test
	public void testEquals() throws IOException{
		LogEntry lhs = getEntry();
		LogEntry rhs = getEntry();
		assertEquals(lhs, rhs);
	}

}
