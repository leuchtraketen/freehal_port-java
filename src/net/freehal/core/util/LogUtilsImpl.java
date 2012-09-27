package net.freehal.core.util;

public interface LogUtilsImpl {

	public void e(String e);

	public void w(String e);

	public void i(String e);

	public void d(String e);

	public void flush();

	public LogUtilsImpl addFilter(String className, String type);

	public LogUtilsImpl addTemporaryFilter(String className, String type);

	public LogUtilsImpl resetTemporaryFilters();

}
