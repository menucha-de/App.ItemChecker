package havis.app.itemchecker.ui.handheld.hw.impl;

import havis.application.common.HAL;
import havis.application.common.data.JsonArray;
import havis.application.common.data.Tag;
import havis.app.itemchecker.ui.handheld.hw.RfidReader;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A {@link RfidReader} implementation which uses the Hal.js
 */
public class HalRfidReader implements RfidReader {

	public HalRfidReader() {

	}

	/**
	 * @return True if C1G2 service is available.
	 */
	private boolean isServiceAvailable() {
		return HAL.Service.C1G2.isSupported();
	}

	@Override
	public void inventory(final AsyncCallback<List<Tag>> callback) {
		if(isServiceAvailable()){		
			HAL.Service.C1G2.tagInventory(new AsyncCallback<JsonArray<Tag>>() {

				@Override
				public void onSuccess(JsonArray<Tag> result) {
					List<Tag> tags = new ArrayList<Tag>();
					if ((result != null) && (result.length() > 0)) {
						for (int i = 0; i < result.length(); i++) {
							tags.add(result.get(i));
						}
					}
					callback.onSuccess(tags);
				}

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}
			});
		} else {
			callback.onFailure(new Throwable("C1G2 service is not available."));
		}
	}

	@Override
	public void inventory(final boolean includeTID, final AsyncCallback<List<Tag>> callback) {		
		inventory(new AsyncCallback<List<Tag>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(List<Tag> result) {
				if(!includeTID){
					callback.onSuccess(result);
				} else {					
					// TODO read TID
					// For the feature
					callback.onFailure(new Throwable("Unsupported operation."));
				}
			}
		});
	}	
}