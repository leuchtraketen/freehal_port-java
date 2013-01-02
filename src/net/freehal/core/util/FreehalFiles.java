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
package net.freehal.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * An utility class for creating {@link FreehalFile} objects. <br />
 * <br />
 * 
 * It contains a map with of protocols (like {@code "file"}, {@code "http"},
 * {@code "sqlite"}, ...) and the corresponding {@link FreehalFile}
 * implementations which is filled with {@link #addImplementation(String, FreehalFile)}. When
 * creating a {@link FreehalFile} object using {@link #createInstance(String)}, this
 * map is used to determine the correct implementation. <br />
 * <br />
 * 
 * You must provide a standard implementation which is added with
 * {@link #ALL_PROTOCOLS} as protocol string. The standard implementation is
 * used if the path given to {@link #createInstance(String)} doesn't contain a protocol
 * (like {@code "/home/username/"} instead of {@code "file:///home/username/"})
 * or if the protocol is invalid or unknown. <br />
 * <br />
 * 
 * The protocol prefix (like {@code "http://"}) is never stored in a
 * {@link FreehalFile} object as part of the path.
 * 
 * @author "Tobias Schulz"
 */
public class FreehalFiles {

	/**
	 * Use this if you'd like to add a {@link FreehalFile} implementation for
	 * all protocols or for the case that no protocol is specified using
	 * {@link #addImplementation(String, FreehalFile)}.
	 */
	public static final String ALL_PROTOCOLS = "all";

	private static final String NO_GENERAL_IMPL_FOUND = "There must be at least one "
			+ "FreehalFile implementation " + "which is registered for all protocols!";
	private static Map<String, FreehalFiles.Factory> impls = new HashMap<String, FreehalFiles.Factory>();

	/**
	 * Never used.
	 */
	private FreehalFiles() {}

	/**
	 * Add the given {@link FreehalFile} implementation for the given protocol
	 * (like {@code "file"}, {@code "http"}, {@code "sqlite"}, ...) to the map.
	 * 
	 * @param protocol
	 *        the protocol
	 * @param fileImpl
	 *        the {@link FreehalFile} implementation
	 */
	public static void addImplementation(String protocol, FreehalFiles.Factory fileImpl) {
		if (fileImpl != null)
			FreehalFiles.impls.put(protocol, fileImpl);
	}

	private static FreehalFiles.Factory getFactory(String protocol, String path) {
		if (!impls.containsKey(ALL_PROTOCOLS))
			throw new IllegalArgumentException(NO_GENERAL_IMPL_FOUND);

		for (final String protocol2 : impls.keySet()) {
			if (!protocol2.equals(ALL_PROTOCOLS) && protocol2.equals(protocol)) {
				return impls.get(protocol);
			}
		}

		return impls.get(ALL_PROTOCOLS);
	}

	/**
	 * Construct a new {@link FreehalFile} instance by using the implementation
	 * that is mapped to the protocol found in the path string. If the protocol
	 * is unknown or if there is no protocol in the path string, the standard
	 * implementation is used.
	 * 
	 * @param path
	 *        the path (without any protocol prefix!)
	 * @return a new object implementing {@link FreehalFile}
	 */
	public static FreehalFileImpl createInstance(String path) {
		if (path.contains("://")) {
			String[] p = path.split("[:][/][/]", 2);
			return FreehalFiles.createInstance(p[0], p[1]);
		} else {
			return FreehalFiles.createInstance("all", path);
		}
	}

	/**
	 * Construct a new {@link FreehalFile} instance by using the implementation
	 * that is mapped to the given protocol string. If the protocol is unknown,
	 * the standard implementation is used.
	 * 
	 * @param protocol
	 *        to protocol to use
	 * @param path
	 *        the path (without any protocol prefix!)
	 * @return a new object implementing {@link FreehalFile}
	 */
	public static FreehalFileImpl createInstance(String protocol, String path) {
		path = StringUtils.replace(path, "\\", "/");
		final FreehalFiles.Factory impl = FreehalFiles.getFactory(protocol, path);
		return impl.newInstance(path);
	}

	public interface Factory {
		FreehalFileImpl newInstance(String path);
	}
}
