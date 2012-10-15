package net.freehal.plugin.wikipedia;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.freehal.core.lang.Languages;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.XmlUtils;

public class WikipediaClient implements FreehalFile {

	private FreehalFile http;
	private String lang;
	private String name;

	private String getArticleUrl(String lang, String name) {
		return lang + ".wikipedia.org/w//api.php?action=query" + "&prop=revisions&rvlimit=1&rvprop=content"
				+ "&format=xml&titles=" + encode(name);
	}

	private String getWikipediaSearchUrl(String lang, String name) {
		return lang + ".wikipedia.org/w/api.php?action=opensearch"
				+ "&limit=10&namespace=0&format=xml&search=" + encode(name);
	}

	private String getGoogleSearchUrl(String lang, String name) {
		return "ajax.googleapis.com/ajax/services/search/web?v=1.0&q=site:" + lang + ".wikipedia.org%20"
				+ encode(name); // StringUtils.replace(name, " ", "%20");
	}

	private String encode(String name) {
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LogUtils.e(ex);
			return name;
		}
	}

	private WikipediaClient(String path) {
		if (path == null)
			throw new IllegalArgumentException("path must not be null!");

		String[] parts = path.split("[/]+", 2);
		if (parts.length == 2) {
			this.lang = parts[0];
			this.name = parts[1];
		} else if (parts.length == 1) {
			this.lang = Languages.getLanguage().getCode();
			this.name = parts[0];
		}
		this.http = FreehalFiles.getFile("http", getArticleUrl(lang, name));
		check();
	}

	public static Factory<FreehalFile, String> newFactory() {
		return new Factory<FreehalFile, String>() {
			@Override
			public FreehalFile newInstance(String b) {
				return new WikipediaClient(b);
			}
		};
	}

	@Override
	public int compareTo(FreehalFile o) {
		if (o instanceof WikipediaClient)
			return http.compareTo(((WikipediaClient) o).http);
		else
			return http.compareTo(o);
	}

	@Override
	public FreehalFile getChild(String path) {
		return new WikipediaClient(path);
	}

	@Override
	public FreehalFile getChild(FreehalFile path) {
		return path;
	}

	@Override
	public File getFile() {
		return http.getFile();
	}

	@Override
	public boolean isAbsolute() {
		return true;
	}

	@Override
	public String getAbsolutePath() {
		return getPath();
	}

	@Override
	public String getPath() {
		return lang + "/" + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public FreehalFile[] listFiles() {
		return http.listFiles();
	}

	@Override
	public long length() {
		return read().length();
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
	public Iterable<String> readLines() {
		return new XmlUtils.StripXmlTagsIterator(http.readLines());
	}

	@Override
	public String read() {
		return XmlUtils.stripXmlTags(http.read());
	}

	@Override
	public void append(String s) {}

	@Override
	public void write(String s) {}

	private void check() {
		for (String line : http.readLines()) {
			if (line.contains("<page ") && line.contains("missing=\"\"")) {
				doSearch();
			}
		}
	}

	private void doSearch() {
		List<String> match;

		// use the wikipedia search
		LogUtils.i("1");
		http = FreehalFiles.getFile("http", getWikipediaSearchUrl(lang, name));
		for (String line : http.readLines()) {
			if ((match = RegexUtils.imatch(line, "<Url.*>([^<]+)</Url>")) != null) {
				String[] parts = match.get(0).split("/");
				LogUtils.i("2");
				http = FreehalFiles.getFile("http", getArticleUrl(lang, parts[parts.length - 1]));
				if (http.length() > 0)
					return;
			}
		}

		// use the google ajax search api
		LogUtils.i("3");
		http = FreehalFiles.getFile("http", getGoogleSearchUrl(lang, name));
		for (String line : http.readLines()) {
			if ((match = RegexUtils.imatch(line, "\"url\":\"([^\"]+)\"")) != null) {
				String[] parts = match.get(0).split("/");
				LogUtils.i("4");
				http = FreehalFiles.getFile("http", getArticleUrl(lang, parts[parts.length - 1]));
				if (http.length() > 0)
					return;
			}
		}

		http = null;
	}

	@Override
	public int countLines() {
		int countOfLines = 0;
		Iterable<String> lines = readLines();
		for (@SuppressWarnings("unused") String line : lines) {
			++countOfLines;
		}
		return countOfLines;
	}
}
