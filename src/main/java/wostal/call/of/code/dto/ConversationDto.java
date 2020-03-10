package wostal.call.of.code.dto;

import java.io.Serializable;
import java.util.TreeSet;

public class ConversationDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1383658870847908701L;
	private TreeSet<String> nicks = new TreeSet<String>();
	private String name;
	
	public TreeSet<String> getNicks() {
		return nicks;
	}
	public void setNicks(TreeSet<String> nicks) {
		this.nicks = nicks;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
