package net.freehal.ui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.freehal.core.util.LogUtils;

public class Extensions {
	private static final String EXTENSION_BASE_PACKAGE = "net.freehal.ui.";

	private static final Map<String, Extension> instances = new HashMap<String, Extension>();
	private static final Set<MainLoopListener> loopListeners = new HashSet<MainLoopListener>();

	private static Class<?> getClass(String subpackage) {
		try {
			Class<?> classType = Class.forName(EXTENSION_BASE_PACKAGE + subpackage + ".Loader", false,
					ClassLoader.getSystemClassLoader());
			return classType;
		} catch (Exception ex) {
			return null;
		}
	}

	public static boolean hasExtension(String subpackage) {
		Class<?> extension = getClass(subpackage);
		return extension != null;
	}

	private static Extension initExtension(String subpackage) {
		Class<?> extension = getClass(subpackage);
		if (extension != null) {
			try {
				return (Extension) extension.newInstance();
			} catch (Exception ex) {
				LogUtils.e(ex);
				return null;
			}
		} else {
			return null;
		}
	}

	public static Object getExtension(String subpackage) {
		if (instances.containsKey(subpackage)) {
			return instances.get(subpackage);
		} else {
			Extension extension = initExtension(subpackage);
			instances.put(subpackage, extension);
			return extension;
		}
	}

	public static void registerMainLoop(MainLoopListener extension) {
		loopListeners.add(extension);
	}

	public static void runLoops() {
		List<Thread> threads = new ArrayList<Thread>();
		for (MainLoopListener loop : loopListeners) {
			threads.add(new Thread(new LoopRunnable(loop)));
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (Exception ex) {
				LogUtils.e(ex);
			}
		}
	}

	private static class LoopRunnable implements Runnable {

		private MainLoopListener loop;

		public LoopRunnable(MainLoopListener loop) {
			this.loop = loop;
		}

		@Override
		public void run() {
			loop.loop();
		}

	}
}
