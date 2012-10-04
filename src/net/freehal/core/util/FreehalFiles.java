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
package net.freehal.core.util;

import java.util.HashMap;
import java.util.Map;

public class FreehalFiles {
	public static final String ALL_PROTOCOLS = "all";
	private static final String NO_GENERAL_IMPL_FOUND = "There must be at least one FreehalFile implementation "
			+ "which is registered for all protocols";
	private static Map<String, FreehalFile> impls = new HashMap<String, FreehalFile>();

	public static Map<String, FreehalFile> getImpls() {
		return impls;
	}

	public static void add(String protocol, FreehalFile fileImpl) {
		FreehalFiles.impls.put(protocol, fileImpl);
	}

	private static FreehalFile getImpl(String path) {
		for (final String protocol : impls.keySet()) {
			if (!protocol.equals(ALL_PROTOCOLS) && path.startsWith(protocol + ":")) {
				return impls.get(protocol);
			}
		}
		for (final String protocol : impls.keySet()) {
			if (protocol.equals(ALL_PROTOCOLS)) {
				return impls.get(protocol);
			}
		}
		throw new IllegalArgumentException(NO_GENERAL_IMPL_FOUND);
	}

	public static FreehalFile create(String path) {
		final FreehalFile impl = FreehalFiles.getImpl(path);
		path = RegexUtils.ireplace(path, "^[a-zA-Z]+[:][/][/]", "");
		path = RegexUtils.ireplace(path, "^[a-zA-Z]+[:]", "");
		return impl.create(path);
	}

	public static FreehalFile create(String dir, String file) {
		final FreehalFile impl = FreehalFiles.getImpl(dir);
		dir = RegexUtils.ireplace(dir, "^[a-zA-Z]+[:][/][/]", "");
		dir = RegexUtils.ireplace(dir, "^[a-zA-Z]+[:]", "");
		return impl.create(dir, file);
	}

	public static FreehalFile create(FreehalFile impl, String file) {
		String dir = impl.getAbsolutePath();
		dir = RegexUtils.ireplace(dir, "^[a-zA-Z]+[:][/][/]", "");
		dir = RegexUtils.ireplace(dir, "^[a-zA-Z]+[:]", "");
		return impl.create(dir, file);
	}

	public static FreehalFile create(FreehalFile fileImpl) {
		return fileImpl;
	}
}
