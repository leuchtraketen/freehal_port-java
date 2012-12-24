package net.freehal.core.lang;

import java.util.Set;

import net.freehal.core.util.LogUtils;
import net.freehal.core.util.MultiHashMap;
import net.freehal.core.util.MultiMap;
import net.freehal.core.util.StringUtils;

public class LanguageSpecific {

	private static MultiMap<String, Class<?>> defaults = new MultiHashMap<String, Class<?>>();

	public static <T> void add(Class<? extends Language> languageType, Class<? extends T> implType) {

		final String languageClassName;
		if (languageType != null)
			languageClassName = StringUtils.substringAfterLast(languageType.getName(), ".");
		else
			languageClassName = "null";

		defaults.put(languageClassName, implType);
	}

	@SuppressWarnings("unchecked")
	private static <T> T chooseByInterface(final String lang, Class<T> interfaceType) {

		Set<Class<?>> pool = defaults.get(lang);
		LogUtils.i(lang + ": looking for default implementations of " + interfaceType.getName());

		for (Class<?> type : pool) {
			if (type.isAssignableFrom(interfaceType) || interfaceType.isAssignableFrom(type)) {
				LogUtils.i(lang + ": found default implementation: " + type.getName());
				return (T) newInstanceOf(type, null);
			} else {
				LogUtils.i(lang + ": no default implementation: " + type.getName());
			}
		}

		return null;
	}

	public static <T> T chooseByLanguage(Class<T> interfaceType) {
		final String lang = StringUtils.substringAfterLast(Languages.getLanguage().getClass().getName(), ".");
		T obj = chooseByInterface(lang, interfaceType);
		if (obj == null) {
			obj = chooseByInterface("null", interfaceType);
		}
		if (obj == null) {
			LogUtils.e(lang + ": no implementation found. " + "Classes associated with the language: "
					+ defaults.get(lang));

		}
		return obj;
	}

	private static Object newInstanceOf(Class<?> classType, Class<?> alternative) {
		try {
			return classType.newInstance();
		} catch (Exception e) {
			LogUtils.e(e);
			if (alternative != null)
				return newInstanceOf(alternative, null);
			else
				return null;
		}
	}

}
