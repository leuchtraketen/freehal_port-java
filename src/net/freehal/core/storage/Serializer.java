package net.freehal.core.storage;

import net.freehal.core.xml.XmlUtils;

public interface Serializer<T> {
	String toString(T object);

	Iterable<String> toStringIterator(T object);

	T fromString(String serialized);

	T fromStringIterator(Iterable<String> serialized);

	public static class StringSerializer implements Serializer<String> {

		@Override
		public String toString(String object) {
			return object;
		}

		@Override
		public String fromString(String serialized) {
			return serialized;
		}

		@Override
		public Iterable<String> toStringIterator(String object) {
			return new XmlUtils.OneStringIterator(object);
		}

		@Override
		public String fromStringIterator(Iterable<String> serialized) {
			StringBuilder sb = new StringBuilder();
			for (String s : serialized) {
				sb.append(s);
			}
			return sb.toString();
		}

	}
}
