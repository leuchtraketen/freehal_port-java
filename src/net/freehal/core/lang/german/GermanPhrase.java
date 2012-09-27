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
package net.freehal.core.lang.german;

import java.util.ArrayList;
import java.util.List;

import net.freehal.core.phrase.Phrase2012;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlList;

public class GermanPhrase extends Phrase2012 {
	public GermanPhrase() {
		arranger = new GermanWordArranger();
		modifiers.addLast(new NounsUpperCaseModifier());
	}

	protected class GermanWordArranger implements WordArranger {
		@Override
		public List<Word> arrangeWords(XmlList xfact) {
			List<Word> verbs = xfact.part("verb").getWords();
			List<Word> subject = xfact.part("subject").getWords();
			List<Word> object = xfact.part("object").getWords();
			List<Word> adverbs = xfact.part("adverbs").getWords();
			
			List<Word> arranged = new ArrayList<Word>();
			arranged.addAll(subject);
			arranged.addAll(verbs);
			arranged.addAll(object);
			arranged.addAll(adverbs);
			
			return arranged;
		}
	}

	protected class NounsUpperCaseModifier implements WordModifier {
		@Override
		public List<Word> modifyWords(List<Word> words, XmlList xfact) {
			for (Word word : words) {
				if (word.hasTags() && word.getTags().isType("n")) {
					word.setWord(StringUtils.ucfirst(word.getWord()));
				}
			}
			return words;
		}
	}
}
