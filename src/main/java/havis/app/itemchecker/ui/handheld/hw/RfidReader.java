package havis.app.itemchecker.ui.handheld.hw;

import havis.application.common.data.Tag;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RfidReader {
	
	void inventory(AsyncCallback<List<Tag>> callback);
	
	void inventory(boolean includeTID, AsyncCallback<List<Tag>> callback);
}
