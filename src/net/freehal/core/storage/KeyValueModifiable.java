package net.freehal.core.storage;

public interface KeyValueModifiable<T> {

	KeyValueModifiable<T> set(String key, T value, String dbname);

	KeyValueModifiable<T> remove(String key, String dbname);
}
