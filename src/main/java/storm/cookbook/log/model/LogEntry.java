package storm.cookbook.log.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LogEntry implements Serializable {

	private static final long serialVersionUID = -7736449148132326957L;
	public static Logger LOG = Logger.getLogger(LogEntry.class);
	private String message = "";
	private String version = "1";
	private Date timestamp;
	private String host = "";
	private String path = "";
	private String type = "";
	private List<String> tags = new ArrayList<String>();
	private Map<String, String> fields = new HashMap<String, String>();
	private boolean filter = false;

	private NotificationDetails notifyAbout = null;

	private static String[] FORMATS = new String[] {
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy.MM.dd G 'at' HH:mm:ss z",
			"yyyyy.MMMMM.dd GGG hh:mm aaa", "EEE, d MMM yyyy HH:mm:ss Z",
			"yyMMddHHmmssZ" };

	@SuppressWarnings("unchecked")
	public LogEntry(JSONObject json) {
		message = (String) json.get("message");
		version = (String) json.get("@version");
		timestamp = parseDate((String) json.get("@timestamp"));
		host = (String) json.get("host");
		path = (String) json.get("path");
		type = (String) json.get("type");
		// JSONArray array = (JSONArray)json.get("@tags");
		String t = "[]";
		Object obj = JSONValue.parse(t);
		JSONArray array = (JSONArray) obj;
		tags.addAll(array);
		// JSONObject fields = (JSONObject)json.get("@fields");
		String o = "{}";
		Object obj_o = JSONValue.parse(o);
		JSONObject fields = (JSONObject) obj_o;
		fields.putAll(fields);
	}

	public Date parseDate(String value) {
		for (int i = 0; i < FORMATS.length; i++) {
			SimpleDateFormat format = new SimpleDateFormat(FORMATS[i]);
			Date temp;
			try {
				temp = format.parse(value);
				if (temp != null)
					return temp;
			} catch (ParseException e) {
			}
		}
		LOG.error("Could not parse timestamp for log");
		return null;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String dateString = format.format(timestamp);
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("@version", version);
//		json.put("@timestamp", DateFormat.getDateInstance().format(timestamp));
		json.put("@timestamp", dateString);
		json.put("host", host);
		json.put("path", path);
		json.put("type", type);
		JSONArray temp = new JSONArray();
		temp.addAll(tags);
		json.put("@tags", temp);
		JSONObject fieldTemp = new JSONObject();
		fieldTemp.putAll(fields);
		json.put("@fields", fieldTemp);
		return json;
	}

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}

	public NotificationDetails getNotificationDetails() {
		return notifyAbout;
	}

	public void notifyAbout(NotificationDetails notifyAbout) {
		this.notifyAbout = notifyAbout;
	}

	public String getVersion() {
		return version;
	}

	public List<String> getTags() {
		return tags;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void addField(String name, String value) {
		fields.put(name, value);
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		path.contains("");
		return path;
	}

	public String getMessage() {
		return message;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		result = prime * result + (filter ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogEntry other = (LogEntry) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		if (filter != other.filter)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
