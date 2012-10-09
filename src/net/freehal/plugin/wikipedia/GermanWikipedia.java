package net.freehal.plugin.wikipedia;

public class GermanWikipedia implements WikipediaLanguageUtils {

	@Override
	public boolean lineContainsDisambiguation(String line) {
		return line.contains("Dieser Artikel");
	}

	@Override
	public boolean lineContainsFile(String line) {
		return line.contains("[[Datei:");
	}

}
