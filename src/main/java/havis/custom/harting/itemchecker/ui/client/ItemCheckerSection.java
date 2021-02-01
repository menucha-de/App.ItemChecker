package havis.custom.harting.itemchecker.ui.client;

import havis.custom.harting.itemchecker.Item;
import havis.custom.harting.itemchecker.rest.async.ItemCheckerServiceAsync;
import havis.custom.harting.itemchecker.ui.client.custom.ErrorViewer;
import havis.custom.harting.itemchecker.ui.client.custom.ItemRow;
import havis.custom.harting.itemchecker.ui.resourcebundle.AppResources;
import havis.net.ui.shared.client.list.WidgetList;
import havis.net.ui.shared.client.upload.File;
import havis.net.ui.shared.client.upload.FileList;
import havis.net.ui.shared.client.upload.MultipleFileUpload;

import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.TextCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.xml.XMLHttpRequest;

public class ItemCheckerSection extends Composite {
	private AppResources res = AppResources.INSTANCE;
	private static ItemCheckerUiBinder uiBinder = GWT.create(ItemCheckerUiBinder.class);

	private ItemCheckerServiceAsync service = GWT.create(ItemCheckerServiceAsync.class);

	private static final String[] FIELD_LABELS = new String[] { "Group", "ID", "Code", "Description", "" };
	private static final String EXPORT_LINK = GWT.getHostPageBaseURL() + "rest/itemchecker/itemlist/file";

	private Timer timer;

	interface ItemCheckerUiBinder extends UiBinder<Widget, ItemCheckerSection> {
	}

	@UiField
	WidgetList tagsList;

	@UiField
	ToggleButton monitorButton;

	@UiField
	MultipleFileUpload upload;

	@UiField
	InlineLabel countFound;

	@UiField
	InlineLabel countLeft;

	private int left = 0;
	private int found = 0;

	private String lastGroup = "";

	public ItemCheckerSection() {
		initWidget(uiBinder.createAndBindUi(this));
		res.css().ensureInjected();
		setListHeader();
		service.getActive(new TextCallback() {

			@Override
			public void onSuccess(Method method, String response) {
				boolean active = Boolean.parseBoolean(response);
				monitorButton.setDown(active);
				if (active) {
					startScan();
				}

			}

			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}
		});
		getItems();
	}
	
	public void clear() {
		tagsList.clear();
		countFound.setText("0");
		countLeft.setText("0");
	}

	private void setListHeader() {
		for (String item : FIELD_LABELS) {
			tagsList.addHeaderCell(item);
		}
	}

	@UiHandler("monitorButton")
	public void onMonitorClick(ClickEvent event) {
		if (monitorButton.isDown()) {
			service.setActive("" + true, new MethodCallback<Void>() {
				@Override
				public void onFailure(Method method, Throwable exception) {
					ErrorViewer.showExceptionResponse(exception);
				}

				@Override
				public void onSuccess(Method method, Void response) {
					startScan();
				}
			});
		} else {
			service.setActive("" + false, new MethodCallback<Void>() {
				@Override
				public void onFailure(Method method, Throwable exception) {
					ErrorViewer.showExceptionResponse(exception);
				}

				@Override
				public void onSuccess(Method method, Void response) {
					stopScan();
				}
			});
		}
	}

	@UiHandler("upload")
	void onChooseFile(ChangeEvent event) {
		FileList fl = upload.getFileList();
		File file = fl.html5Item(0);
		final XMLHttpRequest xhr = Browser.getWindow().newXMLHttpRequest();
		xhr.open("POST", GWT.getHostPageBaseURL() + "rest/itemchecker/itemlist/file");
		xhr.setRequestHeader("Content-Type", "application/octet-stream");
		// The line below is only required for development purposes 
		xhr.setRequestHeader("Authorization", "Basic " + Browser.getWindow().btoa("admin:"));

		xhr.addEventListener("load", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				int status = xhr.getStatus();
				if (status == 204) {
					getItems();
				} else if(status == 400){
					ErrorViewer.showExceptionResponse(new Exception("Bad Request"));
				} else if(status == 500){
					ErrorViewer.showExceptionResponse(new Exception("Server Error"));
				}
			}
		}, false);
		xhr.addEventListener("error", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				ErrorViewer.showExceptionResponse(new Exception("Error in request"));
			}
		}, false);
		xhr.addEventListener("abort", new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				//ignore
			}
		}, false);
		xhr.send(file);
		upload.reset();
	}

	@UiHandler("export")
	void onExport(ClickEvent event) {
		Window.Location.assign(EXPORT_LINK);
	}

	@UiHandler("importP")
	void onImport(ClickEvent event) {
		upload.setAccept(".csv");
		upload.click();
	}

	@UiHandler("clear")
	void onClear(ClickEvent event) {
		service.clear(new MethodCallback<Void>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}

			@Override
			public void onSuccess(Method method, Void response) {
				clear();
			}
		});
	}

	private void startScan() {
		timer = new Timer() {
			@Override
			public void run() {
				getItems();

			}
		};
		timer.scheduleRepeating(1000);
	}

	private void stopScan() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private void getItems() {
		service.getItems(new MethodCallback<List<Item>>() {
			@Override
			public void onFailure(Method method, Throwable exception) {
				ErrorViewer.showExceptionResponse(exception);
			}

			@Override
			public void onSuccess(Method method, List<Item> response) {
				tagsList.clear();
				lastGroup = "";
				found = 0;
				left = 0;
				for (Item item : response) {
					if (item.getState() == 0) {
						found++;
					} else if (item.getState() > 0) {
						found++;
					} else {
						left++;
					}
					insertRow(item);
				}
				countFound.setText("" + found);
				countLeft.setText("" + left);

			}
		});
	}

	private void insertRow(Item item) {
		String code = item.getCode();
		String[] elements = code.split(":");
		String group = "";
		if (elements.length > 3) {
			if (elements[2].equals("raw")) {
				group = "raw";
			} else {
				group = elements[3].split("-")[0];
			}
		}

		if (!lastGroup.equals(group)) {
			tagsList.addItem(new ItemRow(group.toUpperCase(), item).getWidgets());
			lastGroup = group;
		} else {
			tagsList.addItem(new ItemRow("", item).getWidgets());
		}
	}

}
