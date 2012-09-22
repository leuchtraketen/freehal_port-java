package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.util.LogUtils;

public class XmlList extends XmlObj {

	private List<XmlObj> embedded = new ArrayList<XmlObj>();

	public int part(List<XmlObj> list, String tagname) {
		int j = 0;
		for (XmlObj o : embedded) {
			if (o.getName().equals(tagname)) {
				list.add(o);
				++j;
			}
		}
		return j;
	}

	public XmlList part(String tagname) {
		int count = 0;
		for (XmlObj o : embedded) {
			if (o.getName().equals(tagname)) {
				++count;
			}
		}

		if (count == 0) {
			return new XmlList();
		} else if (count == 1) {
			for (XmlObj o : embedded) {
				if (o.getName().equals(tagname)) {
					if (o instanceof XmlList)
						return (XmlList) o;
					else {
						// WTF?
						return new XmlList();
					}
				}
			}

			// this will never happen!
			return null;
		} else {
			XmlList subtree = new XmlList();
			subtree.setName(tagname);

			for (XmlObj o : embedded) {
				if (o.getName().equals(tagname)) {
					subtree.addAll(o);
				}
			}
			return subtree;
		}
	}

	public void add(XmlObj o) {
		embedded.add(o);
	}

	public void addAll(XmlObj o) {
		if (o instanceof XmlList) {
			embedded.addAll(((XmlList) o).embedded);
		} else {
			add(o);
		}
	}

	public void addAll(List<XmlObj> o) {
		embedded.addAll(o);
	}

	@Override
	public void trim() {

		XmlList subject = new XmlList();
		XmlList object = new XmlList();
		XmlList adverbs = new XmlList();
		XmlList verb = new XmlList();
		List<XmlObj> other = new ArrayList<XmlObj>();

		for (XmlObj e : embedded) {
			XmlList unique = null;
			if (e.getName().equals("subject"))
				unique = subject;
			else if (e.getName().equals("object"))
				unique = object;
			else if (e.getName().equals("adverbs"))
				unique = adverbs;
			else if (e.getName().equals("verb"))
				unique = verb;
			e.trim();

			if (unique != null) {
				unique.setName(e.getName());
				unique.addAll(e);
			} else {
				other.add(e);
			}
		}

		embedded.clear();
		this.addAll(subject);
		this.addAll(object);
		this.addAll(adverbs);
		this.addAll(verb);
		this.addAll(other);
	}

	public int size() {
		return embedded.size();
	}

	List<XmlObj> getEmbedded() {
		return embedded;
	}

	public int toggle(AbstractTagger tagger) {
		return 0;
	}

	@Override
	public String printXml(int level, int secondlevel) {
		if (name == "clause") {
			++level;
			secondlevel = 0;
		}

		StringBuilder str = new StringBuilder();
		str.append("<").append(name).append(">")
				.append((secondlevel == 0 ? "\n" : ""));
		for (XmlObj e : embedded) {

			if (secondlevel == 0)
				for (int r = 0; r < level + 1; ++r)
					str.append("  ");

			str.append(e.printXml(level, secondlevel + 1)).append(
					(secondlevel == 0 ? "\n" : ""));
		}

		if (secondlevel == 0)
			for (int r = 0; r < level; ++r)
				str.append("  ");

		str.append("</").append(name).append(">");
		return str.toString();
	}

	@Override
	public String printStr() {
		String delem;
		if (name == "and" || name == "or") {
			delem = " " + name;
		} else {
			delem = ",";
		}

		StringBuilder ss = new StringBuilder();
		if (name == "text" && embedded.size() == 1) {
			ss.append(name).append(":").append(embedded.get(0).printStr());
		} else if (name == "synonyms") {
			ss.append(name).append(": \"");
			int k = 0;
			for (XmlObj e : embedded) {
				if (k++ > 0)
					ss.append("|");
				ss.append(e.printText());
			}
			ss.append("\"");
		} else {
			ss.append("'").append(name).append("':{");

			if (embedded.size() == 1) {
				ss.append(embedded.get(0).printStr());
			} else if (embedded.size() > 1) {
				int k;
				for (k = 0; k < embedded.size(); ++k) {
					if (k > 0)
						ss.append(delem);
					ss.append(" ").append(embedded.get(k).printStr());
				}
				ss.append(" ");
			}
			ss.append("}");
		}
		return ss.toString();
	}

	@Override
	public String printText() {
		String delem;
		if (name == "and" || name == "or") {
			delem = " " + name + " ";
		} else {
			delem = " ";
		}

		StringBuilder ss = new StringBuilder();
		if (name == "text" && embedded.size() == 1) {
			ss.append(embedded.get(0).printStr());
		} else {
			if (embedded.size() == 1) {
				ss.append(embedded.get(0).printStr());
			} else if (embedded.size() > 1) {
				int k;
				for (k = 0; k < embedded.size(); ++k) {
					if (k > 0)
						ss.append(delem);
					ss.append(embedded.get(k).printStr());
				}
			}
		}
		return ss.toString();
	}

	@Override
	public boolean prepareWords() {
		if (super.prepareWords()) {
			if (!name.equals("truth")) {
				for (XmlObj e : embedded) {
					e.prepareWords();
					e.getWords(cacheWords);
				}
			}
		}
		return true;
	}

	@Override
	public boolean prepareTags(AbstractTagger tagger) {
		if (super.prepareTags(tagger)) {
			for (XmlObj e : embedded) {
				e.prepareTags(tagger);
			}
			for (Word word : cacheWords) {
				if (!word.hasTags())
					word.setTags(tagger.getPartOfSpeech(word.getWord()));
			}
		}
		return true;
	}

	@Override
	public double isLike(XmlObj other) {
		double matches = 0;

		int count = 0;
		for (XmlObj subobj : embedded) {
			double m = subobj.isLike(other);
			if (m > 0) {
				matches += m;
				++count;
			}
		}

		if (this.getName() == "link_&")
			matches = (count == embedded.size() ? matches : 0);
		if (this.getName() == "synonyms")
			matches /= count;

		LogUtils.d("---- compare: " + this.printStr() + " isLike "
				+ other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double matches(XmlObj other) {
		double matches = 0;

		int count = 0;
		for (XmlObj subobj : embedded) {
			double m = subobj.matches(other);
			if (m > 0) {
				matches += m;
				++count;
			}
		}

		if (this.getName() == "link_&")
			matches = (count == embedded.size() ? matches : 0);
		if (this.getName() == "synonyms")
			matches /= count;

		LogUtils.d("---- compare: " + this.printStr() + " matches "
				+ other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double countWords() {
		double c = 0;
		for (XmlObj subobj : embedded) {
			if (subobj.getName().equals("questionword") ||
					subobj.getName().equals("extra") ||
							subobj.getName().equals("truth")||
									subobj.getName().equals("clause")) {
				c += subobj.countWords();
			}
		}
		if (this.getName() == "link_|" || this.getName() == "synonyms")
			c /= embedded.size();
		return c;
	}
	
	@Override
	public String toString() {
		return printStr();
	}
}
