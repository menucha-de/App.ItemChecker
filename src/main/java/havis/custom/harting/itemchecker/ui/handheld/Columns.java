package havis.custom.harting.itemchecker.ui.handheld;

public enum Columns {
	GROUP("Group"),
	ID("ID"),
	CODE("Code"),
	DESCRIPTION("Description"),
	IMG("");

	private String title;

	private Columns(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
