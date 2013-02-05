package net.freehal.compat.sunjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import net.freehal.compat.sunjava.StandardFreehalFile.BufferedReaderIterator;
import net.freehal.compat.sunjava.StandardFreehalFile.NullIterator;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFileImpl;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

/**
 * This class represents a HTTP[S] URL.
 * 
 * @author "Tobias Schulz"
 */
public class StandardHttpClient implements FreehalFileImpl {

	protected String protocol;
	private String path;

	private StandardHttpClient(String path) {
		if (path.startsWith("https://"))
			protocol = "https";
		else
			protocol = "http";
		path = StringUtils.replace(path, "http://", "");
		path = StringUtils.replace(path, "https://", "");
		path = RegexUtils.replace(path, "[/]+", "/");
		this.path = path;
	}

	public static FreehalFiles.Factory newFactory() {
		return new FreehalFiles.Factory() {
			@Override
			public FreehalFileImpl newInstance(String path) {
				return new StandardHttpClient(path);
			}
		};
	}

	@Override
	public FreehalFile getChild(String path) {
		return new FreehalFile(new StandardHttpClient(this.path + "/" + path));
	}

	@Override
	public FreehalFile getChild(FreehalFileImpl path) {
		return getChild(path.getPath());
	}

	@Override
	public boolean isFile() {
		return path.length() > 0 && path.charAt(path.length() - 1) != '/';
	}

	@Override
	public boolean isDirectory() {
		return path.length() > 0 && path.charAt(path.length() - 1) == '/';
	}

	@Override
	public FreehalFile[] listFiles() {
		return new FreehalFile[0];
	}

	@Override
	public long length() {
		int length = 0;
		for (String line : readLines())
			length += line.length();
		return length;
	}

	@Override
	public boolean mkdirs() {
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	private static boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 "))
				return true;
		}
		return false;
	}

	private InputStream openStream(String link) throws IOException {
		URL url = new URL(link);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		Map<String, List<String>> header = http.getHeaderFields();
		while (isRedirected(header)) {
			link = header.get("Location").get(0);
			url = new URL(link);
			http = (HttpURLConnection) url.openConnection();
			header = http.getHeaderFields();
		}
		InputStream input = http.getInputStream();
		return input;
	}

	@Override
	public Iterable<String> readLines() {
		Iterable<String> iterator = null;
		try {
			String link = protocol + "://" + path;
			LogUtils.i("HTTP GET: " + this + " (via iterator)");
			iterator = new BufferedReaderIterator(new BufferedReader(new InputStreamReader(openStream(link))));

		} catch (Exception e) {
			LogUtils.e(e.getMessage());
			iterator = new NullIterator<String>();
		}
		System.gc();
		return iterator;
	}

	@SuppressWarnings("resource")
	@Override
	public String read() {
		String content = "";
		try {
			URL u = new URL(protocol + "://" + path);
			LogUtils.i("HTTP GET: " + u + " (whole file)");
			content = new Scanner(u.openStream()).useDelimiter("\\Z").next();
			LogUtils.i("HTTP GET: got " + content.length() + " bytes");

		} catch (NoSuchElementException e) {} catch (Exception e) {
			LogUtils.e(e);
			content = "";
		}
		System.gc();
		return content;
	}

	@Override
	public void append(String s) {}

	@Override
	public void write(String s) {}

	@Override
	public int compareTo(FreehalFileImpl o) {
		return path.compareTo(o.getPath());
	}

	@Override
	public File getFile() {
		return new File(path);
	}

	@Override
	public boolean isAbsolute() {
		return true;
	}

	@Override
	public String getAbsolutePath() {
		return path;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getName() {
		String[] parts = path.split("[/]+");
		return parts[parts.length - 1];
	}

	@Override
	public int countLines() {
		int countOfLines = 0;
		Iterable<String> lines = readLines();
		for (@SuppressWarnings("unused")
		String line : lines) {
			++countOfLines;
		}
		return countOfLines;
	}

	@Override
	public void touch() {}

	@Override
	public String toString() {
		return "{" + protocol + "://" + path + "}";
	}
}
