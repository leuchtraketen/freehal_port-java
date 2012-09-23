package net.freehal.core.xml;

import java.io.File;

import net.freehal.core.database.DatabaseImpl;
import net.freehal.core.typedefs.MutableInteger;

public class XmlUtils {

	public static String orderTags(String indata) {
		StringBuilder predata = new StringBuilder();
		int k = 0;
		char num = 0;
		char _num = 0;
		boolean is_space = false;
		int indata_size = indata.length();
		while (k < indata_size) {
			_num = num;
			num = indata.charAt(k++);
			if (num == '\n' || num == '\r' || num == '\t' || num == ' ') {
				if (!is_space && _num != '>')
					predata.append(" ");
				is_space = true;
				continue;
			}
			is_space = false;
			// cout << num << endl;

			if (num == '>') {
				predata.append("\n");
			} else if (_num == '<') {
				if (num == '/') {
					predata.append(">\n");
				} else {
					predata.append("<\n");
					predata.append(num);
				}
			} else if (num == '<') {
				if (_num != '>') {
					predata.append("\n");
				}
			} else {
				predata.append(num);
			}
		}
		return predata.toString();
	}

	public static int readXmlFacts(DatabaseImpl d, final String prestr,
			final File filename, XmlFactReciever reciever) {

		long start = System.currentTimeMillis() / 1000;

		String[] lines = prestr.split("\n");

		int i;
		int countFacts = 0;
		for (i = 0; i < lines.length; ++i) {
			if (lines[i].equals("<")) {
				++i;
				if (lines[i].equals("fact")) {
					++countFacts;
				}
			}
		}
		i = 0;
		int countFactsSoFar = 0;
		while (i < lines.length) {
			if (lines[i].equals("<")) {
				++i;
				if (lines[i].equals("fact")) {
					++i;
					MutableInteger j = new MutableInteger(i);
					XmlFact xfact = XmlUtils.readXmlFact(lines, j);
					i = j.get();

					reciever.useXmlFact(d, xfact, countFacts, start, filename,
							countFactsSoFar);
					++countFactsSoFar;
				}
			}
			++i;
		}

		return countFactsSoFar;
	}

	private static XmlFact readXmlFact(String[] lines, MutableInteger i) {
		XmlFact fact = new XmlFact();
		return (XmlFact) readTree(fact, "fact", lines, i);
	}

	private static XmlList readTree(XmlList tree, String tagname,
			String[] lines, MutableInteger i) {

		tree.setName(tagname);

		while (i.get() < lines.length) {
			if (lines[i.get()].equals(">")) {
				i.increment();
				if (lines[i.get()].equals(tagname)) {
					break;
				}
			} else if (lines[i.get()].equals("<")) {
				i.increment();
				String _tagname = lines[i.get()];
				i.increment();
				{
					XmlList subtree = new XmlList();
					readTree(subtree, _tagname, lines, i);
					if (subtree.size() > 0) {
						tree.add(subtree);
					}
				}
			} else if (lines[i.get()].length() > 0) {
				final String text = lines[i.get()];
				// check for default values
				if (!text.equals("00000") && !text.startsWith("0.50")) {
					XmlText t = new XmlText();
					t.setText(text.startsWith("0.0") ? "0" : text
							.startsWith("1.0") ? "1" : text);
					if (tagname.equals("text")) {
						tree.add(t);
					} else {
						XmlList textObj = new XmlList();
						textObj.setName("text");
						textObj.add(t);
						tree.add(textObj);
					}
				}
			}
			i.increment();
		}

		return tree;
	}
}
