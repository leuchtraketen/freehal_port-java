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

import java.util.List;

import net.freehal.core.pos.Taggers;
import net.freehal.core.util.LogUtils;

/**
 * This class represent a text inside an XML tag.
 * 
 * @author "Tobias Schulz"
 */
public class XmlWord extends XmlObj {

	private String text;

	public static XmlObj fromWord(String word) {
		XmlWord xword = new XmlWord();
		xword.setText(word);
		return xword;
	}

	public static XmlObj fromWord(Word word) {
		return fromWord(word.getWord());
	}

	/**
	 * Set the text.
	 * 
	 * @param text
	 *        the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Get the text.
	 * 
	 * @r
	 * 
	 *    public static XmlWord fromWord(String word) { XmlWord xword = new
	 *    XmlWord(); xword.setText(word); return xword; }eturn the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the text.
	 * 
	 * @see Word#getWord()
	 * @param word
	 *        the text to set
	 */
	public void setText(Word word) {
		this.text = word.getWord();
	}

	@Override
	public String printXml(int level, int secondlevel) {
		return text;
	}

	@Override
	public String printStr() {
		return "\"" + text + "\"";
	}

	@Override
	public String printText() {
		return text;
	}

	public Word getWord() {
		return getWords().get(0);
	}

	@Override
	protected boolean prepareWords() {
		if (super.prepareWords()) {
			/*
			 * String[] words = text.split("[^A-Za-z0-9}{\\]\\[=)(_$-]+"); for
			 * (String word : words) { if (word.length() > 0 &&
			 * !word.equals("1")) cacheWords.add(new Word(word, null)); }
			 */
			if (text.length() > 0)
				cacheWords.add(new Word(text, null));
		}
		return true;
	}

	@Override
	public double isLike(XmlObj other) {
		double matches = 0;

		if (other instanceof XmlWord) {
			final List<Word> words = this.getWords();
			for (Word word : words) {
				matches += word.isLike((XmlWord) other);
			}

		} else if (other instanceof XmlList) {
			int count = 0;
			final List<XmlObj> embedded = ((XmlList) other).getEmbedded();
			for (XmlObj subobj : embedded) {
				double m = this.isLike(subobj);
				if (m > 0) {
					matches += m;
					++count;
				}
			}

			if (other.getName() == "link_&")
				matches = (count == embedded.size() ? matches : 0);
			if (other.getName() == "synonyms")
				matches /= count;
		}

		LogUtils.d("---- compare: " + this.printStr() + " isLike " + other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double matches(XmlObj other) {
		double matches = 0;

		if (other instanceof XmlWord) {
			final List<Word> words = this.getWords();
			for (Word word : words) {
				matches += word.matches((XmlWord) other);
			}

		} else if (other instanceof XmlList) {
			int count = 0;
			final List<XmlObj> embedded = ((XmlList) other).getEmbedded();
			for (XmlObj subobj : embedded) {
				double m = this.matches(subobj);
				if (m > 0) {
					matches += m;
					++count;
				}
			}

			if (other.getName() == "link_&")
				matches = (count == embedded.size() ? matches : 0);
			if (other.getName() == "synonyms")
				matches /= count;
		}

		LogUtils.d("---- compare: " + this.printStr() + " matches " + other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double countWords() {
		return this.getWords().size();
	}

	@Override
	public boolean toggle() {
		boolean reset = false;
		List<Word> words = this.getWords();
		for (Word word : words) {
			Word newWord = Taggers.getTagger().toggle(word);
			LogUtils.d("TOGGLE: " + word + " -> " + newWord);
			if (!newWord.equals(word)) {
				reset = true;
				word.set(newWord);
			}
		}
		if (reset) {
			this.setText(Word.join(" ", words));
			this.resetCache();
		}
		LogUtils.d("TOGGLE... " + this.toString());
		return reset;
	}

	@Override
	public String toString() {
		return printStr();
	}

	@Override
	protected String hashString() {
		return text;
	}

	@Override
	public XmlObj copy() {
		XmlWord copy = new XmlWord();
		copy.text = new String(text);
		return copy;
	}
}
