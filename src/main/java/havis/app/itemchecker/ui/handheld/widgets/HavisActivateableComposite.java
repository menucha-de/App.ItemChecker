package havis.app.itemchecker.ui.handheld.widgets;

import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;

/**
 * Each offline displayable Component should extend this class. It provides
 * methods which are handled by {@link HavisOfflinePanel}.
 *
 */
public abstract class HavisActivateableComposite extends Composite {

	private HavisOfflinePanel controller;
	private boolean isStartPage;

	/**
	 * This method will be called, if the instance of this object will be
	 * activated
	 * 
	 * @param parameter
	 *            Parameter object, which can be used in the method.
	 */
	public abstract void onShow(HavisParameter parameter);

	/**
	 * This method will be called, if the instance of this object will be
	 * deactivated
	 */
	public abstract void onLeave();

	/**
	 * This method is called, if one of the hardware hotkeys is pressed.
	 * 
	 * @param key
	 *            pressed key
	 */
	public abstract void onKeyEvent(String key);
	
	/**
	 * This method is called, if one of the hardware hotkeys is pressed.
	 * 
	 * @param event
	 *            fired event
	 */
	public abstract void onPreviewNativeEvent(NativePreviewEvent event);

	public final void setStartPage(boolean isStartPage) {
		this.isStartPage = isStartPage;
	}

	public final boolean isStartPage() {
		return isStartPage;
	}

	/**
	 * @param controller
	 *            to be set {@see HavisOfflineCompositeController}
	 */
	final void setController(HavisOfflinePanel controller) {
		this.controller = controller;
	}

	/**
	 * Passes parameters to the callee and activates corresponding Composite.
	 * 
	 * @param clazz
	 *            to be set
	 * @param parameter
	 *            to be set
	 * 
	 * @return activated HavisActivateable
	 */
	public HavisActivateableComposite activateComposite(Class<? extends HavisActivateableComposite> clazz, Object parameter) {

		HavisParameter havisParameter = new HavisParameter(this, parameter);

		return this.controller.activatePanel(clazz, havisParameter);
	}
}
