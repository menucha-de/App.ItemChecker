package havis.app.itemchecker;


public class Configuration {
	private Encoding encoding = Encoding.EPC_TAG;
	private boolean quantity = false;
	private String delimiter = "EXCEL_NORTH_EUROPE_PREFERENCE";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	public boolean isQuantity() {
		return quantity;
	}

	public void setQuantity(boolean quantity) {
		this.quantity = quantity;
	}
}
