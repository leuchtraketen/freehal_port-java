package net.freehal.core.util;

import java.io.File;
import java.util.List;

public interface FileUtilsImpl {

	public List<String> readLines(File f);

	public String read(File f);

	public void append(File f, String s);

	public void write(File f, String s);

	public void delete(File directory);

}
