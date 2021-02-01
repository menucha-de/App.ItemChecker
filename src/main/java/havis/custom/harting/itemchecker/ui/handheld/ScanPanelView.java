package havis.custom.harting.itemchecker.ui.handheld;

import havis.custom.harting.itemchecker.Item;
import havis.custom.harting.itemchecker.ui.client.custom.ItemRow;
import havis.custom.harting.itemchecker.ui.handheld.widgets.HavisActivateableComposite;
import havis.custom.harting.itemchecker.ui.handheld.widgets.HavisParameter;
import havis.custom.harting.itemchecker.ui.handheld.widgets.Header;
import havis.custom.harting.itemchecker.ui.resourcebundle.AppResources;
import havis.custom.harting.itemchecker.ui.resourcebundle.ConstantsResource;
import havis.net.ui.shared.client.list.WidgetList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScanPanelView extends HavisActivateableComposite implements ScanPanel {

	private static SettingsPanelUiBinderUiBinder uiBinder = GWT.create(SettingsPanelUiBinderUiBinder.class);

	interface SettingsPanelUiBinderUiBinder extends UiBinder<Widget, ScanPanelView> {
	}

	private ScanPanel.Presenter presenter;

	@UiField
	protected Label popup;
	@UiField
	protected Header header;
	@UiField
	protected WidgetList tagsList;
	@UiField
	protected Label scanButton;
	@UiField
	InlineLabel countFound;
	@UiField
	InlineLabel countLeft;

	private int left = 0;

	private int found = 0;

	protected HasValue<List<Item>> items = new HasValue<List<Item>>() {

		private List<Item> value;

		@Override
		public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<Item>> handler) {
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
		public List<Item> getValue() {
			return value;
		}

		@Override
		public void setValue(List<Item> value) {
			setValue(value, false);
		}

		@Override
		public void setValue(List<Item> value, boolean fireEvents) {
			if (value == null) {
				value = new ArrayList<Item>();
			}
			this.value = value;
			tagsList.clear();
			found = 0;
			left = 0;
			List<Item> result = new ArrayList<Item>();
			List<Item> expected = new ArrayList<Item>();
			List<Item> unexpected = new ArrayList<Item>();
			List<Item> seen = new ArrayList<Item>();
			for (Item item : value) {
				if (item.getState() == 0) {
					found++;
					seen.add(item);
				} else if (item.getState() > 0) {
					found++;
					unexpected.add(item);
				} else {
					left++;
					expected.add(item);
				}
			}
			result.addAll(expected);
			result.addAll(unexpected);
			result.addAll(seen);
			HashMap<Columns, Boolean> map = new HashMap<Columns, Boolean>();
			for (Column c : columns.getValue()) {
				map.put(c.getColumn(), c.isEnabled());
			}
			for (Item item : result) {
				tagsList.addItem(new ItemRow("", item).getWidgets(map.get(Columns.GROUP), map.get(Columns.ID), map.get(Columns.CODE),
						map.get(Columns.DESCRIPTION), map.get(Columns.IMG)));
			}
			countFound.setText("" + found);
			countLeft.setText("" + left);
			resizeBody();
		}
	};

	private HasValue<List<Column>> columns = new HasValue<List<Column>>() {

		private List<Column> value = new ArrayList<Column>();

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
			if (value == null) {
				return new ArrayList<Column>();
			}
			return value;
		}

		@Override
		public void setValue(List<Column> value) {
			setValue(value, false);
		}

		@Override
		public void setValue(List<Column> value, boolean fireEvents) {
			this.value = value;
			clearTableLayout();
			for (Column c : value) {
				if (c.isEnabled()) {
					tagsList.addHeaderCell(c.getTitle());
				}
			}
			resizeHeader();
			resizeBody();
			items.setValue(items.getValue());
		}

		/**
		 * Removes the header, body content and the table column groups
		 */
		private void clearTableLayout() {
			// remove header
			tagsList.removeHeader();
			// remove column groups
			Node colgroup1 = tagsList.getElement().getChild(1).getChild(0).getChild(0);
			while (colgroup1.getChildCount() > 0) {
				colgroup1.removeChild(colgroup1.getChild(0));
			}

			// clear body
			tagsList.clear();
			Node colgroup2 = tagsList.getElement().getChild(1).getChild(1).getChild(0).getChild(0).getChild(0);
			while (colgroup2.getChildCount() > 0) {
				colgroup2.removeChild(colgroup2.getChild(0));
			}
		}
	};

	public ScanPanelView(Controller controller) {
		initWidget(uiBinder.createAndBindUi(this));

		// add button to go to settings view
		Button settingsButton = new Button();
		settingsButton.addStyleName(AppResources.INSTANCE.css().settingsButton());
		settingsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.showSettingsPanel();
			}
		});
		header.addButton(settingsButton);

		presenter = new ScanPanelPresenter(controller, this);
	}

	@Override
	public void onShow(HavisParameter parameter) {
		presenter.updateUi();
	}

	@Override
	public void onLeave() {

	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {

	}

	@Override
	public void onKeyEvent(String key) {
		if (key != null) {
			switch (key.toLowerCase()) {
			case "frontaction":
				onScanClick(null);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public HasValue<List<Item>> getItems() {
		return items;
	}

	@UiHandler("refresh")
	protected void onRefresh(ClickEvent event) {
		presenter.refresh(true);
	}

	@UiHandler("scanButton")
	public void onScanClick(ClickEvent event) {
		presenter.switchScan();
	}

	@Override
	public HasValue<Boolean> getScanStatus() {
		return new HasValue<Boolean>() {

			@Override
			public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
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
			public Boolean getValue() {
				return null;
			}

			@Override
			public void setValue(Boolean value) {
				setValue(value, false);
				if (value)
					showPopUp("Scanning. Tags in field: 0");
				else {
					hidePopUp();
				}
			}

			@Override
			public void setValue(Boolean value, boolean fireEvents) {
				scanButton.setText(value == true ? ConstantsResource.INSTANCE.stopScanning() : ConstantsResource.INSTANCE.startScanning());
			}
		};
	}

	@Override
	public HasValue<List<Column>> getColumns() {
		return columns;
	}

	@Override
	public void showPopUp(String message) {
		popup.setText(message);
		popup.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	public void hidePopUp() {
		popup.getElement().getStyle().setDisplay(Display.NONE);
	}

	/**
	 * Calculates the width of each column
	 * 
	 * @return
	 */
	public List<Integer> getColumnsWidth() {
		// width in px
		int freeWidth = 480;

		HashMap<Columns, Column> map = new HashMap<Columns, Column>();
		for (Column c : columns.getValue()) {
			map.put(c.getColumn(), c);
		}

		if (map.containsKey(Columns.IMG)) {
			freeWidth -= map.get(Columns.IMG).getWidth();
		}
		List<Integer> cols = new ArrayList<Integer>();
		for (int i = 0; i < columns.getValue().size(); i++) {
			cols.add(0);
		}
		int maxRelativeValue = 0;
		for (Column c : columns.getValue()) {
			int width = 0;
			if (c.isEnabled()) {
				width = c.getWidth() > 0 ? c.getWidth() : 1;
			}
			if (!Columns.IMG.equals(c.getColumn())) {
				maxRelativeValue += width;
			}
			cols.set(c.getPosition(), width);
		}
		for (int i = 0; i < cols.size() - 1; i++) {
			if (cols.get(i) > 0) {
				cols.set(i, (int) (freeWidth / 100.0 * (100.0 / maxRelativeValue * cols.get(i))));
			}
		}
		while (cols.remove(new Integer(0)))
			;
		return cols;
	}

	private void resizeHeader() {
		List<Integer> cols = getColumnsWidth();
		Node colgroup1 = tagsList.getElement().getChild(1).getChild(0).getChild(0);
		for (int i = 0; i < colgroup1.getChildCount(); i++) {
			if (colgroup1.getChild(i).getNodeType() == Node.ELEMENT_NODE) {
				if (i < cols.size() - 1) {
					((Element) colgroup1.getChild(i)).getStyle().setWidth(cols.get(i), Unit.PX);
				}
			}
		}
	}

	private void resizeBody() {
		List<Integer> cols = getColumnsWidth();
		Node colgroup2 = tagsList.getElement().getChild(1).getChild(1).getChild(0).getChild(0).getChild(0);
		for (int i = 0; i < colgroup2.getChildCount(); i++) {
			if (colgroup2.getChild(i).getNodeType() == Node.ELEMENT_NODE) {
				if (i < cols.size() - 1) {
					((Element) colgroup2.getChild(i)).getStyle().setWidth(cols.get(i), Unit.PX);
				}
			}
		}
	}

}
