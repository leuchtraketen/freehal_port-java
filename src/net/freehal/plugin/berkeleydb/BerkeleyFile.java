package net.freehal.plugin.berkeleydb;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import net.freehal.core.storage.Serializer;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.RegexUtils;

public class BerkeleyFile implements FreehalFile {

	private static final String DBNAME = "filesystem";
	private static BerkeleyDb<String> db = null;

	private final String path;
	private final String name;

	private BerkeleyFile(String path) {
		this.path = path;
		String[] parts = path.split("/");
		this.name = parts[parts.length - 1];

		if (db == null) {
			db = new BerkeleyDb<String>(Storages.getPath().getChild(DBNAME),
					new Serializer.StringSerializer());
		}
	}

	public static Factory<FreehalFile, String> newFactory() {
		return new Factory<FreehalFile, String>() {
			@Override
			public FreehalFile newInstance(String b) {
				return new BerkeleyFile(b);
			}
		};
	}

	@Override
	public FreehalFile getChild(String name) {
		String child = path;
		if (!path.endsWith("/"))
			child += "/";
		child += name;
		child = RegexUtils.replace(child, "[/]+", "/");
		return new BerkeleyFile(child);
	}

	@Override
	public FreehalFile getChild(FreehalFile file) {
		return getChild(file.getPath());
	}

	@Override
	public boolean delete() {
		db.remove(this.getAbsolutePath(), DBNAME);
		return true;
	}

	@Override
	public boolean isDirectory() {
		return path.endsWith("/");
	}

	@Override
	public boolean isFile() {
		return !isDirectory();
	}

	@Override
	public long length() {
		long length = 0;
		for (String line : readLines())
			length += line.length();
		return length;
	}

	@Override
	public FreehalFile[] listFiles() {
		Database berkeley = db.transaction().getDatabase(DBNAME);
		DatabaseEntry keyEntry = new DatabaseEntry();
		DatabaseEntry dataEntry = new DatabaseEntry();
		final String path1 = this.getPath();
		final String path2 = this.getAbsolutePath();
		Cursor cursor = berkeley.openCursor(null, null);
		Collection<FreehalFile> files = new ArrayList<FreehalFile>();
		while (cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			final String key = new String(keyEntry.getData());
			if (key.startsWith(path1) || key.startsWith(path2)) {
				files.add(new BerkeleyFile(key));
			}
		}
		return files.toArray(new FreehalFile[files.size()]);
	}

	@Override
	public boolean mkdirs() {
		// ignore on BerkeleyDB...
		return true;
	}

	@Override
	public String toString() {
		return "{berkeley://" + super.toString() + "}";
	}

	@Override
	public void append(String content) {}

	@Override
	public String read() {
		return null;
	}

	@Override
	public Iterable<String> readLines() {
		return Arrays.asList(read().split("[\r\n]+"));
	}

	@Override
	public void write(final String content) {}

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
	public int compareTo(FreehalFile o) {
		if (o instanceof BerkeleyFile) {
			return path.compareTo(((BerkeleyFile) o).path);
		} else {
			return 0;
		}
	}

	@Override
	public File getFile() {
		return new File(path);
	}

	@Override
	public boolean isAbsolute() {
		return false;
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
		return name;
	}
}
