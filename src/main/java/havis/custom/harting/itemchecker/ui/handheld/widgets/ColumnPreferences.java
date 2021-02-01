package havis.custom.harting.itemchecker.ui.handheld.widgets;

import havis.custom.harting.itemchecker.ui.handheld.Column;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ColumnPreferences extends Composite {

	private static ColumnPreferencesUiBinder uiBinder = GWT.create(ColumnPreferencesUiBinder.class);

	interface ColumnPreferencesUiBinder extends UiBinder<Widget, ColumnPreferences> {
	}

	@UiField
	protected ToggleButton enabled;
	@UiField
	protected Label name;
	@UiField
	protected IntegerBox relativeWitdh;

	private Column column;

	public ColumnPreferences(Column column) {
		this.column = column;
		initWidget(uiBinder.createAndBindUi(this));
		this.name.setText(column.getTitle());
		this.enabled.setDown(column.isEnabled());
		this.relativeWitdh.setValue(column.getWidth());
	}
	
	@UiHandler("relativeWitdh")
	protected void onWidthChange(ValueChangeEvent<Integer> e){
		column.setWidth(relativeWitdh.getValue() != null ? relativeWitdh.getValue() : 0);
	}
	
	@UiHandler("enabled")
	protected void onEnable(ClickEvent e){
		column.setEnabled(enabled.isDown());
	}
}
