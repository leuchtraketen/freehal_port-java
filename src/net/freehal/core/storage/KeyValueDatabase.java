package net.freehal.core.storage;

public interface KeyValueDatabase<T> extends KeyValueModifiable<T>, KeyValueReadable<T> {

	public static final String EVERYTHING = null;

	KeyValueTransaction<T> transaction();

	void finish();
}
