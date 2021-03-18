package havis.app.itemchecker.ui.client.custom;

import havis.app.itemchecker.Item;
import havis.app.itemchecker.ui.resourcebundle.AppResources;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ItemRow {
	private static final Widget[] WIDGET_TYPE = new Widget[] {};
	private Label group;
	private Label id;
	private Label code;
	private Label description;
	private Image img;

	// private Image img;

	public ItemRow(String group, Item entry) {
		this.group = new Label(group);
		this.group.addStyleName(AppResources.INSTANCE.css().groupLbl());
		this.id = new Label(entry.getId());
		this.id.setTitle(entry.getId());
		this.code = new Label(entry.getCode());
		this.code.setTitle(entry.getCode());
		this.description = new Label(entry.getDescription());
		this.description.setTitle(entry.getDescription());

		if (entry.getState() > 0) {
			this.img = new Image(AppResources.INSTANCE.added());
			this.img.setTitle("added");
		} else if (entry.getState() == 0) {
			this.img = new Image(AppResources.INSTANCE.found());
			this.img.setTitle("found");
		} else {
			this.img = new Image(AppResources.INSTANCE.left());
			this.img.setTitle("left");
		}
		this.img.addStyleName(AppResources.INSTANCE.css().imgStyle());
	}

	public Widget[] getWidgets() {
		return getWidgets(true, true, true, true, true);		
	}
	
	public Widget[] getWidgets(boolean group, boolean id, boolean code, boolean description, boolean img) {
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		if(group)
			widgets.add(this.group);
		if(id)
			widgets.add(this.id);
		if(code)
			widgets.add(this.code);
		if(description)
			widgets.add(this.description);
		if(img)
			widgets.add(this.img);
		return widgets.toArray(WIDGET_TYPE);
	}
}
