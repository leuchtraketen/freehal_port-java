package net.freehal.core.util;

public class LogUtils {

	private static LogUtilsImpl instance = null;

	public static void set(LogUtilsImpl instance) {
		LogUtils.instance = instance;
	}

	public static void e(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.e(line);
			}
		} else {
			instance.e(s);
		}
	}

	public static void w(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.w(line);
			}
		} else {
			instance.w(s);
		}
	}

	public static void i(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.i(line);
			}
		} else {
			instance.i(s);
		}
	}

	public static void d(final Object obj) {
		final String s = obj.toString();
		if (s.contains("\n")) {
			for (final String line : s.split("[\r\n]+")) {
				instance.d(line);
			}
		} else {
			instance.d(s);
		}
	}

	public static void flush() {
		instance.flush();
	}
}
