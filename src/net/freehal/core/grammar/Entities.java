/*******************************************************************************
 * Copyright (c) 2006 - 2012 Tobias Schulz and Contributors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 ******************************************************************************/
package net.freehal.core.grammar;

import java.util.ArrayList;


public class Entities extends ArrayList<Entity> {

	private static final long serialVersionUID = 1658375404776969761L;

	public Entities() {
		super();
	}

	public Entities(Entities entities) {
		super(entities);
	}

	public void add(Entities entities, Entity previous, Grammar2012 grammar) {

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

	public String print() {
		StringBuilder ss = new StringBuilder();
		ss.append("[");
		for (Entity i : this) {
			if (i != this.get(0))
				ss.append(", ");
			ss.append(i.print());
		}
		ss.append("]");
		return ss.toString();

	}
}
