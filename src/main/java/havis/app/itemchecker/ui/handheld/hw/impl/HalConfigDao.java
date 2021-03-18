package havis.app.itemchecker.ui.handheld.hw.impl;

import havis.application.component.db.Callback;
import havis.application.component.db.Error;
import havis.app.itemchecker.ui.handheld.Column;
import havis.app.itemchecker.ui.handheld.Columns;
import havis.app.itemchecker.ui.handheld.hw.ConfigDao;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Specifies the data source of the {@link ConfigDao} interface. Local database
 * in this implementation represents the data store.
 */
public class HalConfigDao implements ConfigDao {

	private final static String HOST = "gateway_host";
	private final static String COLUMNS = "columns";

	private List<Column> DEFAULT_COLUMNS = new ArrayList<Column>() {
		private static final long serialVersionUID = 1L;

		{
			add(new Column(Columns.GROUP, false, 1, 0));
			add(new Column(Columns.ID, false, 1, 1));
			add(new Column(Columns.CODE, true, 1, 2));
			add(new Column(Columns.DESCRIPTION, false, 1, 3));
			add(new Column(Columns.IMG, true, 75, 4));
		}
	};

	public HalConfigDao() {
		
	}

	private void getConfigValue(String key, Callback<String> callback) {
		DBAccess.getConfigDatabaseInstance().get(key, callback);
	}

	private void setConfigValue(String key, String value, Callback<Void> callback) {
		DBAccess.getConfigDatabaseInstance().put(key.toString(), value, callback);
	}

	@Override
	public void getHost(final AsyncCallback<String> callback) {
		getConfigValue(HOST, new Callback<String>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(new Throwable(error.getCode() + " - " + error.getMessage()));
			}

			@Override
			public void onSuccess(String result) {
				callback.onSuccess(result);
			}
		});
	}

	@Override
	public void setHost(String host, final AsyncCallback<Void> callback) {
		setConfigValue(HOST, host, new Callback<Void>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(new Throwable(error.getCode() + " - " + error.getMessage()));
			}

			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(result);
			}
		});
	}

	@Override
	public void getColumns(final AsyncCallback<List<Column>> callback) {
		getConfigValue(COLUMNS, new Callback<String>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(new Throwable(error.getCode() + " - " + error.getMessage()));				
			}

			@Override
			public void onSuccess(String result) {				
				try {
					callback.onSuccess(Column.fromString(result));
				} catch (Exception e) {
					callback.onSuccess(DEFAULT_COLUMNS);
				}
			}
		});
	}

	@Override
	public void setColumns(List<Column> columns, final AsyncCallback<Void> callback) {
		setConfigValue(COLUMNS, Column.toString(columns), new Callback<Void>() {

			@Override
			public void onFailure(Error error) {
				callback.onFailure(new Throwable(error.getCode() + " - " + error.getMessage()));
			}

			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(result);
			}
		});
	}
	
	
}
