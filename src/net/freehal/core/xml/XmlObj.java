package net.freehal.core.xml;

import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.Tags;

public abstract class XmlObj {

	protected String name = new String();

	protected boolean isCachedWords;
	protected boolean isCachedTags;
	protected List<Word> cacheWords;

	public XmlObj() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void trim() {
		// does nothing
	}

	public String printXml() {
		return printXml(0, 0);
	}

	public String printXml(int level, int secondlevel) {
		return "";
	}

	public String printStr() {
		return "";
	}

	public String printText() {
		return "";
	}

	public boolean prepareWords() {
		if (isCachedWords)
			return false;
		cacheWords.clear();
		isCachedWords = true;
		return true;
	}

	public boolean prepareTags(AbstractTagger tagger) {
		if (isCachedTags)
			return false;

		prepareWords();
		isCachedTags = (tagger != null);
		return (tagger != null);
	}

	public List<Word> getWords() {
		if (!isCachedWords) {
			prepareWords();
		}
		return cacheWords;
	}

	public int getWords(List<Word> words) {
		if (!isCachedWords) {
			prepareWords();
		}
		words.addAll(cacheWords);
		return cacheWords.size();
	}

	public void resetCache() {
		isCachedWords = false;
		isCachedTags = false;
		cacheWords = null;
	}

	abstract public double isLike(XmlObj other);

	abstract double matches(XmlObj other);

	abstract public double countWords();

	public double countTags(Tags tags) {
		List<Word> words = this.getWords();
		int amount = 0;
		for (Word word : words) {
			if (word.hasTags()) {
				if (word.getTags().isType(tags.getType())) {
					++amount;
				}
			}
		}

		return (double) amount;
	}
}
