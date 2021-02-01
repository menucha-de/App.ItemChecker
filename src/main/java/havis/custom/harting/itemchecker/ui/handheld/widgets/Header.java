package havis.custom.harting.itemchecker.ui.handheld.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

	private static HeaderUiBinder uiBinder = GWT.create(HeaderUiBinder.class);

	interface HeaderUiBinder extends UiBinder<Widget, Header> {
	}

	@UiField
	protected HorizontalPanel header;
	@UiField
	protected Label title;
	@UiField
	protected HorizontalPanel buttons;

	public Header() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiConstructor
	public Header(String title) {
		initWidget(uiBinder.createAndBindUi(this));
		setTitle(title);
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void addButton(Widget widget) {
		buttons.add(widget);
	}
}
