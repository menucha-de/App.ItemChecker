package havis.custom.harting.itemchecker.ui.client.custom;

import havis.net.ui.shared.client.event.MessageEvent.MessageType;
import havis.net.ui.shared.client.widgets.CustomMessageWidget;

import org.fusesource.restygwt.client.FailedResponseException;

import com.google.gwt.http.client.Response;

public class ErrorViewer {
	public static void showExceptionResponse(Throwable exception) {
		String result = exception.getMessage();
		if (exception instanceof FailedResponseException) {
			Response response = ((FailedResponseException) exception).getResponse();
			result = response.getText();
		}
		CustomMessageWidget errorPanel = new CustomMessageWidget();
		errorPanel.showMessage(result, MessageType.ERROR);
	}
}
