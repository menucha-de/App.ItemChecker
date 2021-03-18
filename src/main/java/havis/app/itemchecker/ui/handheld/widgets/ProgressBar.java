package havis.app.itemchecker.ui.handheld.widgets;

import havis.app.itemchecker.ui.resourcebundle.AppResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBar extends Composite {
	private static ProgressBarUiBinder uiBinder = GWT.create(ProgressBarUiBinder.class);

	interface ProgressBarUiBinder extends UiBinder<Widget, ProgressBar> {
	}

	@UiField
	protected Label progressLabel;

	@UiField
	protected FlowPanel bar;

	public ProgressBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setProgress(final int value) {

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				progressLabel.setText(value + "%");
				double margin = 4;
				double width = (bar.getOffsetWidth() - 19 * margin) / 20;

				while (bar.getWidgetCount() > 0) {
					bar.remove(0);
				}
				int level = value / 5;

				for (int i = 0; i < 20; i++) {
					SimplePanel scale = new SimplePanel();
					scale.setWidth(width + "px");
					if (i != 19) {
						scale.getElement().getStyle().setMarginRight(margin, Unit.PX);
					}
					if (i < level) {
						if (value < 11) {
							scale.addStyleName(AppResources.INSTANCE.css().progressRed());
						} else {
							scale.addStyleName(AppResources.INSTANCE.css().progressGreen());
						}
					}
					bar.add(scale);
				}
			}
		});

	}

}
