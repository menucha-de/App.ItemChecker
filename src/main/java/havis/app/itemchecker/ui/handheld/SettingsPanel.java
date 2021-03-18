package havis.app.itemchecker.ui.handheld;

import havis.app.itemchecker.ui.handheld.widgets.HavisActivateableComposite;

import java.util.List;

import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HasValue;

public interface SettingsPanel extends NativePreviewHandler {

	HasValue<String> getHost();

	HasValue<Boolean> getWlanStatus();

	HasValue<String> getWLanAddress();

	HasValue<String> getVersion();

	HasValue<Integer> getBatteryStatus();

	HasValue<List<Column>> getColumns();

	HavisActivateableComposite activateComposite(Class<? extends HavisActivateableComposite> clazz, Object parameter);

	public interface Presenter {

		void refresh();

		void save();

		void esc();
	}
}
