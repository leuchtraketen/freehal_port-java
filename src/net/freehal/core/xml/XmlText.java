package net.freehal.core.xml;

import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.util.LogUtils;

public class XmlText extends XmlObj {

	private String text;

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(Word word) {
		this.text = word.getWord();
	}

	public static XmlObj fromText(String text) {
		XmlText xobj = new XmlText();
		xobj.setText(text);
		return xobj;
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

	@Override
	public boolean prepareWords() {
		if (super.prepareWords()) {
			String[] words = text.split("[^A-Za-z0-9}{][=)(_-]+?");
			for (String word : words) {
				if (word.length() > 0 && word != "1")
					cacheWords.add(new Word(word));
			}
		}
		return true;
	}

	@Override
	public boolean prepareTags(AbstractTagger tagger) {
		if (super.prepareTags(tagger)) {
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

		if (other instanceof XmlText) {
			final List<Word> words = this.getWords();
			for (Word word : words) {
				matches += word.isLike((XmlText) other);
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
		
		LogUtils.d("---- compare: " + this.printStr() + " isLike "
				+ other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double matches(XmlObj other) {
		double matches = 0;

		if (other instanceof XmlText) {
			final List<Word> words = this.getWords();
			for (Word word : words) {
				matches += word.matches((XmlText) other);
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
		
		LogUtils.d("---- compare: " + this.printStr() + " matches "
				+ other.printStr() + " = " + matches);
		return matches;
	}

	@Override
	public double countWords() {
		return this.getWords().size();
	}
}
