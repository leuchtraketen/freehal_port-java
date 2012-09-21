package net.freehal.core.xml;

import java.util.List;

import net.freehal.core.pos.Tags;

public class Word {

	private String word = new String();
	private Tags tag = null;

	public Word() {
	}

	public Word(String word) {
		this.word = word;
	}

	public Word(Word word) {
		this.word = word.word;
		this.tag = word.tag;
	}

	public Word(String word, Tags tag) {
		this.word = word;
		this.tag = tag;
	}

	public boolean equals(Object o) {
		if (o instanceof Word) {
			return (this.word.equals(((Word) o).word) && this.tag
					.equals(((Word) o).tag));
		} else if (o instanceof String) {
			return (this.word.equals((String) o));
		} else {
			return false;
		}
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void setTags(Tags tag) {
		this.tag = tag;
	}

	public String getWord() {
		return word;
	}

	public int size() {
		return word.length();
	}

	public int length() {
		return word.length();
	}

	public Tags getTags() {
		return tag;
	}

	public boolean hasTags() {
		return tag != null;
	}

	public double isLike(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.isLike(otherWord);
			if (matches > 0)
				return matches * (3.0 * otherWords.size() + 1) / 4.0
						/ otherWords.size();
		}

		return 0;
	}

	private double isLike(Word otherWord) {
		if (this.matches(otherWord) > 0) {
			Tags tags = null;
			if (this.hasTags())
				tags = this.getTags();
			else if (otherWord.hasTags())
				tags = otherWord.getTags();

			double weight = tags == null ? 0.75 : tags.isType("n") ? 1 : tags
					.isType("adj") ? 0.5 : tags.isType("v") ? 0.75 : tags
					.isType("art") ? 0.1 : 0.4;

			return weight;
		} else {
			return 0;
		}
	}

	public double matches(XmlObj other) {
		final List<Word> otherWords = other.getWords();

		for (Word otherWord : otherWords) {
			double matches = this.isLike(otherWord);
			if (matches > 0)
				return matches;
		}

		return 0;
	}

	private double matches(Word otherWord) {
		return (this.word == otherWord.word || this.word.toLowerCase() == otherWord.word
				.toLowerCase()) ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return word+"{"+tag+"}";
	}
}
