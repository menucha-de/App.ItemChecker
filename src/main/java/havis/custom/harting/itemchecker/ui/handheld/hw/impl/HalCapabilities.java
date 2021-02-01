package havis.custom.harting.itemchecker.ui.handheld.hw.impl;

import havis.application.common.HAL;
import havis.custom.harting.itemchecker.ui.handheld.hw.Capabilities;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class HalCapabilities implements Capabilities {

	public HalCapabilities() {

	}

	private boolean isServiceAvailable() {
		return true;
	}

	private void getCapabilitiesValue(String key, AsyncCallback<Object> callback) {
		if (isServiceAvailable()) {
			HAL.Service.Capabilities.getCapabilityValue(key, callback);
		} else {
			callback.onFailure(new Throwable("Capabilities service is not available."));
		}

	}

	@Override
	public void getWLanConnectionState(final AsyncCallback<Boolean> callback) {
		getCapabilitiesValue("WLanConnectionState", new AsyncCallback<Object>() {

			@Override
			public void onSuccess(Object result) {
				callback.onSuccess(Boolean.valueOf(result + ""));
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	@Override
	public void getWLanAddress(final AsyncCallback<String> callback) {
		getCapabilitiesValue("WLanAddress", new AsyncCallback<Object>() {

			@Override
			public void onSuccess(Object result) {
				callback.onSuccess(result == null ? "" : result + "");
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	@Override
	public void getLanAddress(final AsyncCallback<String> callback) {
		getCapabilitiesValue("LanAddress", new AsyncCallback<Object>() {

			@Override
			public void onSuccess(Object result) {
				callback.onSuccess(result == null ? "" : result + "");
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	@Override
	public void getChargeCondition(final AsyncCallback<Integer> callback) {
		getCapabilitiesValue("ChargeCondition", new AsyncCallback<Object>() {

			@Override
			public void onSuccess(Object result) {
				try {
					callback.onSuccess(Integer.valueOf(result + ""));
				} catch (NumberFormatException | NullPointerException e) {
					callback.onFailure(new Throwable("Unable to parse charge condition."));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

}
