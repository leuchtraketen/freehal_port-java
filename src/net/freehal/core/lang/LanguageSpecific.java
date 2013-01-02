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
	private static <T> T chooseByInterface(String lang, Class<T> interfaceType) {

		Set<Class<?>> pool = defaults.get(lang);

		for (Class<?> type : pool) {
			if (type.isAssignableFrom(interfaceType) || interfaceType.isAssignableFrom(type)) {
				LogUtils.i(interfaceType.getName() + " is provided by " + type.getName() + " for language ["
						+ lang + "]");
				return (T) newInstanceOf(type, null);
			}
		}

		LogUtils.e("Error! no implementation of interface " + interfaceType.getName()
				+ " found for language [" + lang + "]");
		LogUtils.e("Classes associated with that language:");
		for (Class<?> type : pool) {
			LogUtils.e(" - " + type.getName());
		}

		return null;
	}

	public static <T> T chooseByCurrentLanguage(Class<T> interfaceType) {
		return chooseByLanguage(Languages.getCurrentLanguage(), interfaceType);
	}

	public static <T> T chooseByLanguage(Language language, Class<T> interfaceType) {
		final String lang = StringUtils.substringAfterLast(language.getClass().getName(), ".");
		T obj = chooseByInterface(lang, interfaceType);
		if (obj == null) {
			obj = chooseByInterface("null", interfaceType);
		}
		if (obj == null) {
			LogUtils.e("No implementation found. Returning null.");
			LogUtils.e("This could have unintentional side effects.");
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
