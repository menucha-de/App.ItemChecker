package havis.app.itemchecker.ui.handheld;

import havis.app.itemchecker.rest.async.ItemCheckerServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Controller {
	void showMessage(String message);

	void updateGateway(AsyncCallback<Void> callback);

	ItemCheckerServiceAsync getService();
}
