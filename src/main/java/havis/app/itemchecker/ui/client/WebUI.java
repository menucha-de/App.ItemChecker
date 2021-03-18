package havis.app.itemchecker.ui.client;

import havis.app.itemchecker.ui.resourcebundle.AppResources;
import havis.net.ui.shared.resourcebundle.ResourceBundle;

import org.fusesource.restygwt.client.Defaults;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class WebUI extends Composite implements EntryPoint {

	@UiField
	FlowPanel container;

	@UiField
	ConfigSection config;

	@UiField
	ItemCheckerSection items;

	private ResourceBundle res = ResourceBundle.INSTANCE;
	private AppResources appRes = AppResources.INSTANCE;

	private static WebUIUiBinder uiBinder = GWT.create(WebUIUiBinder.class);

	@UiTemplate("WebUI.ui.xml")
	interface WebUIUiBinder extends UiBinder<Widget, WebUI> {
	}

	public WebUI() {
		initWidget(uiBinder.createAndBindUi(this));
		Defaults.setDateFormat(null);
		ensureInjection();
		config.setItemChecker(items);
	}

	@Override
	public void onModuleLoad() {
		RootLayoutPanel.get().add(this);
	}

	private void ensureInjection() {
		res.css().ensureInjected();
		appRes.css().ensureInjected();
	}
}