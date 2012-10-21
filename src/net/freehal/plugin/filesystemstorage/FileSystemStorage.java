package net.freehal.plugin.filesystemstorage;

import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.KeyValueModifiable;
import net.freehal.core.storage.KeyValueTransaction;
import net.freehal.core.storage.Serializer;
import net.freehal.core.util.FreehalFile;

public class FileSystemStorage<T> implements KeyValueDatabase<T> {

	@SuppressWarnings("unused")
	private Serializer<T> serializer;

	public FileSystemStorage(FreehalFile path, Serializer<T> serializer) {
		this.serializer = serializer;

		path.mkdirs();
	}

	@Override
	public KeyValueModifiable<T> set(String key, T value, String dbname) {
		return null;
	}

	@Override
	public KeyValueModifiable<T> remove(String key, String dbname) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public boolean contains(String key) {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public boolean contains(String key, String dbname) {
		// TODO Automatisch generierter Methodenstub
		return false;
	}

	@Override
	public T get(String key) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public T get(String key, String dbname) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public KeyValueTransaction<T> transaction() {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public void finish() {
		// TODO Automatisch generierter Methodenstub

	}

	@Override
	public void compress() {
		// TODO Automatisch generierter Methodenstub
		
	}

}
