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

import net.freehal.core.util.FreehalFile;
import net.freehal.core.xml.XmlFact;

/**
 * An interface for a class which iterates over given database (XML) files and
 * the facts inside these files and given the facts to implementations of
 * {@link DatabaseComponent}.
 * 
 * @author "Tobias Schulz"
 */
public interface Database {

	public void updateCache();

	public void updateCache(FreehalFile filename);

	public void addComponent(DatabaseComponent index);

	public interface DatabaseComponent {
		void addToCache(XmlFact xfact);

		void startUpdateCache(FreehalFile databaseFile);

		void stopUpdateCache();
	}
}
