package havis.custom.harting.itemchecker.ui.handheld;

import java.util.ArrayList;
import java.util.List;

public class Column {
	private Columns column;
	private boolean enabled;
	private int width;
	private int position = -1;

	public Column(Columns column, boolean enabled, int width, int position) {
		this.column = column;
		this.enabled = enabled;
		this.width = width;
		this.position = position;
	}

	public Columns getColumn() {
		return column;
	}

	public void setColumn(Columns column) {
		this.column = column;
	}

	public String getTitle() {
		if (column != null) {
			return column.getTitle();
		}
		return null;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getPosition() {
		return position;
	}

	public static String toString(List<Column> columns) {
		String dbString = "";
		for (Column c : columns) {
			dbString += ";" + c.getColumn().toString();
			dbString += ":" + (c.isEnabled() ? 1 : 0);
			dbString += ":" + c.getWidth();
			dbString += ":" + c.getPosition();
		}
		return dbString.substring(1);
	}

	public static List<Column> fromString(String dbString) throws NullPointerException, IndexOutOfBoundsException {
		if (dbString == null) {
			throw new NullPointerException();
		}
		List<Column> result = new ArrayList<Column>();
		String[] columns = dbString.split(";");
		for (int i = 0; i < columns.length; i++) {
			String[] column = columns[i].split(":");
			Columns c = Columns.valueOf(column[0]);
			boolean enabled = "1".equals(column[1]);
			result.add(new Column(c, enabled, Integer.valueOf(column[2]), Integer.valueOf(column[3])));
		}
		return result;
	}
}
