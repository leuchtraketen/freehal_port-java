package net.freehal.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SystemUtils {

	private static List<ExitListener> exitListeners;
	private static String USERNAME;
	private static String EMAIL_ADDR;
	private static String HOSTNAME;

	public static enum OS {
		LINUX, UNIX, MACOSX, WINDOWS, UNKNOWN;
	}

	static {
		exitListeners = new ArrayList<ExitListener>();

		try {
			HOSTNAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			HOSTNAME = "localhost";
		}

		USERNAME = System.getProperty("user.name");
		EMAIL_ADDR = USERNAME + "@" + HOSTNAME;
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

	public static String getLocalHostname() {
		return HOSTNAME;
	}

	public static String getUsername() {
		return USERNAME;
	}

	public static void setUsername(String username) {
		USERNAME = username;
	}

	public static String getEmailAddr() {
		return EMAIL_ADDR;
	}

	public static void setEmailAddr(String emailAddr) {
		EMAIL_ADDR = emailAddr;
	}

	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
