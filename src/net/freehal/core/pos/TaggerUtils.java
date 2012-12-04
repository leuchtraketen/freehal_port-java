package net.freehal.core.pos;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlList;

public class TaggerUtils {

	public static List<Word> filterIndexWords(List<Word> words) {
		List<Word> usefulWords = new ArrayList<Word>();
		for (Word word : words) {
			if (Taggers.getTagger().isIndexWord(word)) {
				LogUtils.i("index word: " + word);
				usefulWords.add(word);
			} else
				LogUtils.i("no index word: " + word);

		}
		return usefulWords;
	}

	public static List<Word> getIndexWords(XmlList xfact) {
		List<Word> words = xfact.getWords();
		List<Word> usefulWords = TaggerUtils.filterIndexWords(words);
		return usefulWords;
	}

	public static List<Word> getIndexWords(Word word) {
		List<Word> words = new ArrayList<Word>();
		words.add(word);
		return words;
	}
}
