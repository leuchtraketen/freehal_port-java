/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.freehal.core.storage.Storages;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.XmlFact;
import net.freehal.core.xml.XmlFactReciever;
import net.freehal.core.xml.XmlUtils;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

public class StandardDatabase implements Database {

	private Map<String, Integer> cacheFiles;
	private List<DatabaseComponent> components;

	public StandardDatabase() {
		cacheFiles = new HashMap<String, Integer>();
		components = new ArrayList<DatabaseComponent>();
		readMetadata();
	}

	@Override
	public void addComponent(DatabaseComponent component) {
		components.add(component);
	}

	private void readMetadata() {
		Iterable<String> lines = DirectoryUtils.getCacheDirectory("database", "meta").getChild("files.csv")
				.readLines();

		for (String line : lines) {
			String[] csv = line.split(":");
			if (csv.length == 2) {
				try {
					cacheFiles.put(csv[0], Integer.valueOf(csv[1]));
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
	}

	private void writeMetadata() {
		StringBuilder sb = new StringBuilder();
		for (String filename : cacheFiles.keySet()) {
			sb.append(filename).append(":").append(cacheFiles.get(filename)).append("\n");
		}
		DirectoryUtils.getCacheDirectory("database", "meta").getChild("files.csv").write(sb.toString());
	}

	@Override
	public void updateCache() {
		updateCache(FreehalFiles.getFile(""));
	}

	@Override
	public void updateCache(FreehalFile databaseFile) {
		if (!databaseFile.isAbsolute()) {
			databaseFile = Storages.inLanguageDirectory(databaseFile);
		}

		if (databaseFile.isDirectory()) {
			LogUtils.i("update cache (directory): " + databaseFile);

			FreehalFile[] files = databaseFile.listFiles();
			for (FreehalFile file : files) {
				LogUtils.i("file:" + file);
				if (file.isFile() && file.getName().contains(".xml")) {
					this.updateCache(file);
				}
			}
		}

		else if (databaseFile.isFile()) {
			if (cacheFiles.containsKey(databaseFile.getName())
					&& cacheFiles.get(databaseFile.getName()) == (int) databaseFile.length()) {
				LogUtils.i("cache is up to date (file): " + databaseFile);

			} else {
				LogUtils.i("update cache (file): " + databaseFile);

				final Iterable<String> xmlInput = databaseFile.readLines();

				// order the xml data
				final XmlStreamIterator xmlPre = new XmlUtils.XmlStreamIterator(xmlInput);

				// a separate scope for garbage collector!
				{
					// we use the component interface for updating the caches
					for (DatabaseComponent updater : components) {
						updater.startUpdateCache();
					}

					// don't print all these checks whether a fact is a synonym
					// or
					// not...
					LogUtils.addTemporaryFilter("xml", "debug");

					// read the xml data and build XmlFact objects
					XmlUtils.readXmlFacts(xmlPre, databaseFile, new XmlFactReciever() {
						@Override
						public void useXmlFact(XmlFact xfact, int countFacts, long start,
								FreehalFile filename, int countFactsSoFar) {

							// update the caches
							for (DatabaseComponent updater : components) {
								updater.addToCache(xfact);
							}
						}
					});

					// reset the temporary log filter from above!
					LogUtils.resetTemporaryFilters();

					// update the caches
					for (DatabaseComponent updater : components) {
						updater.stopUpdateCache();
					}
				}
				System.gc();

				LogUtils.i("updated cache (file): " + databaseFile);

				// ... and mark this database file as done!
				cacheFiles.put(databaseFile.getName(), (int) databaseFile.length());
				writeMetadata();
			}
		}
	}
}
