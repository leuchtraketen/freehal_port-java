/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.LogUtils;

/**
 * This class represents an XML tag which contains other XML tags. It is used to
 * build trees of XML objects.
 * 
 * @see XmlFact
 * @see XmlObj
 * @see XmlText
 * @author "Tobias Schulz"
 */
public class XmlList extends XmlObj {

	/**
	 * the embedded XML objects
	 */
	private List<XmlObj> embedded = new ArrayList<XmlObj>();

	/**
	 * Add all embedded XML objects with the given tag name to the given list.
	 * 
	 * @param list
	 *        the list to add the XML objects to
	 * @param tagname
	 *        the tag name to search for
	 * @return how many objects have been added
	 */
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

	/**
	 * Return all embedded XML objects with the given tag name in a list.
	 * 
	 * @param tagname
	 *        the tag name to search for
	 * @return a list of matching XML objects
	 */
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

	/**
	 * Add an embedded XML object.
	 * 
	 * @param o
	 *        the XML object to add
	 */
	public void add(XmlObj o) {
		embedded.add(o);
	}

	/**
	 * Add all embedded XML objects from the given object to this one if the
	 * given XML object is an instance of {@code XmlList}. Otherwise, the given
	 * XML object is added like in {@code add(XmlObj)}.
	 * 
	 * @see #add(XmlObj)
	 * @param o
	 */
	public void addAll(XmlObj o) {
		if (o instanceof XmlList) {
			embedded.addAll(((XmlList) o).embedded);
		} else {
			add(o);
		}
	}

	/**
	 * Add all XML objects in the given list as embedded XML objects.
	 * 
	 * @param o
	 *        the list of objects to add
	 */
	public void addAll(List<XmlObj> o) {
		embedded.addAll(o);
	}

	/**
	 * Group the embedded XML objects by their tag name, especially the tag
	 * names "subject", "object", "adverbs" and "verb". This improves the speed
	 * of later executions of {@code part()}.
	 * 
	 * @see #part(String)
	 * @see #part(List, String)
	 */
	public void trim() {

		XmlList subject = new XmlList();
		XmlList object = new XmlList();
		XmlList adverbs = new XmlList();
		XmlList verb = new XmlList();
		List<XmlObj> other = new ArrayList<XmlObj>();

		for (XmlObj e : embedded) {
			if (e instanceof XmlList) {
				XmlList unique = null;
				if (e.getName().equals("subject"))
					unique = subject;
				else if (e.getName().equals("object"))
					unique = object;
				else if (e.getName().equals("adverbs"))
					unique = adverbs;
				else if (e.getName().equals("verb"))
					unique = verb;
				((XmlList) e).trim();

				if (unique != null) {
					unique.setName(e.getName());
					unique.addAll(e);
				} else {
					other.add(e);
				}
			}
		}

		embedded.clear();
		this.addAll(subject);
		this.addAll(object);
		this.addAll(adverbs);
		this.addAll(verb);
		this.addAll(other);
	}

	/**
	 * How many embedded XML objects are embedded in this object?
	 * 
	 * @return the count of embedded XML objects
	 */
	public int size() {
		return embedded.size();
	}

	/**
	 * Get a list of embedded XML objects. Do not modify this list if you don't
	 * know what you are doing!
	 * 
	 * @return a list of embedded XML objects
	 */
	public List<XmlObj> getEmbedded() {
		return embedded;
	}

	@Override
	public boolean toggle() {
		boolean changed = false;
		for (XmlObj e : embedded) {
			if (e.toggle()) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	protected String printXml(int level, int secondlevel) {
		if (name == "clause") {
			++level;
			secondlevel = 0;
		}

		StringBuilder str = new StringBuilder();
		str.append("<").append(name).append(">").append((secondlevel == 0 ? "\n" : ""));
		for (XmlObj e : embedded) {

			if (secondlevel == 0)
				for (int r = 0; r < level + 1; ++r)
					str.append("  ");

			str.append(e.printXml(level, secondlevel + 1)).append((secondlevel == 0 ? "\n" : ""));
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
				ss.append(e.printStr());
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
			ss.append(embedded.get(0).printText());
		} else {
			if (embedded.size() == 1) {
				ss.append(embedded.get(0).printText());
			} else if (embedded.size() > 1) {
				int k;
				for (k = 0; k < embedded.size(); ++k) {
					if (k > 0)
						ss.append(delem);
					ss.append(embedded.get(k).printText());
				}
			}
		}
		return ss.toString();
	}

	@Override
	protected boolean prepareWords() {
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

		LogUtils.d("---- compare: " + this.printStr() + " isLike " + other.printStr() + " = " + matches);
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

		LogUtils.d("---- compare: " + this.printStr() + " matches " + other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double countWords() {
		double c = 0;
		for (XmlObj subobj : embedded) {
			if (!(subobj.getName().equals("questionword") || subobj.getName().equals("extra")
					|| subobj.getName().equals("truth") || subobj.getName().equals("clause"))) {
				c += subobj.countWords();
			}
		}
		if (this.getName() == "link_|" || this.getName() == "synonyms")
			c /= embedded.size();
		return c;
	}

	/**
	 * Iterator over all words in all embedded XML objects, use the given
	 * {@link SynonymProvider} to get their synonyms and replace the embedded
	 * {@link XmlText} object by a {@link XmlSynonyms} object, which contains
	 * the original word and all synonyms.
	 * 
	 * @param database
	 */
	public void insertSynonyms(SynonymProvider database) {
		List<XmlObj> newEmbedded = new ArrayList<XmlObj>();
		for (XmlObj e : embedded) {
			if (e instanceof XmlList) {
				((XmlList) e).insertSynonyms(database);
				newEmbedded.add(e);
			} else if (e instanceof XmlText) {
				List<Word> words = e.getWords();
				if (words.size() > 1) {
					XmlList list = new XmlList();
					list.setName("list");
					for (Word word : words) {
						list.add(new XmlSynonyms(word, database));
					}
					newEmbedded.add(list);
				} else if (words.size() == 1) {
					newEmbedded.add(new XmlSynonyms(words.get(0), database));
				}
			}
		}
		embedded = null;
		embedded = newEmbedded;
		this.resetCache();
	}

	/**
	 * Construct a new {@link XmlObj} instance of the parts of a given text,
	 * separated by '|' (in fact its an instance of {@link XmlList} containing
	 * {@link XmlText} objects).
	 * 
	 * @param text
	 *        the text
	 * @return a {@link XmlList} wrapping the parts of given text separated by
	 *         '|'
	 */
	public static XmlObj fromText(String text) {
		XmlList xlist = new XmlList();
		xlist.setName("fromText");
		for (String word : text.split("[|]")) {
			XmlText xobj = new XmlText();
			xobj.setText(word);
			xlist.add(xobj);
		}
		return xlist;
	}

	@Override
	public String toString() {
		return printStr();
	}

	@Override
	protected String hashString() {
		StringBuilder ss = new StringBuilder();
		ss.append(name);
		for (XmlObj e : embedded) {
			ss.append(" ").append(e.hashString());
		}
		return ss.toString();
	}
}
