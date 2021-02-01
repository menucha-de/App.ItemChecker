package havis.custom.harting.itemchecker.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface AppResources extends ClientBundle {

	public static final AppResources INSTANCE = GWT.create(AppResources.class);

	@Source("resources/CssResources.css")
	CssResources css();

	@Source("resources/close.png")
	ImageResource close();

	@Source("resources/export.png")
	ImageResource export();

	@Source("resources/clear.png")
	ImageResource clear();

	@Source("resources/import.png")
	ImageResource importP();

	@Source("resources/added.png")
	ImageResource added();

	@Source("resources/found.png")
	ImageResource found();

	@Source("resources/left.png")
	ImageResource left();

	@Source("resources/icon_error.png")
	ImageResource errorIcon();

	// BELOW YOU WILL FIND THE VALUES FOR THE HANDHELD UI

	@Source("resources/LOGO.png")
	DataResource logo();

	@Source("resources/CONTENT_Switch_On.png")
	DataResource contentSwitchOn();

	@Source("resources/CONTENT_Switch_Off.png")
	DataResource contentSwitchOff();

	@Source("resources/Preferences.png")
	ImageResource preferencesIcon();

	@Source("resources/list_refresh.png")
	ImageResource refreshIcon();

	@Source("resources/info.png")
	ImageResource infoIcon();
}