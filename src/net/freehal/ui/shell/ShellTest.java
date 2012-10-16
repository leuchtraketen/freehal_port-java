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
package net.freehal.ui.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.compat.sunjava.StandardFreehalFile;
import net.freehal.compat.sunjava.StandardHttpClient;
import net.freehal.compat.sunjava.StandardLogUtils;
import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.answer.AnswerProviders;
import net.freehal.core.database.Database;
import net.freehal.core.database.DatabaseAnswerProvider;
import net.freehal.core.database.FactIndex;
import net.freehal.core.database.StandardDatabase;
import net.freehal.core.database.SynonymIndex;
import net.freehal.core.filter.FactFilters;
import net.freehal.core.filter.FilterNoNames;
import net.freehal.core.filter.FilterNot;
import net.freehal.core.filter.FilterQuestionExtra;
import net.freehal.core.filter.FilterQuestionWhat;
import net.freehal.core.filter.FilterQuestionWho;
import net.freehal.core.grammar.Grammar;
import net.freehal.core.grammar.Grammars;
import net.freehal.core.lang.Languages;
import net.freehal.core.lang.german.GermanGrammar;
import net.freehal.core.lang.german.GermanLanguage;
import net.freehal.core.lang.german.GermanParser;
import net.freehal.core.lang.german.GermanPredefinedAnswerProvider;
import net.freehal.core.lang.german.GermanRandomAnswerProvider;
import net.freehal.core.lang.german.GermanTagger;
import net.freehal.core.lang.german.GermanWording;
import net.freehal.core.parser.Parser;
import net.freehal.core.parser.Sentence;
import net.freehal.core.pos.Tagger;
import net.freehal.core.pos.Taggers;
import net.freehal.core.pos.Tags;
import net.freehal.core.pos.storage.TagDatabase;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.Serializer;
import net.freehal.core.storage.StandardStorage;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.AbstractFreehalFile;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.wording.Wording;
import net.freehal.core.wording.Wordings;
import net.freehal.core.xml.FactProviders;
import net.freehal.core.xml.SynonymProviders;
import net.freehal.plugin.berkeleydb.BerkeleyDb;
import net.freehal.plugin.wikipedia.GermanWikipedia;
import net.freehal.plugin.wikipedia.WikipediaClient;
import net.freehal.plugin.wikipedia.WikipediaPlugin;

/**
 * This class is a reference implementation of a simple console user interface.
 * It uses all main APIs and runs on every normal java platform.
 * 
 * @author "Tobias Schulz"
 */
public class ShellTest {
	private static void init() {
		// set the virtual file implementations
		FreehalFiles.add(FreehalFiles.ALL_PROTOCOLS, StandardFreehalFile.newFactory());
		FreehalFiles.add("sqlite", FakeFreehalFile.newFactory());
		FreehalFiles.add("http", StandardHttpClient.newFactory());
		FreehalFiles.add("wikipedia", WikipediaClient.newFactory());

		// how and where to print the log
		// example: all debug messages from the class "DiskDatabase" and the sub
		// packages "xml" (net.freehal.core.xml) and "filter"
		// (net.freehal.core.filter) are not logged to console output, but
		// everything is written into a log file
		StandardLogUtils log = new StandardLogUtils();
		log.to(StandardLogUtils.ConsoleLogStream.create(System.out).addFilter("DiskDatabase", LogUtils.DEBUG)
				.addFilter("xml", LogUtils.DEBUG).addFilter("filter", LogUtils.DEBUG));
		log.to(StandardLogUtils.FileLogStream.create("../stdout.txt"));
		LogUtils.set(log);

		// set the language and the base directory (if executed in "bin/", the
		// base directory is ".."). The "StandardStorage" implementation expects
		// a "lang_xy" directory there which contains the database files.
		Languages.setLanguage(new GermanLanguage());
		Storages.setStorage(new StandardStorage(".."));

		// now language and filesystem stuff are ready!
		LogUtils.startProgress("init");

		LogUtils.updateProgress("set up grammar");

		// initialize the grammar
		// (also possible: EnglishGrammar, GermanGrammar, FakeGrammar)
		Grammar grammar = new GermanGrammar();
		grammar.readGrammar(FreehalFiles.getFile("grammar.txt"));
		Grammars.setGrammar(grammar);

		LogUtils.startProgress("set up part of speech tagger");

		// initialize the part of speech tagger
		// (also possible: EnglishTagger, GermanTagger, FakeTagger)
		// the parameter is either a TaggerCacheMemory (faster, higher memory
		// usage) or a TaggerCacheDisk (slower, less memory usage)
		KeyValueDatabase<Tags> tags = new BerkeleyDb<Tags>(Storages.getCacheDirectory().getChild("tagger"),
				new Tags.StringSerializer());
		KeyValueDatabase<String> meta = new BerkeleyDb<String>(Storages.getCacheDirectory()
				.getChild("tagger"), new Serializer.StringSerializer());
		Tagger tagger = new GermanTagger(TagDatabase.newFactory(tags, meta));
		// Tagger tagger = new GermanTagger(MemoryTagMap.newFactory());
		tagger.readTagsFrom(FreehalFiles.getFile("guessed.pos"));
		tagger.readTagsFrom(FreehalFiles.getFile("brain.pos"));
		tagger.readTagsFrom(FreehalFiles.getFile("memory.pos"));
		tagger.readRegexFrom(FreehalFiles.getFile("regex.pos"));
		tagger.readToggleWordsFrom(FreehalFiles.getFile("toggle.csv"));
		Taggers.setTagger(tagger);

		LogUtils.stopProgress();

		// how to phrase the output sentences
		// (also possible: EnglishWording, GermanWording, FakeWording)
		Wording phrase = new GermanWording();
		Wordings.setWording(phrase);

		LogUtils.startProgress("set up database");

		// we need to store facts...
		FactIndex facts = new FactIndex();
		// ... and synonyms
		SynonymIndex synonyms = new SynonymIndex();
		// add both to their utility classes
		FactProviders.addFactProvider(facts);
		SynonymProviders.addSynonymProvider(synonyms);
		// both are components of a database!
		Database database = new StandardDatabase();
		database.addComponent(facts);
		database.addComponent(synonyms);
		// update the cache of that database...
		// while updating the cache, a cache_xy/ directory will be filled with
		// information from the database files in lang_xy/
		database.updateCache();

		LogUtils.stopProgress();

		// the Wikipedia plugin is a FactProvider too!
		WikipediaPlugin wikipedia = new WikipediaPlugin(new GermanWikipedia());
		FactProviders.addFactProvider(wikipedia);

		// Freehal has different ways to find an answer for an input
		AnswerProviders.add(new GermanPredefinedAnswerProvider());
		AnswerProviders.add(new DatabaseAnswerProvider(facts));
		AnswerProviders.add(wikipedia);
		AnswerProviders.add(new GermanRandomAnswerProvider());
		AnswerProviders.add(new FakeAnswerProvider());

		// fact filters are used to filter the best-matching fact in the
		// database
		FactFilters.getInstance().add(new FilterNot()).add(new FilterNoNames()).add(new FilterQuestionWho())
				.add(new FilterQuestionWhat()).add(new FilterQuestionExtra());

		LogUtils.stopProgress();
	}

	public static void main(String[] args) {
		// initialize everything
		init();

		for (String input : args) {
			// also possible: EnglishParser, GermanParser, FakeParser
			Parser p = new GermanParser(input);

			// parse the input and get a list of sentences
			final List<Sentence> inputParts = p.getSentences();

			List<String> outputParts = new ArrayList<String>();
			// for each sentence...
			for (Sentence s : inputParts) {
				// get the answer using the AnswerProvider API
				outputParts.add(AnswerProviders.getAnswer(s));
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

class FakeFreehalFile extends AbstractFreehalFile {

	private FakeFreehalFile(File file) {
		super(file);
	}

	public static Factory<FreehalFile, String> newFactory() {
		return new Factory<FreehalFile, String>() {
			@Override
			public FreehalFile newInstance(String b) {
				return new FakeFreehalFile(new File(b));
			}
		};
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public FreehalFile[] listFiles() {
		return new FreehalFile[0];
	}

	@Override
	public long length() {
		return 0;
	}

	@Override
	public boolean mkdirs() {
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public FreehalFile getChild(String path) {
		return null;
	}

	@Override
	public FreehalFile getChild(FreehalFile path) {
		return null;
	}

	@Override
	public Iterable<String> readLines() {
		return null;
	}

	@Override
	public String read() {
		return null;
	}

	@Override
	public void append(String s) {}

	@Override
	public void write(String s) {}

	@Override
	public int countLines() {
		return 0;
	}
}
