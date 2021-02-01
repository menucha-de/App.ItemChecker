package havis.custom.harting.itemchecker.ui.client;

import havis.custom.harting.itemchecker.rest.async.ItemCheckerServiceAsync;
import havis.custom.harting.itemchecker.ui.client.custom.ErrorViewer;
import havis.custom.harting.itemchecker.ui.resourcebundle.AppResources;
import havis.net.ui.shared.client.ConfigurationSection;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class ConfigSection extends ConfigurationSection {
	private AppResources res = AppResources.INSTANCE;
	private static ConfigSectionUiBinder uiBinder = GWT.create(ConfigSectionUiBinder.class);

	interface ConfigSectionUiBinder extends UiBinder<Widget, ConfigSection> {
	}

	private ItemCheckerServiceAsync service = GWT.create(ItemCheckerServiceAsync.class);

	private ItemCheckerSection itemChecker;

	@UiField
	ListBox encoding;

	@UiField
	ListBox delimiter;

	@UiConstructor
	public ConfigSection(String name) {
		super(name);
		initWidget(uiBinder.createAndBindUi(this));
		res.css().ensureInjected();
	}

	@Override
	protected void onOpenSection() {
		super.onOpenSection();
		service.getEncoding(new TextCallback() {
			@Override
			public void onSuccess(Method method, String response) {
				for (int i = 0; i < encoding.getItemCount(); i++) {
					if (encoding.getValue(i).equals(response)) {
						encoding.setSelectedIndex(i);
						break;
					}
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
			}
		});

		service.getDelimiter(new TextCallback() {
			@Override
			public void onSuccess(Method method, String response) {
				for (int i = 0; i < delimiter.getItemCount(); i++) {
					if (delimiter.getValue(i).equals(response)) {
						delimiter.setSelectedIndex(i);
						break;
					}
				}
			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}
		});
	}

	@UiHandler("encoding")
	void onEncodingChange(ChangeEvent event) {
		service.setEncoding(encoding.getSelectedValue(), new MethodCallback<Void>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				if (itemChecker != null) {
					itemChecker.clear();
				}
			}
		});
	}

	@UiHandler("delimiter")
	void onDelimiterChange(ChangeEvent event) {
		service.setDelimiter(delimiter.getSelectedValue(), new MethodCallback<Void>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}

			@Override
			public void onSuccess(Method method, Void response) {
			}
		});
	}

	public void setItemChecker(ItemCheckerSection itemChecker) {
		this.itemChecker = itemChecker;
	}

}
