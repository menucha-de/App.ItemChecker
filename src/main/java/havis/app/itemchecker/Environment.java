package havis.app.itemchecker;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Environment {

	private final static Logger log = Logger.getLogger(Environment.class.getName());
	private final static Properties properties = new Properties();

	static {
		try (InputStream stream = Environment.class.getClassLoader().getResourceAsStream("havis.app.itemchecker.properties")) {
			properties.load(stream);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load environment properties", e);
		}
	}

	public static final String LOCK = properties.getProperty("havis.app.itemchecker.lock", "conf/havis/app/itemchecker/lock");
	public static final String SPEC = properties.getProperty("havis.app.itemchecker.spec", "conf/havis/app/itemchecker/spec");

	public static final String JDBC_URL = properties.getProperty("havis.app.itemchecker.jdbcUrl",
			"jdbc:h2:./itemchecker;INIT=RUNSCRIPT FROM 'conf/havis/app/itemchecker/items.sql'");
	public static final String JDBC_DRIVER = properties.getProperty("havis.app.itemchecker.jdbcDriver", "org.h2.Driver");
	public static final String JDBC_USERNAME = properties.getProperty("havis.app.itemchecker.jdbcUsername", "sa");
	public static final String JDBC_PASSWORD = properties.getProperty("havis.app.itemchecker.jdbcPassword", "");
	
	public static final String CONFIG_FILE = properties.getProperty("havis.app.itemchecker.configFile", "conf/havis/app/itemchecker/config.json");
	
}