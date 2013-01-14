package net.freehal.plugin.berkeleydb;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.freehal.compat.sunjava.StandardFreehalFile;
import net.freehal.core.storage.KeyValueDatabase;
import net.freehal.core.storage.KeyValueTransaction;
import net.freehal.core.storage.Serializer;
import net.freehal.core.util.ExitListener;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.SystemUtils;
import net.freehal.core.util.StringUtils;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

public class BerkeleyDb<T> implements KeyValueDatabase<T>, ExitListener {

	private Environment env;
	private Serializer<T> serializer;
	private Set<String> dbnames;
	private FreehalFile path;

	public BerkeleyDb(FreehalFile path, Serializer<T> serializer) {
		SystemUtils.destructOnExit(this);

		this.path = path;
		this.serializer = serializer;

		if (path.getImpl() instanceof StandardFreehalFile) {
			path.mkdirs();
			final File envDir = path.getFile();
			final EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
			envConfig.setAllowCreate(true);
			envConfig.setConfigParam(EnvironmentConfig.CLEANER_MIN_UTILIZATION, "75");
			new File(envDir, "je.lck").delete();
			this.env = new Environment(envDir, envConfig);
			/*
			 * try { LogUtils.updateProgress("compressing database...");
			 * env.cleanLog(); } catch (DatabaseException dbe) {
			 * LogUtils.e(dbe); }
			 */

			dbnames = new HashSet<String>();
			for (String dbname : path.getChild("dbnames").readLines()) {
				dbnames.add(dbname);
			}

		} else {
			LogUtils.e(new IllegalArgumentException("BerkeleyDb only supports real files "
					+ "(that means an instance of java.io.File wrapped by "
					+ StandardFreehalFile.class.getName() + ")! " + "You have given me an instance of "
					+ path.getImpl().getClass()));
			SystemUtils.exit(1);
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

	private void writeDatabaseNames() {
		if (dbnames != null && dbnames.size() > 0)
			path.getChild("dbnames").write(StringUtils.join("\n", dbnames));
	}

	@Override
	public void compress() {
		writeDatabaseNames();
		try {
			LogUtils.updateProgress("compressing database: " + path.getName());
			env.cleanLog();
		} catch (DatabaseException dbe) {
			LogUtils.e(dbe);
		}
	}

	@Override
	public void finish() {
		writeDatabaseNames();
		try {
			env.close();
		} catch (DatabaseException dbe) {
			LogUtils.e(dbe);
		}
		env = null;
		serializer = null;
		dbnames = null;
		path = null;
	}

	@Override
	public BerkeleyTransaction transaction() {
		writeDatabaseNames();
		return new BerkeleyTransaction().init();
	}

	public class BerkeleyTransaction implements KeyValueTransaction<T> {

		private Transaction txn;

		public BerkeleyTransaction init() {
			txn = env.beginTransaction(null, null);
			txn.setLockTimeout(5000, TimeUnit.MILLISECONDS);
			dbs = new HashMap<String, Database>();
			return this;
		}

		private Map<String, Database> dbs;

		public Database getDatabase(String dbname) {
			if (dbs.containsKey(dbname) && dbs.get(dbname) != null) {
				return dbs.get(dbname);
			} else {
				final DatabaseConfig dbConfig = new DatabaseConfig();
				dbConfig.setTransactional(true);
				dbConfig.setAllowCreate(true);
				dbConfig.setSortedDuplicates(true);
				final Database db = env.openDatabase(txn, dbname, dbConfig);
				dbnames.add(dbname);
				dbs.put(dbname, db);
				return db;
			}
		}

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
					LogUtils.d("delete: " + dbname);
					// close all databases
					for (Database db : dbs.values())
						db.close();
					dbs.clear();
					// commit all changes
					txn.commit();
					txn = env.beginTransaction(null, null);
					// remove the whole database
					env.truncateDatabase(null, dbname, false);
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
			StringBuilder data = new StringBuilder();
			String error = null;
			for (String dbname : env.getDatabaseNames()) {
				Database db = getDatabase(dbname);
				// LogUtils.i("test: dbname=" + dbname);

				try {
					final DatabaseEntry dataEntry = new DatabaseEntry();
					final OperationStatus res = db.get(txn, keyEntry, dataEntry, null);
					if (res != OperationStatus.SUCCESS) {
						error = res.toString();
					} else {
						data.append(new String(dataEntry.getData()));
						// System.out.println(new String(dataEntry.getData()));
					}
				} catch (DatabaseException ex) {
					LogUtils.e("Caught exception: " + ex.toString());
				}

				db.close();
				dbs.remove(dbname);
			}
			if (error != null && !error.equals("OperationStatus.NOTFOUND"))
				LogUtils.e("Error: " + error);
			return serializer.fromString(data.toString());
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
			for (Database db : dbs.values())
				db.close();
			dbs.clear();
			dbs = null;
			txn = null;
			return BerkeleyDb.this;
		}
	}

	@Override
	public void onExit(int status) {
		finish();
	}
}
