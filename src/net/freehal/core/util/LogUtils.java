package net.freehal.core.util;

public class LogUtils {

	private static LogUtilsImpl instance = null;

	public static void set(LogUtilsImpl instance) {
		LogUtils.instance = instance;
	}

	public static void e(final String s) {
		instance.e(s);
	}

	public static void w(final String s) {
		instance.w(s);
	}

	public static void i(final String s) {
		instance.i(s);
	}

	public static void d(final String s) {
		instance.d(s);
	}
}
