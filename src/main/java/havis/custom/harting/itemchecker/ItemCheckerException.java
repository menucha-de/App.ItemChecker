package havis.custom.harting.itemchecker;

public class ItemCheckerException extends Exception {

	private static final long serialVersionUID = 1L;

	public ItemCheckerException(String message) {
		super(message);
	}

	public ItemCheckerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemCheckerException(Throwable cause) {
		super(cause);
	}
}