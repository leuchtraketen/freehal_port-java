package net.freehal.core.storage;

public interface KeyValueTransaction<T> extends KeyValueModifiable<T>, KeyValueReadable<T> {

	KeyValueDatabase<T> finish();
}
