package havis.app.itemchecker.ui.handheld.widgets;

import havis.application.common.event.KeyEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main offline Panel, that contains all other Panels (as childs) which might be
 * shown in this offline application. It handles the activation and deactivation
 * of current "application Mask". Also it provides possibility for writing
 * messages on the area of activated Panel.
 * 
 */
public class HavisOfflinePanel extends Composite implements HasWidgets, KeyEventListener, NativePreviewHandler {

	private HashMap<Class<? extends HavisActivateableComposite>, HavisActivateableComposite> composites = new HashMap<Class<? extends HavisActivateableComposite>, HavisActivateableComposite>();
	private HavisActivateableComposite currentActivateableComposite;

	private static RootComponentControllerUiBinder uiBinder = GWT.create(RootComponentControllerUiBinder.class);

	interface RootComponentControllerUiBinder extends UiBinder<Widget, HavisOfflinePanel> {
	}

	@UiField
	protected SimplePanel placeholderPanel;

	public HavisOfflinePanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	protected Widget getWidget() {
		return placeholderPanel.getWidget();
	};

	@Override
	protected void onLoad() {
		super.onLoad();
		initComponents();
	}

	@Override
	protected void setWidget(Widget widget) {
		placeholderPanel.setWidget(widget);
	}

	@Override
	public void add(Widget w) {
		setWidget(w);
	}

	@Override
	public void clear() {
		setWidget(null);
	}

	@Override
	public Iterator<Widget> iterator() {
		return Arrays.asList(getWidget()).iterator();
	}

	@Override
	public boolean remove(Widget w) {
		if (getWidget() == w) {
			clear();

			return true;
		}

		return false;
	}

	/**
	 * For activating of other application "Panel", you should call this method.
	 * 
	 * @param clazz
	 *            This parameter specifies which application form/panel should
	 *            be called.
	 * @param parameter
	 *            Parameters to be passed to the next form/panel
	 * 
	 * @return activated form/panel
	 */
	public HavisActivateableComposite activatePanel(Class<? extends HavisActivateableComposite> clazz, HavisParameter parameter) {

		HavisActivateableComposite composite = null;

		try {

			for (HavisActivateableComposite activatable : composites.values()) {
				if (activatable instanceof HavisActivateableComposite) {
					HavisActivateableComposite activateableComposite = (HavisActivateableComposite) activatable;

					if (activateableComposite.getClass().equals(clazz)) {
						activateableComposite.setVisible(true);
						activateableComposite.onShow(parameter);

						composite = activateableComposite;
					} else {
						activateableComposite.setVisible(false);
						activateableComposite.onLeave();
					}
				}
			}

		} catch (final Exception exc) {
			Window.alert(exc.getMessage());
		}

		currentActivateableComposite = composite;

		return composite;
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event) {
		if (currentActivateableComposite != null) {
			currentActivateableComposite.onPreviewNativeEvent(event);
		}
	}

	@Override
	public void onKeyEvent(String key) {
		if (currentActivateableComposite != null) {
			currentActivateableComposite.onKeyEvent(key);
		}
	}

	private void initComponents() {
		Iterator<Widget> widgets = iterator();
		while (widgets.hasNext()) {
			Widget widget = widgets.next();

			if (widget instanceof ComplexPanel) {

				ComplexPanel complexPanel = (ComplexPanel) widget;

				Iterator<Widget> childWidgets = complexPanel.iterator();

				while (childWidgets.hasNext()) {

					Widget childWidget = childWidgets.next();
					if (childWidget instanceof HavisActivateableComposite) {
						HavisActivateableComposite activateableComposite = (HavisActivateableComposite) childWidget;
						if (!composites.containsKey(widget.getClass())) {
							activateableComposite.setController(this);
							composites.put(activateableComposite.getClass(), activateableComposite);
							if (activateableComposite.isStartPage()) {
								currentActivateableComposite = activateableComposite;
								activateableComposite.onShow(null);
							}
						}
					}
				}
			}
		}
	}
}
