package havis.custom.harting.itemchecker.ui.handheld.hw;

import havis.custom.harting.itemchecker.ui.handheld.Column;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigDao {

	void getHost(AsyncCallback<String> callback);
	
	void setHost(String host, AsyncCallback<Void> callback);
	
	void getColumns(AsyncCallback<List<Column>> callback);
	
	void setColumns(List<Column> columns, AsyncCallback<Void> callback);
}
