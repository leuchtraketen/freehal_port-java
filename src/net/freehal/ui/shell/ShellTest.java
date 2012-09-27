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
package net.freehal.ui.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.compat.sunjava.FileUtilsStandard;
import net.freehal.compat.sunjava.FreehalConfigStandard;
import net.freehal.compat.sunjava.LogUtilsStandard;
import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.answer.AnswerProviders;
import net.freehal.core.database.DatabaseAnswerProvider;
import net.freehal.core.database.DatabaseImpl;
import net.freehal.core.database.DiskDatabase;
import net.freehal.core.filter.FactFilters;
import net.freehal.core.filter.FilterNoNames;
import net.freehal.core.filter.FilterNot;
import net.freehal.core.filter.FilterQuestionExtra;
import net.freehal.core.filter.FilterQuestionWhat;
import net.freehal.core.filter.FilterQuestionWho;
import net.freehal.core.grammar.AbstractGrammar;
import net.freehal.core.lang.german.GermanGrammar;
import net.freehal.core.lang.german.GermanParser;
import net.freehal.core.lang.german.GermanPhrase;
import net.freehal.core.lang.german.GermanTagger;
import net.freehal.core.parser.AbstractParser;
import net.freehal.core.parser.Sentence;
import net.freehal.core.phrase.AbstractPhrase;
import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.TaggerCacheMemory;
import net.freehal.core.predefined.PredefinedAnswerProvider;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.StringUtils;

/**
 * This class is a reference implementation of a simple console user interface.
 * It uses all main APIs and runs on every normal java platform.
 * 
 * @author tobias
 */
public class ShellTest {
	private static void init() {
		// file access
		FileUtils.set(new FileUtilsStandard());

		// how and where to print the log
		// example: all debug messages from the class "DiskDatabase" and the sub
		// packages "xml" (net.freehal.core.xml) and "filter"
		// (net.freehal.core.filter) are not logged to console output, but
		// everything is written into a log file
		LogUtilsStandard log = new LogUtilsStandard();
		log.to(LogUtilsStandard.ConsoleLogStream.create(System.out)
				.addFilter("DiskDatabase", "debug").addFilter("xml", "debug")
				.addFilter("filter", "debug"));
		log.to(LogUtilsStandard.FileLogStream.create("../stdout.txt"));
		LogUtils.set(log);

		// set the language and the base directory (if executed in "bin/", the
		// base directory is ".."). Freehal expects a "lang_xy" directory there
		// which contains the database files.
		FreehalConfig.set(new FreehalConfigStandard().setLanguage("de")
				.setPath(new File("..")));

		// initialize the grammar
		// (also possible: EnglishGrammar, GermanGrammar, FakeGrammar)
		AbstractGrammar grammar = new GermanGrammar();
		grammar.readGrammar(new File("grammar.txt"));
		FreehalConfig.setGrammar(grammar);

		// initialize the part of speech tagger
		// (also possible: EnglishTagger, GermanTagger, FakeTagger)
		// the parameter is either a TaggerCacheMemory (faster, higher memory
		// usage) or a TaggerCacheDisk (slower, less memory usage)
		AbstractTagger tagger = new GermanTagger(new TaggerCacheMemory());
		tagger.readTagsFrom(new File("guessed.pos"));
		tagger.readTagsFrom(new File("brain.pos"));
		tagger.readTagsFrom(new File("memory.pos"));
		tagger.readRegexFrom(new File("regex.pos"));
		tagger.readToggleWordsFrom(new File("toggle.csv"));
		FreehalConfig.setTagger(tagger);

		// how to phrase the output sentences
		// (also possible: EnglishPhrase, GermanPhrase, FakePhrase)
		AbstractPhrase phrase = new GermanPhrase();
		FreehalConfig.setPhrase(phrase);

		// initialize the database
		// (also possible: DiskDatabase, FakeDatabase)
		DatabaseImpl database = new DiskDatabase();
		// while updating the cache, a cache_xy/ directory will be filled with
		// information from the database files in lang_xy/
		database.updateCache();

		// Freehal has different ways to find an answer for an input
		AnswerProviders.getInstance().add(new PredefinedAnswerProvider())
				.add(new DatabaseAnswerProvider(database))
				.add(new FakeAnswerProvider());

		// fact filters are used to filter the best-matching fact in the
		// database
		FactFilters.getInstance().add(new FilterNot()).add(new FilterNoNames())
				.add(new FilterQuestionWho()).add(new FilterQuestionWhat())
				.add(new FilterQuestionExtra());
	}

	public static void main(String[] args) {
		// initialize everything
		init();

		for (String input : args) {
			// also possible: EnglishParser, GermanParser, FakeParser
			AbstractParser p = new GermanParser(input);

			// parse the input and get a list of sentences
			final List<Sentence> inputParts = p.getSentences();

			List<String> outputParts = new ArrayList<String>();
			// for each sentence...
			for (Sentence s : inputParts) {
				// get the answer using the AnswerProvider API
				outputParts.add(AnswerProviders.getInstance().getAnswer(s));
			}
			// put all answers together
			final String output = StringUtils.join(" ", outputParts);
			System.out.println("Input: " + input);
			System.out.println("Output: " + output);
		}
	}
}

/**
 * This is just for testing the AnswerProvider API
 * 
 * @author tobias
 */
class FakeAnswerProvider implements AnswerProvider {

	@Override
	public String getAnswer(Sentence s) {
		return "Hello World!";
	}

}
