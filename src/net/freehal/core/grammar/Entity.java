package net.freehal.core.grammar;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.grammar.typedefs.Entities;
import net.freehal.core.grammar.typedefs.StringMultiMap;
import net.freehal.core.util.Mutable;
import net.freehal.core.util.RegexUtils;

public class Entity {

	private Grammar2012 grammar;
	private String data = "";
	private String symbol = "";
	private String repl = "";
	private List<String> virt = new ArrayList<String>();;
	private String text = "";
	private Entities embed = new Entities();
	private int order = -1;

	public Entity(Grammar2012 grammar, String text) {
		init(text);
	}

	public Entity(Grammar2012 grammar, String text, Entities embed) {
		init(text);
		this.grammar = grammar;
		this.embed = embed;
	}

	public Entity(Entity obj) {
		this.grammar = obj.grammar;
		this.data = obj.data;
		this.symbol = obj.symbol;
		this.repl = obj.repl;
		this.virt.addAll(obj.virt);
		this.text = obj.text;
		this.embed.addAll(obj.embed);
		this.order = obj.order;
	}

	public void add(String s) {
		init(text);
	}

	private void init(String text) {

		String[] parts = text.split("/");
		for (String part : parts) {
			if (part.equals("") || part.equals("null")) {
				// ignore
			} else if (RegexUtils.find(part, "^([0-9]+)$")) {
				order = Integer.parseInt(part);
			} else if (symbol.length() == 0 && repl.length() == 0
					&& data.length() == 0) {
				if (part.startsWith("s-")) {
					symbol = part;
				} else if (part.startsWith("r-")) {
					repl = part;
				} else if (part.startsWith("d-")) {
					data = part;
				} else {
					virt.add(part);
				}
			} else {
				if (part.startsWith("r-")) {
					// ignore
				} else {
					virt.add(part);
				}
			}
		}
	}

	public StringMultiMap toGroups() {
		return toGroups(new StringMultiMap());
	}

	public StringMultiMap toGroups(StringMultiMap p) {
		return toGroups(p, new ArrayList<String>());
	}

	public StringMultiMap toGroups(StringMultiMap p, List<String> l) {
		return toGroups(p, l, "v-clause-1");
	}

	public StringMultiMap toGroups(StringMultiMap pm, List<String> keys,
			String keyprefix) {

		for (String it : virt) {
			if (it.startsWith("v-clause")) {
				keyprefix = it;
			} else if (it.startsWith("v-subject")) {
				keys.add("subjects");
			} else if (it.startsWith("v-object")) {
				keys.add("objects");
			} else if (it.startsWith("v-verb")
					&& !it.startsWith("v-verbprefix")) {
				keys.add("verbs");
			} else if (it.startsWith("v-verbprefix")) {
				keys.add("verbprefixes");
			} else if (it.startsWith("v-adverb")
					|| it.startsWith("v-longadverb")) {
				keys.add("adverbs");
			} else if (it.startsWith("v-questionword")) {
				keys.add("questionwords");
			} else if (it.startsWith("v-extra-after")) {
				keys.add("extra");
			} else if (it.startsWith("v-extra-before")) {
				keys.add("before-adverbs");
			}
		}
		if (pm == null) {
			pm = new StringMultiMap();
		}
		if (text.length() > 0) {
			String last = "";
			for (String key : keys) {
				if (!key.equals(last)) {
					pm.multiPut(keyprefix + "/" + key, text);
				}
				last = key;
			}

		} else {
			if (embed.size() > 0) {
				for (Entity entity : embed) {
					entity.toGroups(pm, keys);
				}
			}
		}

		return pm;
	}

	/**
	 * only for printGraph(String)!
	 */
	private static int _u = 1;

	private String printGraph(Mutable<String> _key) {

		String key;
		if (virt.size() > 0)
			key = virt.get(0);
		else
			key = this.toKey();
		key += " (" + _u++ + ")";
		if (_key != null)
			_key.set(key);

		StringBuilder ss = new StringBuilder();
		ss.append("\"").append(key).append("\" [shape=record,regular=1];\n");

		if (text.length() > 0) {
			StringBuilder sstext = new StringBuilder();
			sstext.append(text).append(" (").append(_u++).append(")");
			ss.append("\"")
					.append(sstext)
					.append("\" [shape=record,fontsize=14,style=filled,fillcolor=yellow,regular=1];\n");
			ss.append("\"").append(key).append("\" -> \"").append(sstext)
					.append("\" [dir=none,weight=10];\n");
		} else {
			if (embed.size() > 0) {
				for (Entity embeddedEntity : embed) {
					Mutable<String> keyOfEmbeddedEntity = new Mutable<String>(
							null);
					ss.append(embeddedEntity.printGraph(keyOfEmbeddedEntity));

					ss.append("\"").append(key).append("\" -> \"")
							.append(keyOfEmbeddedEntity.get())
							.append("\" [dir=none,weight=10];\n");
				}
			}
		}

		return ss.toString();
	}

	public String printGraph() {
		return printGraph(null);
	}

	private String toXml(Mutable<String> _key, Mutable<String> _text, int level) {

		String grammarKey = (virt.size() > 0) ? virt.get(0) : this.toKey();
		String key = grammarKey == "s-all" ? "fact"
				: grammarKey == "v-subject" ? "subject"
						: grammarKey == "v-object" ? "object"
								: grammarKey == "v-questionword" ? "questionword"
										: grammarKey.startsWith("v-extra-") ? "extra"
												: grammarKey == "v-verb" ? "verb"
														: (grammarKey
																.startsWith("v-clause-") && grammarKey != "v-clause-1") ? "clause"
																: grammarKey == "v-adverb" ? "adverbs"
																		: grammarKey == "v-linked" ? "linked"
																				: grammarKey == "d-linking" ? "linking"
																						: grammarKey == "" ? ""
																								: ""; // "--"
																										// +
																										// key;

		if (_key != null)
			_key.set(grammarKey);
		if (_text != null)
			_text.set(text);

		if (key == "clause")
			level = 0;
		if (grammarKey == "d-linking" || grammarKey == "d-komma")
			return "";

		StringBuilder ssEmbed = new StringBuilder();
		if (text.length() > 0) {
			ssEmbed.append("<text>").append(text).append("</text>");
		} else if (embed.size() > 0 && !grammarKey.equals("d-komma")) {
			for (int i = 0; i < embed.size(); ++i) {
				final Entity it = embed.get(i);

				Mutable<String> embeddedGrammarKey = new Mutable<String>();
				Mutable<String> embeddedText = new Mutable<String>();
				final String embedded = it.toXml(embeddedGrammarKey,
						embeddedText, (key.length() > 0 ? level + 1 : level));
				if (embeddedGrammarKey.get().startsWith("v-link")) {
					// found some linked objects -> inner loop
					String linkKey = "link_?";
					StringBuilder ssLink = new StringBuilder();
					int j = i;
					for (; j < embed.size(); ++j) {
						final Entity it2 = embed.get(j);

						final String embedded2 = it2.toXml(embeddedGrammarKey,
								embeddedText, (key.length() > 0 ? level + 1
										: level));
						if (!embeddedGrammarKey.get().startsWith("v-link")) {
							// no linked objects from now on
							break;
						}

						if (embeddedText.get().contains("&")
								|| embeddedText.equals("und")
								|| embeddedText.equals("and"))
							linkKey = "link_&";
						else if (embeddedText.get().contains("|")
								|| embeddedText.equals("oder")
								|| embeddedText.equals("or"))
							linkKey = "link_|";
						else
							ssLink.append(embedded2);
					}
					// go on with the normal (outer) loop
					i = --j;
					ssEmbed.append("<").append(linkKey).append(">");
					ssEmbed.append(ssLink);
					ssEmbed.append("</").append(linkKey).append(">");
				} else {
					ssEmbed.append(embedded);
				}
			}
		}
		StringBuilder ss = new StringBuilder();
		if (key.length() > 0) {
			if (level == 0) {
				ss.append("<").append(key).append(">\n");
				ss.append(ssEmbed);
				ss.append("</").append(key).append(">\n");
			} else if (level == 1) {
				ss.append("<").append(key).append(">");
				ss.append(ssEmbed);
				ss.append("</").append(key).append(">\n");
			} else {
				ss.append("<").append(key).append(">");
				ss.append(ssEmbed);
				ss.append("</").append(key).append(">");
			}
		} else {
			ss.append(ssEmbed);
		}

		return ss.toString();
	}

	public String toXml() {
		return this.toXml(null, null, 0);
	}

	public String print() {
		String str = this.toKey();
		if (virt.size() > 0) {
			for (String it : virt) {
				str += "/" + it;
			}
		}

		if (text.length() > 0) {
			str += ": '" + text + "'";
		}
		if (embed.size() > 0) {
			str += " < ";
			for (Entity it : embed) {
				if (it != embed.get(0))
					str += ", ";
				str += it.print();
			}
			str += " > ";
		}
		return str;
	}

	private String printLong(String left) {
		String str = this.toKey();
		if (virt.size() > 0) {
			for (String it : virt) {
				str += "/" + it;
			}
		}

		StringBuilder ss = new StringBuilder();
		ss.append(left).append(str);
		if (embed.size() > 0) {
			ss.append(":\n");
			for (Entity it : embed) {
				ss.append(it.printLong(left + "  "));
			}
		} else {
			ss.append(": '").append(text).append("'\n");
		}
		return ss.toString();
	}

	public String printLong() {
		return printLong("");
	}

	public String toString() {
		String str = this.toKey();
		if (virt.size() > 0) {
			for (String it : virt) {
				str += "/" + it;
			}
		}

		if (text.length() > 0) {
			str += ": '" + text + "'";
		}
		if (embed.size() > 0) {
			str += " < ";
			for (Entity it : embed) {
				if (it != embed.get(0))
					str += ", ";
				str += it.print();
			}
			str += " > ";
		}
		return str;
	}

	public String toKey() {
		String key = "null";
		if (data.length() > 0)
			key = data;
		else if (symbol.length() > 0)
			key = symbol;
		else if (repl.length() > 0)
			key = repl;

		if (order >= 0)
			key += "/" + order;
		return key;
	}

	public char type() {
		return (data.length() == 0) ? (symbol.length() == 0 ? (repl.length() == 0 ? (virt
				.size() == 0 ? 0 : 'v') : 'r')
				: 's')
				: 'd';
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getData() {
		return data;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getRepl() {
		return repl;
	}

	public List<String> getVirt() {
		return virt;
	}

	public List<String> getMarker() {
		if (virt.size() > 0) {
			return virt;
		} else {
			List<String> v = new ArrayList<String>();
			v.add(this.toString());
			return v;
		}
	}

	public List<Entity> getEmbed() {
		return embed;
	}

	public int getOrder() {
		return order >= 0 ? order : 1;
	}

	public static String printPerl(StringMultiMap p, String s1, String s2) {
		return null;
	}

	public static String printPerl(StringMultiMap p) {
		return null;
	}
}
