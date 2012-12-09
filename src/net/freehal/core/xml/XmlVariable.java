package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.List;

public class XmlVariable extends XmlWord {

	/** A regular expression that can be used to match a variable */
	public static final String REGEX_VARIABLE = "(\\$[a-zA-Z0-9]+\\$)";
	
	private List<XmlObj> before = new ArrayList<XmlObj>();
	
	private List<XmlObj> after = new ArrayList<XmlObj>();

	public List<XmlObj> getBefore() {
		return before;
	}

	public void setBefore(List<XmlObj> before) {
		this.before = before;
	}

	public List<XmlObj> getAfter() {
		return after;
	}

	public void setAfter(List<XmlObj> after) {
		this.after = after;
	}

}
