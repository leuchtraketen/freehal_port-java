package net.freehal.core.pos;

import net.freehal.core.util.FreehalConfig;

public class Tags {

	private String type = null;
	private String genus = null;
	private String word = null;
	private Boolean isName = null;

	public Tags(String type, String genus) {
		init(null, type, genus, null);
	}

	public Tags(String type, String genus, String word) {
		init(null, type, genus, word);
	}

	public Tags(Tags tags, String type, String genus) {
		init(tags, type, genus, null);
	}

	public Tags(Tags tags, String type, String genus, String word) {
		init(tags, type, genus, word);
	}

	private void init(Tags tags, String type, String genus, String word) {
		if (tags != null) {
			this.type = tags.type;
			this.genus = tags.genus;
			this.word = tags.word;
			this.isName = tags.isName;
		}
		if (type != null)
			this.type = type;
		if (genus != null)
			this.genus = genus;
		if (word != null)
			this.word = word;
	}

	public boolean isType(String string) {
		return type != null && type.equals(string);
	}

	public boolean hasType() {
		return type != null;
	}

	public String getType() {
		return type;
	}

	public boolean isGenus(String string) {
		return genus != null && genus.equals(string);
	}

	public boolean hasGenus() {
		return genus != null;
	}

	public String getGenus() {
		return genus;
	}

	public String getGrammarType() {
		if (type == null)
			return "q";
		else if (type.equals("komma"))
			return "d-komma";
		else if (type.equals("v"))
			return "d-verb";
		else if (type.equals("art"))
			return "d-article";
		else if (type.equals("adj"))
			return "d-adjective";
		else if (type.equals("prep"))
			return "d-preposition";
		else if (type.equals("questionword"))
			return "d-questionword";
		else if (type.equals("linking"))
			return "d-linking";
		else if (type.equals("n")) {
			if (isName == null && word != null) {
				isName = FreehalConfig.getTagger().isName(word);
			}
			if (isName)
				return "d-title";
			else
				return "d-noun";
		} else
			return "q";
	}

	public static String getUniqueType(String type) {
		if (type.equals("komma"))
			return "komma";
		else if (type.equals("vi") || type.equals("vt") || type.equals("ci"))
			return "v";
		else if (type.startsWith("a") && !type.equals("art"))
			return "adj";
		else if (type.equals("n") || type.equals("f") || type.equals("m")
				|| type.startsWith("n,") || type.equals("pron")
				|| type.equals("b"))
			return "n";
		else if (type.equals("fw") || type.startsWith("ques"))
			return "questionword";
		return type;
	}

	@Override
	public String toString() {
		return "{" + (hasType() ? "type=" + type : "")
				+ (hasType() && hasGenus() ? "," : "")
				+ (hasGenus() ? "genus=" + genus : "") + "}";
	}
}
