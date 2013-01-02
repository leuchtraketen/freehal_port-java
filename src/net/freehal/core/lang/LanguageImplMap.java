package net.freehal.core.lang;

public abstract class LanguageImplMap<A> {

	public abstract void set(Language language, A implementation);

	public void setCurrent(A implementation) {
		set(Languages.getCurrentLanguage(), implementation);
	}

	public abstract A get(Language language);

	public A getCurrent() {
		return get(Languages.getCurrentLanguage());
	}

	public abstract boolean has(Language language);

	public boolean hasCurrent() {
		return has(Languages.getCurrentLanguage());
	}

}
