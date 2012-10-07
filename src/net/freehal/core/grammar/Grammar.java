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
package net.freehal.core.grammar;

import java.util.List;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.xml.Word;

/**
 * A grammar; see this description about how to parse a fact. <br />
 * <br />
 * 
 * You can parse a {@link java.util.List} of {@link Word} objects like the
 * following example:
 * 
 * <pre>
 * List&lt;Word&gt; wordsList = new ArrayList&lt;Word&gt;();
 * // fill the word list here
 * 
 * List&lt;Entities&gt; parsed = Grammars.getGrammar().parse(wordsList);
 * final XmlStreamIterator stream = Grammars.asXmlStream(parsed);
 * XmlUtils.readXmlFacts(stream, null, new XmlFactReciever() {
 * 
 * 	&#064;Override
 * 	public void useXmlFact(XmlFact xfact, int countFacts, long start, FreehalFile filename,
 * 			int countFactsSoFar) {
 * 		// the parsed fact in now in &quot;xfact&quot;!
 * 	}
 * });
 * </pre>
 * 
 * @author "Tobias Schulz"
 */
public abstract class Grammar {

	/**
	 * The part of speech tags to use in a freehal grammar.
	 * 
	 * @author "Tobias Schulz"
	 */
	public static final class LexicalClass {
		private LexicalClass() {}

		/** a comma (if used between main and sub clauses) **/
		public static final String KOMMA = "d-komma";
		/** a verb */
		public static final String VERB = "d-verb";
		/** an article */
		public static final String ARTICLE = "d-article";
		/** an adjective */
		public static final String ADJECTIVE = "d-adjective";
		/** a preposition */
		public static final String PREPOSITION = "d-preposition";
		/** a question word or a coordinating or subordinating conjunction */
		public static final String QUESTIONWORD = "d-questionword";
		/** a logical or correlative conjunction */
		public static final String LINKING = "d-linking";
		/** a noun or a pronoun, but no prename or title or job name! */
		public static final String NOUN = "d-noun";
		/** a prename or title or job name */
		public static final String TITLE = "d-title";
		/** for words that could not be tagged... */
		public static final String NULL = "q";
	}

	/**
	 * Parse the given {@link java.util.List} of {@link Word} objects.
	 * 
	 * @param words
	 *        the word list
	 * @return a list of {@link Entity} lists
	 */
	public abstract List<Entities> parse(List<Word> words);

	/**
	 * Read the grammar to use from a file.
	 * 
	 * @param file
	 *        the grammar file to read
	 * @return {@code true} if it was read successfully, {@code false} otherwise
	 */
	public abstract boolean readGrammar(FreehalFile file);
}
