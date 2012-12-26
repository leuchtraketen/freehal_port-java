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

import net.freehal.core.lang.fake.FakeGrammar;
import net.freehal.core.util.MultiMap;
import net.freehal.core.xml.Word;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

/**
 * An utility class for holding the currently used grammar.
 * 
 * @author "Tobias Schulz"
 */
public class Grammars {
	private static Grammar grammar = null;

	static {
		grammar = new FakeGrammar();
	}

	/**
	 * Returns the current grammar.
	 * 
	 * @return an instance of {@link Grammar}
	 */
	public static Grammar getGrammar() {
		return grammar;
	}

	/**
	 * Set the current grammar.
	 * 
	 * @param grammar
	 *        the grammar to set
	 */
	public static void setGrammar(Grammar grammar) {
		Grammars.grammar = grammar;
	}

	/**
	 * Returns a string for console output representing the given
	 * {@link java.util.List} of {@link Word} objects.
	 * 
	 * @param words
	 *        the list to print
	 * @return a string
	 */
	public static String printInput(List<Word> words) {
		StringBuilder ss = new StringBuilder();

		// for each word
		for (Word word : words) {
			// ignore invalid words
			if (word.getWord().isEmpty() || word.equals("null")) {
				continue;
			}

			// print it
			ss.append("  - ");
			if (word.hasTags())
				ss.append(word.getTags().getLexicalClassForGrammar());

			else
				ss.append("(no tags)");
			ss.append(": '").append(word.getWord()).append("'\n");
		}

		return ss.toString();
	}

	/**
	 * Returns a string for console output representing the given grammar
	 * output.
	 * 
	 * @param list
	 *        the list to print
	 * @return a string
	 */
	public static String printOutput(List<Entities> list) {
		StringBuilder ss = new StringBuilder();

		for (Entities output : list) {
			for (Entity entity : output) {
				// long
				ss.append(entity.printLong());

				// perl
				MultiMap<String, String> perlmap = entity.toGroups();
				ss.append(Entity.printPerl(perlmap));
			}
		}

		return ss.toString();
	}

	/**
	 * Converts the given grammar output into input data for graphviz.
	 * 
	 * @param list
	 *        the grammar output to convert
	 * @return a string for graphviz
	 */
	public static String printGraph(List<Entities> list) {
		StringBuilder ss = new StringBuilder();
		ss.append("digraph parsed {\n");

		for (Entities output : list) {
			for (Entity entity : output) {
				ss.append(entity.toXml());
			}
			break;
		}

		ss.append("}\n");
		return ss.toString();
	}

	/**
	 * Return an XML code representing the given grammar output.
	 * 
	 * @see Entity#toXml()
	 * @param list
	 *        the list to print as XML
	 * @return a string of XML code
	 */
	public static String printXml(List<Entities> list) {
		StringBuilder ss = new StringBuilder();

		for (Entities output : list) {
			for (Entity entity : output) {
				ss.append(entity.toXml());
			}
			break;
		}

		return ss.toString();
	}

	/**
	 * Convert the given grammr output to an @
	 * {@link XmlUtils.XmlStreamIterator} by using {@link #printXml(List)} and a
	 * {@link XmlUtils.OneStringIterator}.
	 * 
	 * @param parsed
	 *        the grammar output
	 * @return an {@link XmlUtils.XmlStreamIterator}
	 */
	public static XmlStreamIterator asXmlStream(List<Entities> parsed) {
		final String xmlInput = Grammars.printXml(parsed);
		return new XmlUtils.XmlStreamIterator(new XmlUtils.OneStringIterator(xmlInput));
	}
}
