package havis.app.itemchecker.ui.handheld;

import havis.application.common.data.Tag;
import havis.app.itemchecker.Item;
import havis.app.itemchecker.Sighting;
import havis.app.itemchecker.rest.async.ItemCheckerServiceAsync;
import havis.app.itemchecker.ui.handheld.hw.ConfigDao;
import havis.app.itemchecker.ui.handheld.hw.RfidReader;
import havis.app.itemchecker.ui.handheld.hw.impl.HalConfigDao;
import havis.app.itemchecker.ui.handheld.hw.impl.HalRfidReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ScanPanelPresenter implements ScanPanel.Presenter {

	private final static boolean OPTION_READ_TID = false;

	private LinkedList<Sighting> sightings;

	private Controller controller;

	private ScanPanel view;

	private ItemCheckerServiceAsync service;

	private Timer timer;

	/**
	 * Defines whether the last REST call is finished or is still running
	 */
	private boolean isAddSightingsReturned = true;

	/**
	 * Defines whether the last inventory is finished or is still running
	 */
	private boolean isInventoryReturned = true;

	private boolean scanning;

	private boolean awaitingRefresh;

	private ConfigDao config;

	private RfidReader rfidReader;

	public ScanPanelPresenter(Controller controller, ScanPanel view) {
		this.controller = controller;
		this.view = view;
		config = new HalConfigDao();
		rfidReader = new HalRfidReader();
		service = controller.getService();
	}

	@Override
	public void showSettingsPanel() {
		view.activateComposite(SettingsPanelView.class, null);
	}

	@Override
	public void updateUi() {
		service = controller.getService();
		config.getColumns(new AsyncCallback<List<Column>>() {

			@Override
			public void onSuccess(List<Column> result) {
				view.getColumns().setValue(result);
				refresh(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Failed to read column preferences. " + caught.getMessage());
			}
		});
	}

	@Override
	public void refresh(final boolean showMessage) {
		if (!awaitingRefresh) {
			// lock
			awaitingRefresh = true;
			if (showMessage) {
				controller.showMessage("Receiving items...");
			}
			// get items
			service.getItems(new MethodCallback<List<Item>>() {

				@Override
				public void onSuccess(Method method, List<Item> response) {
					// unlock
					awaitingRefresh = false;
					if (showMessage) {
						controller.showMessage("Received items.");
					}
					// paste items to ui
					view.getItems().setValue(response);
				}

				@Override
				public void onFailure(Method method, Throwable exception) {
					// unlock
					awaitingRefresh = false;
					// show message
					controller.showMessage("Failed to receive items. " + method.getResponse().getText());
				}
			});
		}
	}

	@Override
	public void switchScan() {
		if (scanning) {
			stopScan();
		} else {
			startScan();
		}
	}

	@Override
	public void startScan() {
		if (!scanning) {
			scanning = true;
			sightings = new LinkedList<Sighting>();

			timer = new Timer() {

				@Override
				public void run() {
					inventory();
					addSightings();
				}
			};
			timer.scheduleRepeating(1000);
		}
		view.getScanStatus().setValue(scanning);
	}

	@Override
	public void stopScan() {
		if (timer != null) {
			if (timer.isRunning()) {
				timer.cancel();
			}
			timer = null;
		}
		scanning = false;

		view.getScanStatus().setValue(scanning);
	}

	/**
	 * Read tags
	 */
	private void inventory() {
		if (isInventoryReturned) {
			// lock inventory
			isInventoryReturned = false;
			// start inventory
			rfidReader.inventory(OPTION_READ_TID, new AsyncCallback<List<Tag>>() {

				@Override
				public void onFailure(Throwable caught) {
					// show message
					controller.showMessage("Inventory failed: " + caught.getMessage());
					// unlock method for next inventory
					isInventoryReturned = true;
				}

				@Override
				public void onSuccess(List<Tag> result) {
					if (result != null && result.size() > 0) {
						for (int i = 0; i < result.size(); i++) {
							Tag tag = result.get(i);
							final Sighting sighting = new Sighting();
							sighting.setCode(tag.getEpc());

							if (OPTION_READ_TID) {
								String tid = "TODO";
								// TODO get TID from tag.getResults();
								sighting.setTid(tid);
							}
							// add seen tags to queue
							sightings.add(sighting);
						}
						// unlock method for next inventory
						isInventoryReturned = true;
					} else {
						// unlock method for next inventory
						isInventoryReturned = true;
					}
					if (scanning) {
						view.showPopUp("Scanning. Tags in field: " + result.size());
					}
				}
			});
		}
	}

	/**
	 * Report seen items to backend
	 */
	private void addSightings() {
		// lock method
		if (isAddSightingsReturned) {
			isAddSightingsReturned = false;
			// create list which shall be reported
			final List<Sighting> sightings = new ArrayList<Sighting>();
			while (this.sightings.size() > 0) {
				sightings.add(this.sightings.removeFirst());
			}
			if (sightings.size() > 0) {
				// report items
				service.addSighting(sightings, new MethodCallback<Void>() {

					@Override
					public void onFailure(Method method, Throwable exception) {
						// Failed to report sightings
						// show error message
						String error = method.getResponse().getText();
						if(error == null || "".equals(error.trim())) {
							error = "Failed to report tags. " + error;
						}
						controller.showMessage(error);
						// add sightings to list for retry
						ScanPanelPresenter.this.sightings.addAll(sightings);
						// unlock method for next request
						isAddSightingsReturned = true;
					}

					@Override
					public void onSuccess(Method method, Void response) {
						// Successfully reported sightings
						// refresh list
						refresh(false);
						// unlock method for next request
						isAddSightingsReturned = true;
					}
				});
			} else {
				// unlock method for next request
				isAddSightingsReturned = true;
			}
		}
	}
}
