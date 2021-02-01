package havis.custom.harting.itemchecker.ui.handheld.hw.impl;

import com.google.gwt.user.client.rpc.AsyncCallback;

import havis.application.common.HAL;
import havis.application.common.event.KeyEventListener;
import havis.custom.harting.itemchecker.ui.handheld.hw.Keyboard;

public class HalKeyboard implements Keyboard {

	public HalKeyboard() {

	}

	private boolean isServiceAvailable() {
		return HAL.Service.Keyboard.isSupported();
	}

	@Override
	public void setKeyEventListener(KeyEventListener listener, AsyncCallback<Void> callback) {
		if (isServiceAvailable()) {
			HAL.Service.Keyboard.setKeyEventListener(listener);
			callback.onSuccess(null);
		} else {
			callback.onFailure(new Throwable("Keyboad service is not available."));
		}
	}

}
