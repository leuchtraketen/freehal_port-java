package net.freehal.core.lang.german;

import java.util.HashSet;
import java.util.Set;

import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.Tagger2012;
import net.freehal.core.util.RegexUtils;

public class GermanTagger extends Tagger2012 implements AbstractTagger {

	Set<String> builtinEntityEnds = new HashSet<String>();
	Set<String> builtinMaleNames = new HashSet<String>();
	Set<String> builtinFemaleNames = new HashSet<String>();
	Set<String> customNames = new HashSet<String>();

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

}
