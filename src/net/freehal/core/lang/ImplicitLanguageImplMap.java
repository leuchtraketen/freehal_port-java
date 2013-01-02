package net.freehal.core.lang;

import java.util.HashMap;
import java.util.Map;

import net.freehal.core.util.Factory;

public class ImplicitLanguageImplMap<A> extends LanguageImplMap<A> {

	private Map<Language, A> impls = new HashMap<Language, A>();
	private Factory<A> factory = null;

	public ImplicitLanguageImplMap(Factory<A> factory) {
		this.factory = factory;
	}

	@Override
	public void set(Language language, A implementation) {
		if (language != null && implementation != null) {
			impls.put(language, implementation);
		}
	}

	@Override
	public A get(Language language) {
		if (language != null) {
			if (!impls.containsKey(language)) {
				setToDefault(language);
			}
			return impls.get(language);
		} else {
			return null;
		}
	}

	@Override
	public boolean has(Language language) {
		if (language != null && !impls.containsKey(language)) {
			setToDefault(language);
		}
		return true;
	}

	private void setToDefault(Language language) {
		set(language, factory.newInstance());
	}

}
