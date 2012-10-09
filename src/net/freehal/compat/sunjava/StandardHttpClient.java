package net.freehal.compat.sunjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import net.freehal.compat.sunjava.StandardFreehalFile.BufferedReaderIterator;
import net.freehal.compat.sunjava.StandardFreehalFile.NullIterator;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

public class StandardHttpClient implements FreehalFile {

	protected String protocol = "http";
	private String path;

	private StandardHttpClient(String path) {
		path = StringUtils.replace(path, "http://", "");
		path = RegexUtils.replace(path, "[/]+", "/");
		this.path = path;
	}

	public static Factory<FreehalFile, String> newFactory() {
		return new Factory<FreehalFile, String>() {
			@Override
			public FreehalFile newInstance(String b) {
				return new StandardHttpClient(b);
			}
		};
	}

	@Override
	public FreehalFile getChild(String path) {
		return new StandardHttpClient(this.path + "/" + path);
	}

	@Override
	public FreehalFile getChild(FreehalFile path) {
		return new StandardHttpClient(this.path + "/" + path.getPath());
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

	@Override
	public Iterable<String> readLines() {
		Iterable<String> iterator = null;
		try {
			URL u = new URL(protocol + "://" + path);
			LogUtils.i("HTTP GET: " + u + " (via iterator)");
			iterator = new BufferedReaderIterator(new BufferedReader(new InputStreamReader(u.openStream())));

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

		} catch (Exception e) {
			LogUtils.e(e.getMessage());
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
	public int compareTo(FreehalFile o) {
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

}
