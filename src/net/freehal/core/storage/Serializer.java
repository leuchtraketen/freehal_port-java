package net.freehal.core.storage;

public interface Serializer<T> {
	String toString(T object);

	T fromString(String serialized);

	public static class StringSerializer implements Serializer<String> {

		@Override
		public String toString(String object) {
			return object;
		}

		@Override
		public String fromString(String serialized) {
			return serialized;
		}

	}
}
