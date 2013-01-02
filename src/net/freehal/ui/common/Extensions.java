package net.freehal.ui.common;

import java.util.HashMap;
import java.util.Map;

import net.freehal.core.util.LogUtils;

public class Extensions {
	private static final String EXTENSION_BASE_PACKAGE = "net.freehal.ui.";

	private static final Map<String, Object> instances = new HashMap<String, Object>();

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

	private static Object initExtension(String subpackage) {
		Class<?> extension = getClass(subpackage);
		if (extension != null) {
			try {
				return extension.newInstance();
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
		}
		else {
			Object extension = initExtension(subpackage);
			instances.put(subpackage, extension);
			return extension;
		}
	}
}
