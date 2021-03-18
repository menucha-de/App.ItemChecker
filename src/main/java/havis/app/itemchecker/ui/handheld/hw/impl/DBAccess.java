package havis.app.itemchecker.ui.handheld.hw.impl;

import havis.application.component.db.Database;

/**
 * Instance of this class is a singleton and provides an instance for
 * configuration database.
 */
public class DBAccess {

	private static DBAccess dbAccess = new DBAccess();
	private Database configDatabase;

	private DBAccess() {
		configDatabase = new Database("itemchecker_config_db", "1.0", "Configuration Database", new Long(1024));
	}

	/**
	 * @return Configuration database instance
	 */
	public static Database getConfigDatabaseInstance() {
		return dbAccess.configDatabase;
	}
}