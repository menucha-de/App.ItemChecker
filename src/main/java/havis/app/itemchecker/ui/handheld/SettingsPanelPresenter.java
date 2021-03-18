package havis.app.itemchecker.ui.handheld;

import havis.app.itemchecker.ui.handheld.hw.Capabilities;
import havis.app.itemchecker.ui.handheld.hw.ConfigDao;
import havis.app.itemchecker.ui.handheld.hw.impl.HalCapabilities;
import havis.app.itemchecker.ui.handheld.hw.impl.HalConfigDao;
import havis.app.itemchecker.ui.resourcebundle.ConstantsResource;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SettingsPanelPresenter implements SettingsPanel.Presenter {

	private Controller controller;

	private SettingsPanel view;

	private ConfigDao config;
	private Capabilities capabilities;

	public SettingsPanelPresenter(Controller controller, SettingsPanel view) {
		this.config = new HalConfigDao();
		this.capabilities = new HalCapabilities();
		this.controller = controller;
		this.view = view;
	}

	@Override
	public void refresh() {
		config.getHost(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Failed to read host. " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				view.getHost().setValue(result);
			}
		});

		capabilities.getWLanConnectionState(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Faild to read WLAN state. " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				view.getWlanStatus().setValue(result.booleanValue());
				if (result.booleanValue()) {
					capabilities.getWLanAddress(new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							controller.showMessage("Failed to read WLAN address: " + caught.getMessage());
						}

						@Override
						public void onSuccess(String result) {
							view.getWLanAddress().setValue(result);
						}

					});
				} else {
					capabilities.getLanAddress(new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							controller.showMessage("Failed to read LAN address. " + caught.getMessage());
						}

						@Override
						public void onSuccess(String result) {
							view.getWLanAddress().setValue(result);
						}
					});
				}
			}
		});

		capabilities.getChargeCondition(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Failed to read charge condition. " + caught.getMessage());
			}

			@Override
			public void onSuccess(Integer result) {
				view.getBatteryStatus().setValue(result);
			}
		});

		view.getVersion().setValue(ConstantsResource.INSTANCE.version());

		config.getColumns(new AsyncCallback<List<Column>>() {

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Failed to read column preferences. " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Column> result) {
				view.getColumns().setValue(result);
			}
		});

	}

	@Override
	public void save() {
		config.setHost(view.getHost().getValue(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				config.setColumns(view.getColumns().getValue(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						controller.showMessage("Failed to save columns. " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						controller.updateGateway(new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								controller.showMessage("Failed to update gateway. " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								view.activateComposite(ScanPanelView.class, null);
							}
						});
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				controller.showMessage("Failed to save host. " + caught.getMessage());
			}
		});
	}

	@Override
	public void esc() {
		view.activateComposite(ScanPanelView.class, null);
	}
}
