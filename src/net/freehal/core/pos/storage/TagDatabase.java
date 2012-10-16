package net.freehal.core.pos.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.freehal.core.pos.Tags;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.KeyValueTransaction;
import net.freehal.core.util.Factory;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;

public class TagDatabase extends MemoryTagContainer implements TagContainer {

	public static Factory<TagContainer, String> newFactory(final KeyValueDatabase<Tags> database,
			final KeyValueDatabase<String> meta) {
		return new Factory<TagContainer, String>() {
			@Override
			public TagContainer newInstance(String dbname) {
				return new TagDatabase(database, meta);
			}
		};
	}

	private KeyValueDatabase<Tags> database = null;
	private KeyValueTransaction<Tags> transaction = null;
	private KeyValueDatabase<String> meta = null;

	public TagDatabase(KeyValueDatabase<Tags> database, KeyValueDatabase<String> meta) {
		this.database = database;
		this.meta = meta;
	}

	@Override
	public Iterator<Entry<String, Tags>> iterator() {
		return new HashMap<String, Tags>().entrySet().iterator();
	}

	@Override
	public void add(String word, Tags tags) {
		database.set(word, tags, "unknown.pos");
	}

	@Override
	protected void add(String word, Tags tags, FreehalFile from) {
		transaction.set(word, tags, from.getName());
	}

	@Override
	public boolean containsKey(String word) {
		return database.contains(word);
	}

	@Override
	public Tags get(String word) {
		return database.get(word);
	}

	@Override
	public boolean add(FreehalFile filename) {
		final String savedSize = meta.get(filename.getName(), "files");
		final String size = filename.length() + "";

		// if the file has changed
		if (savedSize == null || !savedSize.equals(size)) {
			// create a new transaction
			transaction = database.transaction();

			// delete old cache files
			transaction.remove(KeyValueDatabase.EVERYTHING, filename.getName());

			// temporarily filter annoying log messages
			LogUtils.addTemporaryFilter("StandardTagger", "i");

			// read the file (calls #add(String, Tags, FreehalFile))
			boolean result = super.add(filename);

			// reset temporary log filters
			LogUtils.resetTemporaryFilters();

			// save curretn state of the file
			meta.set(filename.getName(), size, "files");

			// close the transaction
			transaction.finish();
			transaction = null;

			return result;

		} else
			return false;
	}
}
