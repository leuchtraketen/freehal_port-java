package net.freehal.core.util;

import java.io.File;

public interface FreehalFile extends Comparable<FreehalFile> {

	public FreehalFile create(String path);

	public FreehalFile create(String dir, String file);

	public File getFile();

	public boolean isAbsolute();

	public String getAbsolutePath();

	public String getPath();

	public String getName();

	public boolean isFile();

	public boolean isDirectory();

	public FreehalFile[] listFiles();

	public long length();

	public boolean mkdirs();

	public boolean delete();

	public String toString();

	public FileUtilsImpl getFileUtilsImpl();
}
