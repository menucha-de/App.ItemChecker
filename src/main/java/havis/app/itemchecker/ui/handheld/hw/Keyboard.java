package havis.app.itemchecker.ui.handheld.hw;

import com.google.gwt.user.client.rpc.AsyncCallback;

import havis.application.common.event.KeyEventListener;

public interface Keyboard {

	public void setKeyEventListener(KeyEventListener listener, AsyncCallback<Void> callback);
	
}
