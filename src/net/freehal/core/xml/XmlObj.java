package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.Tags;

public abstract class XmlObj {

	protected String name = new String();

	protected boolean isCachedWords = false;
	protected boolean isCachedTags = false;
	protected List<Word> cacheWords = new ArrayList<Word>();

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

	protected String printXml(int level, int secondlevel) {
		return "";
	}

	public String printStr() {
		return "";
	}

	public String printText() {
		return "";
	}

	protected boolean prepareWords() {
		if (isCachedWords)
			return false;
		cacheWords.clear();
		isCachedWords = true;
		return true;
	}

	protected boolean prepareTags(AbstractTagger tagger) {
		if (isCachedTags)
			return false;

		prepareWords();
		isCachedTags = (tagger != null);
		return (tagger != null);
	}

	public void tag(AbstractTagger tagger) {
		prepareTags(tagger);
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
		cacheWords = new ArrayList<Word>();
	}

	public abstract double isLike(XmlObj other);

	public abstract double matches(XmlObj other);

	public abstract double countWords();

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

	public abstract boolean toggle(AbstractTagger tagger);

	@Override
	public abstract String toString();

	@Override
	public int hashCode() {
		return hashString().hashCode();
	}
	
	protected abstract String hashString();

	@Override
	public boolean equals(Object other) {
		return this.hashCode() == other.hashCode();
	}
}
