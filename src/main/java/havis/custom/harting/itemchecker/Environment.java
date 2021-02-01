package havis.custom.harting.itemchecker;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment {

	private final static Logger log = Logger.getLogger(Environment.class.getName());
	private final static Properties properties = new Properties();

	static {
		try (InputStream stream = Environment.class.getClassLoader().getResourceAsStream("havis.custom.harting.itemchecker.properties")) {
			properties.load(stream);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load environment properties", e);
		}
	}

	public static final String LOCK = properties.getProperty("havis.custom.harting.itemchecker.lock", "conf/havis/custom/harting/itemchecker/lock");
	public static final String SPEC = properties.getProperty("havis.custom.harting.itemchecker.spec", "conf/havis/custom/harting/itemchecker/spec");

	public static final String JDBC_URL = properties.getProperty("havis.custom.harting.itemchecker.jdbcUrl",
			"jdbc:h2:./itemchecker;INIT=RUNSCRIPT FROM 'conf/havis/custom/harting/itemchecker/items.sql'");
	public static final String JDBC_DRIVER = properties.getProperty("havis.custom.harting.itemchecker.jdbcDriver", "org.h2.Driver");
	public static final String JDBC_USERNAME = properties.getProperty("havis.custom.harting.itemchecker.jdbcUsername", "sa");
	public static final String JDBC_PASSWORD = properties.getProperty("havis.custom.harting.itemchecker.jdbcPassword", "");
	
	public static final String CONFIG_FILE = properties.getProperty("havis.custom.harting.itemchecker.configFile", "conf/havis/custom/harting/itemchecker/config.json");
	
}