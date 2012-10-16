package net.freehal.core.storage;

public interface KeyValueReadable<T> {

	boolean contains(String key);

	boolean contains(String key, String dbname);

	T get(String key);

	T get(String key, String dbname);
}
