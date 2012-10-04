package net.freehal.core.lang;

public class Languages {

	private static Language language = new IllegalLanguage();

	public static Language getLanguage() {
		return language;
	}

	public static void setLanguage(Language language) {
		Languages.language = language;
	}

	private static class IllegalLanguage implements Language {
		@Override
		public String getCode() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isCode(String otherCode) {
			throw new UnsupportedOperationException();
		}
	}
}
