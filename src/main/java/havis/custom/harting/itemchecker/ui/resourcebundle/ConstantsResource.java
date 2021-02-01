package havis.custom.harting.itemchecker.ui.resourcebundle;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Constants;

public interface ConstantsResource extends Constants {

	public static final ConstantsResource INSTANCE = GWT.create(ConstantsResource.class);

	String header();
	String startScanning();
	String stopScanning();
	String found();
	String left();
	
	// BELOW YOU WILL FIND THE VALUES FOR THE HANDHELD UI
	
	String version();
	String informationLabel();
	String gatewayHostLabel();
	String wlanStatusLabel();
	String batteryStatusLabel();
	String versionLabel();
	String okButtonLabel();
	String escButtonLabel();
	String itemCheckerLabel();
	String columnsLabel();
	
}