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
package net.freehal.core.xml;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.xml.XmlUtils.XmlStreamIterator;

/**
 * This interface needs to be implemented by classes which want to read facts
 * from XML files.
 * 
 * @see XmlUtils#readXmlFacts(XmlStreamIterator, FreehalFile, XmlFactReciever)
 * @author "Tobias Schulz"
 */
public interface XmlFactReciever {

	/**
	 * When a fact is found in an XML file or input stream, than the following
	 * method is invoked.
	 * 
	 * @see XmlUtils#readXmlFacts(XmlStreamIterator, FreehalFile,
	 *      XmlFactReciever)
	 * @param xfact
	 *        the fact as XmlFact object
	 * @param countFacts
	 *        the amount of facts in the input source
	 * @param start
	 *        the timestamp when we started reading
	 * @param filename
	 *        the name of the file if we are reading from a file, {@code null}
	 *        otherwise
	 * @param countFactsSoFar
	 *        how many facts have been read so far
	 */
	public void useXmlFact(XmlFact xfact, int countFacts, long start, FreehalFile filename,
			int countFactsSoFar);

}
