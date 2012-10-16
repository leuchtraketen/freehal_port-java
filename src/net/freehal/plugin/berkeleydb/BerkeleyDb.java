package net.freehal.plugin.berkeleydb;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import net.freehal.compat.sunjava.StandardFreehalFile;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.KeyValueTransaction;
import net.freehal.core.storage.Serializer;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;

public class BerkeleyDb<T> implements KeyValueDatabase<T> {

	private Map<String, Database> dbs;
	private Environment env;
	private Serializer<T> serializer;

	public BerkeleyDb(FreehalFile path, Serializer<T> serializer) {
		this.dbs = new HashMap<String, Database>();
		this.serializer = serializer;

		if (path instanceof StandardFreehalFile) {
			path.mkdirs();
			final File envDir = path.getFile();
			final EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
			envConfig.setAllowCreate(true);
			this.env = new Environment(envDir, envConfig);
		} else {
			throw new IllegalArgumentException("BerkeleyDb only supports real files "
					+ "(java.io.File wrapped by StandardFreehalFile)!");
		}
	}

	private Database getDatabase(String dbname) {
		if (dbs.containsKey(dbname)) {
			return dbs.get(dbname);
		} else {
			final DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setTransactional(true);
			dbConfig.setAllowCreate(true);
			dbConfig.setSortedDuplicates(true);
			final Database db = env.openDatabase(null, dbname, dbConfig);
			dbs.put(dbname, db);
			return db;
		}
	}

	@Override
	public BerkeleyDb<T> set(String key, T value, String dbname) {
		final KeyValueTransaction<T> txn = transaction();
		txn.set(key, value, dbname);
		txn.finish();
		return this;
	}

	@Override
	public BerkeleyDb<T> remove(String key, String dbname) {
		final KeyValueTransaction<T> txn = transaction();
		txn.remove(key, dbname);
		txn.finish();
		return this;
	}

	@Override
	public boolean contains(String key) {
		final KeyValueTransaction<T> txn = transaction();
		boolean result = txn.contains(key);
		txn.finish();
		return result;
	}

	@Override
	public boolean contains(String key, String dbname) {
		final KeyValueTransaction<T> txn = transaction();
		boolean result = txn.contains(key, dbname);
		txn.finish();
		return result;
	}

	@Override
	public T get(String key) {
		final KeyValueTransaction<T> txn = transaction();
		T result = txn.get(key);
		txn.finish();
		return result;
	}

	@Override
	public T get(String key, String dbname) {
		final KeyValueTransaction<T> txn = transaction();
		T result = txn.get(key, dbname);
		txn.finish();
		return result;
	}

	@Override
	public void finish() {
		env = null;
		serializer = null;
		dbs.clear();
		dbs = null;
	}

	@Override
	public KeyValueTransaction<T> transaction() {
		final Transaction txn = env.beginTransaction(null, null);
		return new KeyValueTransaction<T>() {

			@Override
			public KeyValueTransaction<T> set(String key, T value, String dbname) {
				try {
					final DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
					final DatabaseEntry dataEntry = new DatabaseEntry(serializer.toString(value).getBytes());
					final OperationStatus res = getDatabase(dbname).put(txn, keyEntry, dataEntry);
					if (res != OperationStatus.SUCCESS) {
						LogUtils.e("Error: " + res.toString());
					}
				} catch (DatabaseException ex) {
					LogUtils.e("Caught exception: " + ex.toString());
				}
				return this;
			}

			@Override
			public KeyValueTransaction<T> remove(String key, String dbname) {
				try {
					if (key != null) {
						// remove a single key
						final DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
						getDatabase(dbname).delete(txn, keyEntry);
					} else {
						// remove the whole database
						env.removeDatabase(txn, dbname);
					}
				} catch (DatabaseException ex) {
					LogUtils.e("Caught exception: " + ex.toString());
				}
				return this;
			}

			@Override
			public boolean contains(String key) {
				return get(key) != null;
			}

			@Override
			public boolean contains(String key, String dbname) {
				return get(key, dbname) != null;
			}

			@Override
			public T get(String key) {
				final DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
				final DatabaseEntry dataEntry = new DatabaseEntry();
				String error = null;
				for (Database db : dbs.values()) {
					try {
						final OperationStatus res = db.get(txn, keyEntry, dataEntry, null);
						if (res != OperationStatus.SUCCESS) {
							error = "Error: " + res.toString();
						} else {
							return serializer.fromString(new String(dataEntry.getData()));
						}
					} catch (DatabaseException ex) {
						LogUtils.e("Caught exception: " + ex.toString());
					}
				}
				if (error != null) {
					LogUtils.e(error);
				}
				return null;
			}

			@Override
			public T get(String key, String dbname) {
				final DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes());
				final DatabaseEntry dataEntry = new DatabaseEntry();
				T result = null;
				try {
					final OperationStatus res = getDatabase(dbname).get(txn, keyEntry, dataEntry, null);
					if (res != OperationStatus.SUCCESS) {
						LogUtils.e("Error: " + res.toString());
					} else {
						result = serializer.fromString(new String(dataEntry.getData()));
					}
				} catch (DatabaseException ex) {
					LogUtils.e("Caught exception: " + ex.toString());
				}
				return result;
			}

			@Override
			public BerkeleyDb<T> finish() {
				txn.commit();
				return BerkeleyDb.this;
			}
		};
	}
}
