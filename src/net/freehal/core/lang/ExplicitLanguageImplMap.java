package net.freehal.core.lang;

import java.util.HashMap;
import java.util.Map;

public class ExplicitLanguageImplMap<A> extends LanguageImplMap<A> {

	private Map<Language, A> impls = new HashMap<Language, A>();
	private A defaultImpl = null;

	public ExplicitLanguageImplMap(A defaultImpl) {
		this.defaultImpl = defaultImpl;
	}

	public ExplicitLanguageImplMap() {
		this.defaultImpl = null;
	}

	public void set(Language language, A implementation) {
		if (language != null && implementation != null) {
			impls.put(language, implementation);
		}
	}

	public A get(Language language) {
		if (language != null && impls.containsKey(language)) {
			return impls.get(language);
		} else {
			return defaultImpl;
		}
	}

	public boolean has(Language language) {
		if (language != null) {
			return impls.containsKey(language);
		} else {
			return true;
		}
	}

}
