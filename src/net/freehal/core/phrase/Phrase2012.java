/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.phrase;

import java.util.LinkedList;
import java.util.List;

import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlList;

public class Phrase2012 implements AbstractPhrase {

	protected WordArranger arranger = new FakeWordArranger();
	protected LinkedList<WordModifier> modifiers = new LinkedList<WordModifier>();

	public Phrase2012() {
		modifiers.addFirst(new AddTagsModifier());
	}

	@Override
	public String phrase(XmlList best) {
		List<Word> words = arrangeWords(best);
		words = modifyWords(words, best);
		return join(words);
	}

	private String join(List<Word> words) {
		String output = Word.join(" ", words);
		output = RegexUtils.replace(output, "[ _]+", " ");
		output = RegexUtils.replace(output, "[ ]+([.?!;,])", "$1");
		if (!RegexUtils.find(output, "[?!]"))
			output += ".";
		return output;
	}

	private List<Word> modifyWords(List<Word> words, XmlList xfact) {
		for (WordModifier modifier : modifiers) {
			words = modifier.modifyWords(words, xfact);
		}
		return words;
	}

	private List<Word> arrangeWords(XmlList xfact) {
		return arranger.arrangeWords(xfact);
	}

	public static interface WordArranger {
		public List<Word> arrangeWords(XmlList xfact);
	}

	public static interface WordModifier {
		public List<Word> modifyWords(List<Word> words, XmlList xfact);
	}

	public static class FakeWordArranger implements WordArranger {
		@Override
		public List<Word> arrangeWords(XmlList xfact) {
			return xfact.getWords();
		}
	}

	public static class AddTagsModifier implements WordModifier {
		@Override
		public List<Word> modifyWords(List<Word> words, XmlList xfact) {
			for (Word word : words) {
				if (!word.hasTags()) {
					word.setTags(FreehalConfig.getTagger());
				}
			}
			return words;
		}
	}
}
