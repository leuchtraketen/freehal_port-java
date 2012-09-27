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
package net.freehal.core.lang.german;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.freehal.core.pos.Tagger2012;
import net.freehal.core.pos.TaggerCache;
import net.freehal.core.pos.Tags;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.xml.Word;

public class GermanTagger extends Tagger2012 {

	Set<String> builtinEntityEnds = new HashSet<String>();
	Set<String> builtinMaleNames = new HashSet<String>();
	Set<String> builtinFemaleNames = new HashSet<String>();
	Set<String> customNames = new HashSet<String>();
	Map<String, Tags> builtinPosTypes = new HashMap<String, Tags>();

	public GermanTagger(TaggerCache container) {
		super(container);

		for (String s : GermanBuiltinData.builtinEntityEnds.split(";")) {
			builtinEntityEnds.add(s);
		}
		for (String s : GermanBuiltinData.builtinMaleNames.split(";")) {
			builtinMaleNames.add(s);
		}
		for (String s : GermanBuiltinData.builtinFemaleNames.split(";")) {
			builtinFemaleNames.add(s);
		}
		for (String s : GermanBuiltinData.builtinPosTypes) {
			String[] parts = s.split("[:][ ]");
			if (parts.length == 2) {
				builtinPosTypes.put(parts[0], new Tags(parts[1], null));
			}
			if (parts.length == 3) {
				builtinPosTypes.put(parts[0], new Tags(parts[1], parts[2]));
			}
		}
	}

	@Override
	public boolean isName(String _name) {
		String name = _name.toLowerCase();

		if (builtinEntityEnds.contains(name))
			return false;

		if (builtinMaleNames.contains(name))
			return true;

		if (builtinFemaleNames.contains(name))
			return true;

		if (customNames.contains(name))
			return true;

		if (isJob(name))
			return true;

		return false;
	}

	private boolean isJob(String name) {
		return RegexUtils
				.ifind(name,
						"(soehne|shne|toechter|tchter|gebrueder|brueder)|(^bundes)|(minister)|(meister$)|(ger$)");
	}

	@Override
	public boolean isIndexWord(final Word word) {
		if (word.equals("a") || word.equals("the") || word.equals("in")
				|| word.equals("verb") || word.equals("von")
				|| word.equals("der") || word.equals("die")
				|| word.equals("das") || word.equals("ein")
				|| word.equals("eine"))
			return false;

		return super.isIndexWord(word);
	}

	@Override
	protected Tags getBuiltinTags(String word) {
		if (builtinPosTypes.containsKey(word))
			return builtinPosTypes.get(word);
		else
			return null;
	}
}
