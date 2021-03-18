package havis.app.itemchecker.ui.handheld;

import havis.app.itemchecker.Item;
import havis.app.itemchecker.ui.handheld.widgets.HavisActivateableComposite;

import java.util.List;

import com.google.gwt.user.client.ui.HasValue;

public interface ScanPanel {
	HasValue<List<Item>> getItems();

	HasValue<Boolean> getScanStatus();

	HasValue<List<Column>> getColumns();

	void showPopUp(String message);

	HavisActivateableComposite activateComposite(Class<? extends HavisActivateableComposite> clazz, Object parameter);

	public interface Presenter {

		void showSettingsPanel();

		void updateUi();

		void refresh(boolean showMessage);

		void switchScan();

		void startScan();

		void stopScan();
	}
}
