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
package net.freehal.core.grammar;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of {@link Entity} objects.
 * 
 * @author "Tobias Schulz"
 */
public class Entities extends ArrayList<Entity> {

	private static final long serialVersionUID = 1658375404776969761L;

	/**
	 * Construct an empty list of entities.
	 */
	public Entities() {
		super();
	}

	/**
	 * Construct an list of entities by copying the given list.
	 * 
	 * @param entities
	 *        the list to copy
	 */
	public Entities(List<Entity> entities) {
		super(entities);
	}

	/**
	 * Get the marker keys from the given {@link Entity} in the second argument with
	 * {@link Entity#getMarker()} and use
	 * {@link StandardGrammar#modifySymbol(Entity, List)} to add these keys to
	 * each {@link Entity} in the entity list in the first argument; after that, add
	 * these entities to this instance.
	 * 
	 * @param entities
	 *        the entities to add to this list
	 * @param previous
	 *        the entity to be replaced by the ones in {@code entities}
	 * @param grammar
	 *        the grammar to use
	 */
	public void add(Entities entities, Entity previous, StandardGrammar grammar) {

		// / cout << "get_virt: size=" << previous->get_virt().size() << endl;
		if (previous.getMarker().size() > 0) {
			for (Entity it : entities) {
				Entity e = grammar.modifySymbol(it, previous.getMarker());
				this.add(e);
			}
		} else {
			for (Entity it : entities) {
				this.add(it);
			}
		}
	}

	/**
	 * Returns a string representation of this list of {@link Entity} objects
	 * for log or console output.
	 * 
	 * @return a string
	 */
	public String toLogOutput() {
		StringBuilder ss = new StringBuilder();
		ss.append("[");
		for (Entity i : this) {
			if (i != this.get(0))
				ss.append(", ");
			ss.append(i.toLogOutput());
		}
		ss.append("]");
		return ss.toString();
	}

	public String toString() {
		return this.toLogOutput();
	}
}
