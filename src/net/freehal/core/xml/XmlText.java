package net.freehal.core.xml;

import net.freehal.core.util.ArrayUtils;
import net.freehal.core.util.RegexUtils;

public class XmlText extends XmlList {

	public XmlText() {
		setName("text");
	}

	private static final String WORD_SEPARATOR = "[^A-Za-z0-9}{\\]\\[=)(_$-]+";

	/**
	 * Construct a new {@link XmlObj} instance of the given text (in fact its an
	 * instance of {@link XmlWord}).
	 * 
	 * @param text
	 *        the text
	 * @return a {@link XmlWord} wrapping the given text
	 */
	public static XmlObj fromText(String text) {
		String[] words = text.split(WORD_SEPARATOR);
		XmlText textlist = new XmlText();
		for (String word : words) {
			if (RegexUtils.find(word, XmlVariable.REGEX_VARIABLE)) {
				XmlVariable xobj = new XmlVariable();
				xobj.setText(word);
				textlist.add(xobj);
			} else {
				XmlWord xobj = new XmlWord();
				xobj.setText(word);
				textlist.add(xobj);
			}
		}
		for (int i = 0; i < textlist.embedded.size(); ++i) {
			XmlObj e = textlist.embedded.get(i);
			if (e instanceof XmlVariable) {
				((XmlVariable) e).setBefore(ArrayUtils.partOfList(textlist.embedded, 0, i));
				((XmlVariable) e).setAfter(ArrayUtils.partOfList(textlist.embedded, i + i, 0));
			}
		}
		return textlist;
	}

	/**
	 * Construct a new {@link XmlObj} instance of the given word's text (in fact
	 * its an instance of {@link XmlWord}).
	 * 
	 * @param word
	 *        the text
	 * @return a {@link XmlWord} wrapping the given text
	 */
	public static XmlObj fromText(Word word) {
		return fromText(word.getWord());
	}

}
