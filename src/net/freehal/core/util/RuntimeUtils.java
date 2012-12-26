package net.freehal.core.util;

import java.util.ArrayList;
import java.util.List;

public class RuntimeUtils {

	private static List<ExitListener> exitListeners;

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
}
