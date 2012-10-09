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

/**
 * A fake implementation of a {@link Database} for API tests.
 * 
 * @author "Tobias Schulz"
 */
public class FakeDatabase implements Database {

	/**
	 * a fake database doesn't need a cache...
	 */
	@Override
	public void updateCache() {}

	/**
	 * a fake database doesn't need a cache...
	 */
	@Override
	public void updateCache(FreehalFile filename) {}

	/**
	 * a fake database doesn't need components...
	 */
	@Override
	public void addComponent(DatabaseComponent index) {

	}

}
