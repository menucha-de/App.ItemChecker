package havis.custom.harting.itemchecker.ui.resourcebundle;

import com.google.gwt.resources.client.CssResource;

public interface CssResources extends CssResource {

	String label();

	@ClassName("clear")
	String clearButton();

	@ClassName("export")
	String exportButton();

	@ClassName("importP")
	String importButton();

	String imgStyle();

	String imgEmpty();

	String groupLbl();

	@ClassName("webui-ListBoxShort")
	String webuiListBoxShort();
	
	String commonLabel();
	
	@ClassName("webui-message-popup-panel")
	String webuiMessagePopupPanel();

	@ClassName("webui-message-popup-panel-error-dot")
	String webuiMessagePopupPanelErrorDot();
	
	// BELOW YOU WILL FIND THE VALUES FOR THE HANDHELD UI
	
	String fragment();
	String labelSpace();
	String title();
	@ClassName("webui-TextBox")
	String webuiTextBox();
	@ClassName("toolbar-information")
	String toolbarInformation();
	String yellow();
	
	String progress();	
	String progressbar();
	String progressGreen();
	String progressRed();
	String menuBar();
	@ClassName("settings-button")
	String settingsButton();
	@ClassName("refresh-button")
	String refreshButton();
	@ClassName("information-ok")
	String informationOk();
	@ClassName("information-esc")
	String informationEsc();
	@ClassName("message-info")
	String messageInfo();
	String popupPanel();
}