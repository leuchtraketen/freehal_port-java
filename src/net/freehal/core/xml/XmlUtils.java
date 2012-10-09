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

import java.util.Iterator;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.RegexUtils;

/**
 * An utility class for reading XML files.<br />
 * <br />
 * Example:<br />
 * <br />
 * 
 * <pre>
 *   final File databasefile = ...;
 *   final Iterable<String> xmlInput = FileUtils.readLines(databasefile);
 *   final XmlStreamIterator xmlIterator = new XmlUtils.XmlStreamIterator(xmlInput);
 *   XmlUtils.readXmlFacts(xmlIterator, filename, new XmlFactReciever() { ... });
 * </pre>
 * 
 * First we create an Iterator which reads a file line by line with
 * {@code FileUtils.readLines(File)}. Then, we wrap this iterator with an
 * XmlStreamIterator, which can be used by our
 * {@code readXmlFacts(XmlStreamIterator, File, XmlFactReciever)} method for
 * reading the XML file and sending the {@code XmlFact} objects to an
 * {@code XmlFactReciever}.<br />
 * <br />
 * 
 * This is a quite special way to read XML files, but it's much, much faster
 * than using one of those huge XML libraries. And the memory usage of the above
 * example is very low. The first iterator's cause is to read the file line by
 * line, because reading a big database file at once is horrible for example an
 * Android devices. And the second one maintains a small cache of lines read
 * from the first one and immediately creates the fact objects and gives them to
 * the {@code XmlFactReciever} we used.<br />
 * <br />
 * 
 * We only support a minimal subset of the XML language. Don't use any
 * {@code <?xml...>} tag at the beginning and don't use any tag attributes. Tags
 * with no content are not supported too (like {@code <nameoftag />}, for
 * example).
 * 
 * @see net.freehal.core.util.FileUtils#readLines(FreehalFile)
 * @see XmlFactReciever
 * @author "Tobias Schulz"
 */
public class XmlUtils {

	/**
	 * This is a simple iterator for wrapping a string. It returns string given
	 * to the constructor at the first call of {@code next()} and after that,
	 * {@code hasNext()} and {@code next()} will always return {@code false} and
	 * {@code null}.
	 * 
	 * @author "Tobias Schulz"
	 */
	public static class OneStringIterator implements Iterable<String> {

		private final String indata;

		/**
		 * Constructs a new OneStringIterator which wraps the given string.
		 * 
		 * @param indata
		 *        the string to wrap
		 */
		public OneStringIterator(final String indata) {
			this.indata = indata;
		}

		@Override
		public Iterator<String> iterator() {
			return new Iterator<String>() {
				private boolean hasNext = true;

				@Override
				public boolean hasNext() {
					// LogUtils.d("static string iterator: hasNext = " +
					// hasNext);
					if (hasNext == true) {
						hasNext = false;
						return true;
					} else
						return hasNext;
				}

				@Override
				public String next() {
					// LogUtils.d("static string iterator: next = " + indata);
					return indata;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	/**
	 * This is the first step we use for parsing XML files. An XML Stream
	 * Iterator filters the XML input data given to the constructor like the
	 * following example. If it gets the this string from the given String
	 * iterator:<br />
	 * <br />
	 * 
	 * <pre>
	 * &lt;fact&gt; &lt;subject&gt;my name&lt;/subject&gt; &lt;object&gt;freehal&lt;/object&gt;
	 * &lt;verb&gt;is&lt;/verb&gt;&lt;/fact&gt;
	 * </pre>
	 * 
	 * ... then the next calls to {@code hasNext()} and {@code next()} will
	 * return:<br />
	 * <br />
	 * 
	 * <pre>
	 *  1. hasNext() = true, next() = "&lt;fact"
	 *  2. hasNext() = true, next() = "&lt;subject"
	 *  3. hasNext() = true, next() = "my name"
	 *  4. hasNext() = true, next() = "&lt;/subject"
	 *  5. hasNext() = true, next() = "&lt;object"
	 *  6. hasNext() = true, next() = "freehal"
	 *  7. hasNext() = true, next() = "&lt;/object"
	 *  8. hasNext() = true, next() = "&lt;verb"
	 *  9. hasNext() = true, next() = "is"
	 * 10. hasNext() = true, next() = "&lt;/verb"
	 * 11. hasNext() = true, next() = "&lt;/fact"
	 * 12. hasNext() = false
	 * </pre>
	 * 
	 * This is the input used by {@code XmlUtils.readXmlFacts} for second
	 * parsing step.
	 * 
	 * It doesn't matter whether the given Iterator reads a file line by line,
	 * character by character or whether it returns the whole XML data in the
	 * first {@code next()} call. We will cache about 5-15 KiB of data.
	 * 
	 * @see XmlUtils#readXmlFacts(XmlStreamIterator, FreehalFile,
	 *      XmlFactReciever)
	 * @author "Tobias Schulz"
	 */
	public static class XmlStreamIterator implements Iterable<String> {

		private Iterator<String> input;
		private StringBuilder buffer;

		private static final int maxBufferSize = 15 * 1024;
		private static final int minBufferSize = 5 * 1024;

		/**
		 * Construct a new XML Stream Iterator which gets its input from the
		 * given String iterator. It doesn't matter whether the given Iterator
		 * reads a file line by line or whether it returns the whole XML data in
		 * the first {@code next()} call.
		 * 
		 * @param input
		 *        the input iterator which gives us raw XML data
		 */
		public XmlStreamIterator(Iterator<String> input) {
			this.input = input;
			this.buffer = new StringBuilder();
		}

		/**
		 * The same as the other constructor, but with an
		 * {@code Iterable<String>} instead of an an {@code Iterator<String>} as
		 * first argument. It will only call {@code input.iterator()} and then
		 * do the same as the other one.
		 * 
		 * @param input
		 *        the input iterator which gives us raw XML data
		 */
		public XmlStreamIterator(Iterable<String> input) {
			this.input = input.iterator();
			this.buffer = new StringBuilder();
		}

		private boolean fillBuffer() {
			if (buffer.length() < minBufferSize) {
				while (buffer.length() < maxBufferSize && input.hasNext()) {
					buffer.append(input.next());
				}
			}
			return buffer.length() > 0;
		}

		private void resetBuffer(int k) {
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
					// System.out.println("XmlStreamIterator.I.next = " +
					// toReturn);
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

	/**
	 * Parse the given XML input and send the created {@code XmlFact} objects to
	 * the given {@code XmlFactReciever}.
	 * 
	 * @param xmlIterator
	 *        the input data, wrapped by {@code XmlStreamIterator}
	 * @param filename
	 *        the filename of the input file, which will be added to the created
	 *        fact objects, or {@code null} if there is none.
	 * @param reciever
	 *        the {@code XmlFactReciever} to give the fact objects to.
	 * @return how many facts have been parsed.
	 */
	public static int readXmlFacts(final XmlStreamIterator xmlIterator, final FreehalFile filename,
			XmlFactReciever reciever) {

		long start = System.currentTimeMillis() / 1000;

		Iterator<String> lines = xmlIterator.iterator();
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
			// System.out.println("in readTree(tree=" + tree.printStr() +
			// ", tagname=" + tagname + "): line="
			// + line);
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

	public static class StripXmlTagsIterator implements Iterable<String> {

		final Iterator<String> wrapped;

		public StripXmlTagsIterator(Iterator<String> wrapped) {
			this.wrapped = wrapped;
		}

		public StripXmlTagsIterator(Iterable<String> wrapped) {
			this.wrapped = wrapped.iterator();
		}

		@Override
		public Iterator<String> iterator() {
			return new Iterator<String>() {
				@Override
				public boolean hasNext() {
					return wrapped.hasNext();
				}

				@Override
				public String next() {
					final String next = wrapped.next();
					if (next != null)
						return RegexUtils.replace(next, "[<]([^>]+)[>]", "");
					else
						return next;
				}

				@Override
				public void remove() {
					wrapped.remove();
				}
			};
		}

	}

	public static String stripXmlTags(String read) {
		return new StripXmlTagsIterator(new OneStringIterator(read)).iterator().next();
	}
}
