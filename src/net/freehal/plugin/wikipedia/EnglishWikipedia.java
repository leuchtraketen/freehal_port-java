package net.freehal.plugin.wikipedia;

public class EnglishWikipedia implements WikipediaLanguageUtils {

	@Override
	public boolean lineContainsDisambiguation(String line) {
		return line.contains("Other uses");
	}

	@Override
	public boolean lineContainsFile(String line) {
		return line.contains("[[File:");
	}

}
