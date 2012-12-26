package net.freehal.ui.shell;

import java.io.File;
import java.util.Collections;

import net.freehal.core.util.AbstractFreehalFile;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;

class FakeFreehalFile extends AbstractFreehalFile {

	private FakeFreehalFile(File file) {
		super(file);
	}

	public static Factory<FreehalFile, String> newFactory() {
		return new Factory<FreehalFile, String>() {
			@Override
			public FreehalFile newInstance(String b) {
				return new FakeFreehalFile(new File(b));
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
		return this;
	}

	@Override
	public FreehalFile getChild(FreehalFile path) {
		return this;
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