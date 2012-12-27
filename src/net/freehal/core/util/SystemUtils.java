package net.freehal.core.util;

import java.util.ArrayList;
import java.util.List;

public class SystemUtils {

	private static List<ExitListener> exitListeners;

	public static enum OS {
		LINUX, UNIX, MACOSX, WINDOWS, UNKNOWN;
	}

	static {
		exitListeners = new ArrayList<ExitListener>();
	}

	public static void exit(int status) {
		for (ExitListener e : exitListeners) {
			e.onExit(status);
		}
		System.exit(status);
	}

	public static void destructOnExit(ExitListener exitListener) {
		exitListeners.add(exitListener);
	}

	public static OS getOperatingSystem() {
		if (isLinux())
			return OS.LINUX;
		else if (isUnix())
			return OS.UNIX;
		else if (isMacOSX())
			return OS.MACOSX;
		else if (isWindows())
			return OS.WINDOWS;
		else
			return OS.UNKNOWN;
	}

	public static boolean isWindows() {
		return org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;
	}

	public static boolean isMacOSX() {
		return org.apache.commons.lang3.SystemUtils.IS_OS_MAC_OSX;
	}

	public static boolean isUnix() {
		return org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
	}

	public static boolean isLinux() {
		return org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
	}
}
