package havis.custom.harting.itemchecker.ui.handheld;

import havis.custom.harting.itemchecker.ui.handheld.widgets.ColumnPreferences;
import havis.custom.harting.itemchecker.ui.handheld.widgets.HavisActivateableComposite;
import havis.custom.harting.itemchecker.ui.handheld.widgets.HavisParameter;
import havis.custom.harting.itemchecker.ui.handheld.widgets.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class SettingsPanelView extends HavisActivateableComposite implements SettingsPanel {

	private static SettingsPanelUiBinderUiBinder uiBinder = GWT.create(SettingsPanelUiBinderUiBinder.class);

	interface SettingsPanelUiBinderUiBinder extends UiBinder<Widget, SettingsPanelView> {
	}

	private SettingsPanel.Presenter presenter;
	@UiField
	protected TextBox gatewayHostTextBox;
	@UiField
	protected ToggleButton wlanStatusToggleButton;
	@UiField
	protected TextBox wlanStatusTextBox;
	@UiField
	protected ProgressBar batteryStatusProgressBar;
	@UiField
	protected TextBox versionTextBox;
	@UiField
	protected FlowPanel columnsFrame;

	private HasValue<Integer> batteryStatus = new HasValue<Integer>() {

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
			return new HandlerRegistration() {

				@Override
				public void removeHandler() {

				}
			};
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {

		}

		@Override
		public Integer getValue() {
			return null;
		}

		@Override
		public void setValue(Integer value) {
			setValue(value, false);
		}

		@Override
		public void setValue(Integer value, boolean fireEvents) {
			batteryStatusProgressBar.setProgress(value);
		}
	};

	private HasValue<List<Column>> columns = new HasValue<List<Column>>() {

		private List<Column> value;

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Column>> handler) {
			return new HandlerRegistration() {

				@Override
				public void removeHandler() {

				}
			};
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {

		}

		@Override
		public List<Column> getValue() {
			return value;
		}

		@Override
		public void setValue(List<Column> value) {
			setValue(value, false);
		}

		@Override
		public void setValue(List<Column> value, boolean fireEvents) {
			this.value = value;
			while (columnsFrame.getWidgetCount() > 0) {
				columnsFrame.remove(0);
			}
			columnPreferences.clear();
			for (Column c : value) {
				if (!c.getColumn().equals(Columns.GROUP) && !c.getColumn().equals(Columns.IMG)) {
					ColumnPreferences p = new ColumnPreferences(c);
					columnPreferences.add(p);
					columnsFrame.add(p);
				}
			}
		}
	};

	private List<ColumnPreferences> columnPreferences = new ArrayList<ColumnPreferences>();

	public SettingsPanelView(Controller controller) {
		initWidget(uiBinder.createAndBindUi(this));
		presenter = new SettingsPanelPresenter(controller, this);
	}

	@Override
	public void onShow(HavisParameter parameter) {
		presenter.refresh();
	}

	@Override
	public void onLeave() {

	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		int keyCode = event.getNativeEvent().getKeyCode();
		if (KeyCodes.KEY_ENTER == keyCode) {
			onOk(null);
		} else if (KeyCodes.KEY_ESCAPE == keyCode) {
			onEsc(null);
		}
	}

	@Override
	public void onKeyEvent(String key) {

	}

	@Override
	public HasValue<String> getHost() {
		return gatewayHostTextBox;
	}

	@Override
	public HasValue<Boolean> getWlanStatus() {
		return wlanStatusToggleButton;
	}

	@Override
	public HasValue<String> getWLanAddress() {
		return wlanStatusTextBox;
	}

	@Override
	public HasValue<String> getVersion() {
		return versionTextBox;
	}

	@UiHandler("okButton")
	protected void onOk(ClickEvent event) {
		presenter.save();
	}

	@UiHandler("escButton")
	protected void onEsc(ClickEvent event) {
		presenter.esc();
	}

	@Override
	public HasValue<Integer> getBatteryStatus() {
		return batteryStatus;
	}

	@Override
	public HasValue<List<Column>> getColumns() {
		return columns;
	}
}
