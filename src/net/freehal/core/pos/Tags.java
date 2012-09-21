package net.freehal.core.pos;

import net.freehal.core.util.FreehalConfig;

public class Tags {

	private String type;
	private String genus;
	private boolean isName;

	public Tags(String type, String genus) {
		this.type = type;
		this.genus = genus;
	}

	public Tags(String type, String genus, String word) {
		this.type = type;
		this.genus = genus;
		this.isName = (word != null && FreehalConfig.getTagger().isName(word));
	}

	public boolean isType(String string) {
		return type.equals(string);
	}

	public String getType() {
		return type;
	}

	public boolean isGenus(String string) {
		return genus.equals(string);
	}

	public String getGenus() {
		return genus;
	}

	public String getGrammarType() {
		if (type == "komma")
			return "d-komma";
		else if (type == "v")
			return "d-verb";
		else if (type == "art")
			return "d-article";
		else if (type == "adj")
			return "d-adjective";
		else if (type == "prep")
			return "d-preposition";
		else if (type == "questionword")
			return "d-questionword";
		else if (type == "linking")
			return "d-linking";
		else if (type == "n") {
			if (isName)
				return "d-title";
			else
				return "d-noun";
		}
		return "q";
	}

	@Override
	public String toString() {
		return "type=" + type + (genus.isEmpty() ? "" : "genus=" + genus);
	}
}
