package net.freehal.core.lang.fake;

import java.io.File;
import java.util.Collections;

import net.freehal.core.util.AbstractFreehalFile;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFileImpl;
import net.freehal.core.util.FreehalFiles;

public class FakeFreehalFile extends AbstractFreehalFile {

	private FakeFreehalFile(File file) {
		super(file);
	}

	public static FreehalFiles.Factory newFactory() {
		return new net.freehal.core.util.FreehalFiles.Factory() {
			@Override
			public FreehalFileImpl newInstance(String path) {
				return new FakeFreehalFile(new File(path));
			}
		};
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public FreehalFile[] listFiles() {
		return new FreehalFile[0];
	}

	@Override
	public long length() {
		return 0;
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
	public FreehalFile getChild(String path) {
		return new FreehalFile(this);
	}

	@Override
	public FreehalFile getChild(FreehalFileImpl path) {
		return new FreehalFile(this);
	}

	@Override
	public Iterable<String> readLines() {
		return Collections.emptyList();
	}

	@Override
	public String read() {
		return "";
	}

	@Override
	public void append(String s) {}

	@Override
	public void write(String s) {}

	@Override
	public int countLines() {
		return 0;
	}

	@Override
	public void touch() {
		append("");
	}
}