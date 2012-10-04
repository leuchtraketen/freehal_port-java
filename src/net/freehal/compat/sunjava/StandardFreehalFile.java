package net.freehal.compat.sunjava;

import java.io.File;
import java.util.ArrayList;

import net.freehal.core.util.AbstractFreehalFile;
import net.freehal.core.util.FileUtilsImpl;
import net.freehal.core.util.FreehalFile;

public class StandardFreehalFile extends AbstractFreehalFile {

	private static final FileUtilsImpl utils = new StandardFileUtils();

	public StandardFreehalFile(File file) {
		super(file);
	}

	@Override
	public FreehalFile create(String path) {
		return new StandardFreehalFile(new File(path));
	}

	@Override
	public FreehalFile create(String dir, String file) {
		return new StandardFreehalFile(new File(dir, file));
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public FreehalFile[] listFiles() {
		ArrayList<FreehalFile> files = new ArrayList<FreehalFile>();
		File[] realFiles = file.listFiles();
		for (File realFile : realFiles) {
			files.add(this.create(realFile.getPath()));
		}
		return files.toArray(new FreehalFile[realFiles.length]);
	}

	@Override
	public long length() {
		return file.length();
	}

	@Override
	public boolean mkdirs() {
		return file.mkdirs();
	}

	@Override
	public boolean delete() {
		return file.delete();
	}

	@Override
	public FileUtilsImpl getFileUtilsImpl() {
		return utils;
	}

	@Override
	public String toString() {
		return "{" + super.toString() + "}";
	}
}
