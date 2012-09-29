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

import java.io.File;
import java.util.Iterator;

import net.freehal.core.util.LogUtils;

public class XmlUtils {

	public static XmlStreamIterator orderTags(Iterable<String> indata) {
		return new XmlStreamIterator(indata.iterator());
	}

	public static XmlStreamIterator orderTags(Iterator<String> indata) {
		return new XmlStreamIterator(indata);
	}

	public static XmlStreamIterator orderTags(final String indata) {
		return new XmlStreamIterator(new Iterator<String>() {
			private boolean hasNext = true;

			@Override
			public boolean hasNext() {
				LogUtils.i("static string iterator: hasNext = " + hasNext);
				if (hasNext == true) {
					hasNext = false;
					return true;
				} else
					return hasNext;
			}

			@Override
			public String next() {
				LogUtils.i("static string iterator: next = " + indata);
				return indata;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		});
	}

	public static String orderTagsXX(String indata) {
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

	public static class XmlStreamIterator implements Iterable<String> {

		private Iterator<String> input;
		private StringBuilder buffer;

		private static final int maxBufferSize = 15 * 1024;
		private static final int minBufferSize = 5 * 1024;

		public XmlStreamIterator(Iterator<String> input) {
			this.input = input;
			this.buffer = new StringBuilder();
		}

		public boolean fillBuffer() {
			if (buffer.length() < minBufferSize) {
				while (buffer.length() < maxBufferSize && input.hasNext()) {
					buffer.append(input.next());
				}
			}
			return buffer.length() > 0;
		}

		public void resetBuffer(int k) {
			buffer.delete(0, k);
		}

		@Override
		public Iterator<String> iterator() {
			return new Iterator<String>() {

				int k = 0;
				char num = 0;
				StringBuilder predata = new StringBuilder();

				@Override
				public boolean hasNext() {
					fillBuffer();
					// LogUtils.i("XmlStreamIterator.Iterator: hasNext = " +
					// (buffer.trim().length() > 0));
					return buffer.length() > k;
				}

				private String newLine(int _k, final Object appendToNext) {
					resetBuffer(_k);
					k = 0; // this is the class-wide "k"!
					final String toReturn = predata.toString();
					predata.setLength(0);
					if (appendToNext != null)
						predata.append(appendToNext);
					// LogUtils.i("XmlStreamIterator.Iterator: next = " +
					// toReturn);
					//System.out.println("XmlStreamIterator.I.next = " + toReturn);
					return toReturn;
				}

				@Override
				public String next() {
					Boolean inTagname = null;
					boolean isSpace = false;
					while (fillBuffer() && k < buffer.length()) {
						while (k < buffer.length()) {
							num = buffer.charAt(k);
							// System.out.println(num);
							++k;

							if (num == '\n' || num == '\r' || num == '\t' || num == ' ') {
								if (!isSpace && predata.length() > 0)
									predata.append(" ");
								isSpace = true;
								continue;
							}
							isSpace = false;

							if (inTagname == null)
								inTagname = (num == '<');

							if (inTagname && num == '>')
								return newLine(k, null);

							if (!inTagname && num == '<')
								return newLine(k - 1, null);

							predata.append(num);
						}
					}
					return newLine(k, null);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	public static int readXmlFacts(final XmlStreamIterator prestr, final File filename,
			XmlFactReciever reciever) {

		long start = System.currentTimeMillis() / 1000;

		Iterator<String> lines = prestr.iterator();
		int countFacts = 1; // how to do that with an iterator?

		int countFactsSoFar = 0;
		while (lines.hasNext()) {
			if (lines.next().equals("<fact")) {
				XmlFact xfact = XmlUtils.readXmlFact(lines);
				if (filename != null)
					xfact.setFilename(filename);

				reciever.useXmlFact(xfact, countFacts, start, filename, countFactsSoFar);
				++countFactsSoFar;
			}
		}

		return countFactsSoFar;
	}

	private static XmlFact readXmlFact(Iterator<String> lines) {
		XmlFact fact = new XmlFact();
		return (XmlFact) readTree(fact, "fact", lines);
	}

	private static XmlList readTree(XmlList tree, String tagname, Iterator<String> lines) {

		tree.setName(tagname);

		for (String line = lines.next(); lines.hasNext(); line = lines.next()) {
			//System.out.println("in readTree(tree=" + tree.printStr() + ", tagname=" + tagname + "): line="
			//		+ line);
			if (line.startsWith("</")) {
				if (line.equals("</" + tagname)) {
					break;
				}
			} else if (line.startsWith("<")) {
				final String _tagname = line.substring(1);
				{
					XmlList subtree = new XmlList();
					readTree(subtree, _tagname, lines);
					if (subtree.size() > 0) {
						tree.add(subtree);
					}
				}
			} else if (line.length() > 0) {
				final String text = line;
				// check for default values
				if (!text.equals("00000") && !text.startsWith("0.50")) {
					XmlText t = new XmlText();
					t.setText(text.startsWith("0.0") ? "0" : text.startsWith("1.0") ? "1" : text);
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
		}

		return tree;
	}
}
