package net.freehal.core.logs.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.freehal.core.util.LogUtils;

public class FileLog extends PrintStreamLog {

	public FileLog(File filename) {
		super(new PrintStream(openFile(filename)));
	}

	public FileLog(String filename) {
		super(new PrintStream(openFile(new File(filename))));
	}

	private static OutputStream openFile(File filename) {
		try {
			if (!filename.getParentFile().isDirectory())
				filename.getParentFile().mkdirs();
			return new FileOutputStream(filename);

		} catch (FileNotFoundException ex) {
			LogUtils.e(ex);
			return new FakeOutputStream();
		}
	}

	public static class FakeOutputStream extends OutputStream {
		@Override
		public void write(int arg0) throws IOException {}
	}
}