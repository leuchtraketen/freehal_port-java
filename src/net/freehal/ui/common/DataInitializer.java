package net.freehal.ui.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.compat.sunjava.StandardFreehalFile;
import net.freehal.compat.sunjava.StandardHttpClient;
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
import net.freehal.core.lang.Language;
import net.freehal.core.lang.LanguageSpecific;
import net.freehal.core.lang.Languages;
import net.freehal.core.lang.english.EnglishLanguage;
import net.freehal.core.lang.fake.FakeAnswerProvider;
import net.freehal.core.lang.fake.FakeFreehalFile;
import net.freehal.core.lang.fake.FakeLanguage;
import net.freehal.core.lang.german.GermanLanguage;
import net.freehal.core.logs.FilteredLog;
import net.freehal.core.logs.StandardLogUtils;
import net.freehal.core.logs.listener.ColoredLog;
import net.freehal.core.logs.listener.LogDestination;
import net.freehal.core.logs.listener.UncoloredLog;
import net.freehal.core.logs.output.ConsoleLog;
import net.freehal.core.logs.output.FileLog;
import net.freehal.core.parser.Parser;
import net.freehal.core.parser.Sentence;
import net.freehal.core.pos.Tagger;
import net.freehal.core.pos.Taggers;
import net.freehal.core.pos.Tags;
import net.freehal.core.pos.storage.TagDatabase;
import net.freehal.core.predefined.PredefinedAnswerProvider;
import net.freehal.core.predefined.RandomAnswerProvider;
import net.freehal.core.reasoning.FactReasoning;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.Serializer;
import net.freehal.core.storage.StandardStorage;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.util.SystemUtils;
import net.freehal.core.wording.Wording;
import net.freehal.core.wording.Wordings;
import net.freehal.core.xml.FactProviders;
import net.freehal.core.xml.SynonymProviders;
import net.freehal.core.xml.XmlFact;
import net.freehal.plugin.berkeleydb.BerkeleyDb;
import net.freehal.plugin.berkeleydb.BerkeleyFile;
import net.freehal.plugin.wikipedia.GermanWikipedia;
import net.freehal.plugin.wikipedia.WikipediaClient;
import net.freehal.plugin.wikipedia.WikipediaPlugin;

public class DataInitializer {

	private static Set<Language> isDataInitialized = new HashSet<Language>();

	public static synchronized void initializeTerminalLogging(StandardLogUtils log) {
		// the Linux/Unix implementation uses ANSI colors
		LogDestination console = null;
		switch (SystemUtils.getOperatingSystem()) {
		case LINUX:
		case UNIX:
		case MACOSX:
			console = new ColoredLog(new ConsoleLog(System.out), ColoredLog.ANSI);
			break;
		case WINDOWS:
		default:
			console = new UncoloredLog(new ConsoleLog(System.out));
			break;
		}
		// everything except debug messages from some classes and packages
		// are logged to console output
		FilteredLog consoleFiltered = new FilteredLog.StandardFilteredLog(console);
		consoleFiltered.addFilter("DiskDatabase", LogUtils.DEBUG);
		consoleFiltered.addFilter("xml", LogUtils.DEBUG);
		consoleFiltered.addFilter("filter", LogUtils.DEBUG);
		log.addDestination(consoleFiltered);
	}

	public static synchronized void initializeFileLogging(StandardLogUtils log, String logfile) {
		log.addDestination(new UncoloredLog(new FileLog(logfile)));
	}

	public static synchronized void initializeLogging(String baseDirectory, String logfile,
			boolean showLogTerminal) {
		// how and where to print the log.
		StandardLogUtils logger = new StandardLogUtils();

		// show logs in terminal?
		if (showLogTerminal) {
			initializeTerminalLogging(logger);
		}

		// all messages are written into a log file
		if (logfile != null) {
			initializeFileLogging(logger, logfile);
		}

		LogUtils.set(logger);
	}

	public static synchronized void initializeFilesystem() {
		// set the virtual file implementations
		FreehalFiles.addImplementation(FreehalFiles.ALL_PROTOCOLS, StandardFreehalFile.newFactory());
		FreehalFiles.addImplementation("sqlite", FakeFreehalFile.newFactory());
		FreehalFiles.addImplementation("http", StandardHttpClient.newFactory());
		FreehalFiles.addImplementation("wikipedia", WikipediaClient.newFactory());
		FreehalFiles.addImplementation("berkeley", BerkeleyFile.newFactory());
	}

	public static synchronized void initializeStorage(String baseDirectory) {
		// initialize the directory structure. The "StandardStorage"
		// implementation expects a "lang_xy" directory there which contains the
		// database files.
		Storages.setStorage(new StandardStorage(baseDirectory));
	}

	public static synchronized void initializeLanguage(Language language) {
		// initialize the languages
		FakeLanguage.initializeDefaults();
		GermanLanguage.initializeDefaults();
		EnglishLanguage.initializeDefaults();

		// set the language
		Languages.setLanguage(language);
	}

	public static synchronized void initializeLanguageSpecificData(Set<String> params) {
		// abort if already initialized for that language
		if (isDataInitialized.contains(Languages.getCurrentLanguage())) {
			return;
		}

		// now language and filesystem stuff are ready!
		LogUtils.startProgress("init");

		LogUtils.updateProgress("set up grammar");

		// initialize the grammar
		// (also possible: EnglishGrammar, GermanGrammar, FakeGrammar)
		Grammar grammar = LanguageSpecific.chooseByCurrentLanguage(Grammar.class);
		grammar.readGrammar(new FreehalFile("grammar.txt"));
		Grammars.setGrammar(grammar);

		LogUtils.startProgress("set up part of speech tagger");

		// this database is shared by several classes for storing metadata
		KeyValueDatabase<String> meta = new BerkeleyDb<String>(Storages.getCacheDirectory().getChild("meta"),
				new Serializer.StringSerializer());

		// initialize the part of speech tagger
		// (also possible: EnglishTagger, GermanTagger, FakeTagger)
		// the parameter is either a TaggerCacheMemory (faster, higher memory
		// usage) or a TaggerCacheDisk (slower, less memory usage)
		KeyValueDatabase<Tags> tags = new BerkeleyDb<Tags>(Storages.getCacheDirectory().getChild("tagger"),
				new Tags.StringSerializer());
		Tagger tagger = LanguageSpecific.chooseByCurrentLanguage(Tagger.class);
		tagger.setDatabase(TagDatabase.newFactory(tags, meta));
		// Tagger tagger = new GermanTagger(MemoryTagMap.newFactory());
		tagger.readTagsFrom(new FreehalFile("guessed.pos"));
		tagger.readTagsFrom(new FreehalFile("brain.pos"));
		tagger.readTagsFrom(new FreehalFile("memory.pos"));
		tagger.readRegexFrom(new FreehalFile("regex.pos"));
		tagger.readToggleWordsFrom(new FreehalFile("toggle.csv"));
		Taggers.setTagger(tagger);

		LogUtils.stopProgress();

		// how to phrase the output sentences
		// (also possible: EnglishWording, GermanWording, FakeWording)
		Wording phrase = LanguageSpecific.chooseByCurrentLanguage(Wording.class);
		Wordings.setWording(phrase);

		LogUtils.startProgress("set up database");

		// we need to store facts...
		KeyValueDatabase<Iterable<XmlFact>> factsCache = new BerkeleyDb<Iterable<XmlFact>>(Storages
				.getCacheDirectory().getChild("database/facts"), new XmlFact.StringSerializer());
		FactIndex facts = new FactIndex(factsCache);
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

		tags.compress();
		factsCache.compress();
		meta.compress();

		LogUtils.stopProgress();

		// do reasoning processes if requested
		if (params.contains("reasoning")) {
			FactReasoning reasoning = new FactReasoning(facts);
			reasoning.doIdle();
		}

		// the Wikipedia plugin is a FactProvider too!
		WikipediaPlugin wikipedia = new WikipediaPlugin(new GermanWikipedia());
		FactProviders.addFactProvider(wikipedia);

		// Freehal has different ways to find an answer for an input
		AnswerProviders.add(LanguageSpecific.chooseByCurrentLanguage(PredefinedAnswerProvider.class));
		AnswerProviders.add(new DatabaseAnswerProvider(facts));
		AnswerProviders.add(wikipedia);
		AnswerProviders.add(LanguageSpecific.chooseByCurrentLanguage(RandomAnswerProvider.class));
		AnswerProviders.add(new FakeAnswerProvider());

		// fact filters are used to filter the best-matching fact in the
		// database
		FactFilters.getInstance().add(new FilterNot()).add(new FilterNoNames()).add(new FilterQuestionWho())
				.add(new FilterQuestionWhat()).add(new FilterQuestionExtra());

		LogUtils.stopProgress();

		// mark this language as initialized
		isDataInitialized.add(Languages.getCurrentLanguage());
	}

	public static synchronized String processInput(String input) {
		LogUtils.i("input: " + input);

		// also possible: EnglishParser, GermanParser, FakeParser
		Parser p = LanguageSpecific.chooseByCurrentLanguage(Parser.class);
		p.parse(input);

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

		LogUtils.i("output: " + output);
		return output;
	}
}
