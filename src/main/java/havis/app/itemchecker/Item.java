package havis.app.itemchecker;

import java.util.List;

public class Item {
	private String id;
	private String code;
	private String description;
	private int count;
	private int state;
	private List<String> tids;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public List<String> getTids() {
		return tids;
	}
	public void setTids(List<String> tids) {
		this.tids = tids;
	}	
}
