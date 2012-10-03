package net.freehal.core.util;

import java.io.File;

public abstract class AbstractFreehalFile implements FreehalFile {

	/**
	 * The corresponding standard Java file object. It is at least used for the
	 * name/path management.
	 */
	protected File file;

	@SuppressWarnings("unused")
	private AbstractFreehalFile() {
		this.file = null;
	}

	public AbstractFreehalFile(File file) {
		this.file = file;
	}

	public FreehalFile getAbsoluteFile() {
		return create(file.getAbsolutePath());
	}

	@Override
	public String toString() {
		return file.toString();
	}

	@Override
	public int compareTo(FreehalFile o) {
		return file.getAbsolutePath().compareTo(o.getAbsolutePath());
	}

	@Override
	public int hashCode() {
		return getAbsolutePath().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FreehalFile)
			return getAbsolutePath().equals(((FreehalFile) o).getAbsolutePath());
		else
			return false;
	}

	@Override
	public abstract FreehalFile create(String path);

	@Override
	public abstract FreehalFile create(String dir, String file);

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean isAbsolute() {
		return file.isAbsolute();
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public String getPath() {
		return file.getPath();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public abstract boolean isFile();

	@Override
	public abstract boolean isDirectory();

	@Override
	public abstract FreehalFile[] listFiles();

	@Override
	public abstract long length();

	@Override
	public abstract boolean mkdirs();

	@Override
	public abstract boolean delete();

	@Override
	public abstract FileUtilsImpl getFileUtilsImpl();
}