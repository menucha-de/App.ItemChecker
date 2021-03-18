package havis.app.itemchecker.ui.handheld;

import havis.app.itemchecker.rest.async.ItemCheckerServiceAsync;
import havis.app.itemchecker.ui.handheld.hw.ConfigDao;
import havis.app.itemchecker.ui.handheld.hw.Keyboard;
import havis.app.itemchecker.ui.handheld.hw.impl.HalConfigDao;
import havis.app.itemchecker.ui.handheld.hw.impl.HalKeyboard;
import havis.app.itemchecker.ui.handheld.widgets.HavisOfflinePanel;
import havis.app.itemchecker.ui.resourcebundle.AppResources;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import javax.ws.rs.core.HttpHeaders;

import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.dispatcher.DefaultFilterawareDispatcher;
import org.fusesource.restygwt.client.dispatcher.DispatcherFilter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import elemental.client.Browser;

public class WebUIHandheld extends Composite implements EntryPoint, Controller {

	private static WebUIHandheldUiBinder uiBinder = GWT.create(WebUIHandheldUiBinder.class);

	@UiTemplate("WebUIHandheld.ui.xml")
	interface WebUIHandheldUiBinder extends UiBinder<Widget, WebUIHandheld> {
	}

	private ResourceBundle res = ResourceBundle.INSTANCE;

	private AppResources appRes = AppResources.INSTANCE;

	/**
	 * the time span of shown messages in milliseconds
	 */
	private final static int MESSAGE_TIME_SPAN = 2250;

	private final static RegExp userInfoExpression = RegExp.compile("https*:\\/\\/*(\\w+:*\\w*)@.+");

	private Keyboard keyboard;
	private ConfigDao config;
	private ItemCheckerServiceAsync service = GWT.create(ItemCheckerServiceAsync.class);

	@UiField
	protected HavisOfflinePanel rootPanel;
	@UiField(provided = true)
	protected ScanPanelView scanPanel;
	@UiField(provided = true)
	protected SettingsPanelView settingsPanel;
	@UiField
	protected Label messagePanel;

	private Timer messageTimer;

	public WebUIHandheld() {
		scanPanel = new ScanPanelView(this);
		settingsPanel = new SettingsPanelView(this);

		initWidget(uiBinder.createAndBindUi(this));
		Defaults.setDateFormat(null);
		ensureInjection();

		// Add possibility to see complete message
		messagePanel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (messagePanel.getText() != null && !"".equals(messagePanel.getText().trim())) {
					Window.alert(messagePanel.getText());
				}
			}
		});

		// init gateway host
		config = new HalConfigDao();
		updateGateway(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				showMessage("Failed to load gateway. " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {

			}
		});

		// initialize key events
		Event.addNativePreviewHandler(rootPanel);
		keyboard = new HalKeyboard();
		keyboard.setKeyEventListener(rootPanel, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
			}

			@Override
			public void onFailure(Throwable caught) {
				showMessage(caught.getMessage());
			}
		});
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel.get().add(this);
	}

	private void ensureInjection() {
		res.css().ensureInjected();
		appRes.css().ensureInjected();
	}

	@Override
	public void showMessage(String message) {
		if (messageTimer != null) {
			if (messageTimer.isRunning()) {
				messageTimer.cancel();
			}
			messageTimer = null;
		}
		messagePanel.setText(message);
		messagePanel.removeStyleName(AppResources.INSTANCE.css().messageInfo());
		messagePanel.addStyleName(AppResources.INSTANCE.css().messageInfo());
		if (MESSAGE_TIME_SPAN > 0) {
			messageTimer = new Timer() {

				@Override
				public void run() {
					messagePanel.setText("");
					messagePanel.removeStyleName(AppResources.INSTANCE.css().messageInfo());
				}
			};
			messageTimer.schedule(MESSAGE_TIME_SPAN);
		}
	}

	@Override
	public void updateGateway(final AsyncCallback<Void> callback) {
		service = GWT.create(ItemCheckerServiceAsync.class);
		config.getHost(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String gatewayHost) {
				if (gatewayHost == null) {
					gatewayHost = "";
				}
				if (!gatewayHost.startsWith("http:") && !gatewayHost.startsWith("https:")) {
					gatewayHost = "https://" + gatewayHost + "/Apps/rest";
				}
				final String url = gatewayHost;
				Defaults.setServiceRoot(url);
				Defaults.setDispatcher(new DefaultFilterawareDispatcher() {
					{
						addFilter(new DispatcherFilter() {

							@Override
							public boolean filter(Method method, RequestBuilder builder) {
								String userInfo = extractUserInfo(url);
								if (userInfo != null) {
									builder.setHeader(HttpHeaders.AUTHORIZATION, encodeHeaderValue(userInfo));
								}
								return true;
							}

							private String extractUserInfo(String url) {
								if (url != null && url.length() > 0) {
									MatchResult match = userInfoExpression.exec(url);
									if (match != null) {
										return match.getGroup(1);
									}
								}
								return null;
							}

							private String encodeHeaderValue(String userInfo) {
								return "Basic " + Browser.getWindow().btoa(userInfo);
							}
						});
					}
				});				
				callback.onSuccess(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});

	}

	@Override
	public ItemCheckerServiceAsync getService() {
		return service;
	}

}