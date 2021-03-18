package havis.app.itemchecker.ui.handheld.hw;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Capabilities {

	void getWLanConnectionState(AsyncCallback<Boolean> callback);

	void getWLanAddress(AsyncCallback<String> callback);

	void getLanAddress(AsyncCallback<String> callback);

	void getChargeCondition(AsyncCallback<Integer> callback);

}
