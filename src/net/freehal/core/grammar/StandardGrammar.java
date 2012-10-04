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

import net.freehal.core.util.FreehalFile;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.freehal.core.storage.Storages;
import net.freehal.core.typedefs.StringMultiMap;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.MultiMap;
import net.freehal.core.util.Mutable;
import net.freehal.core.util.Pair;
import net.freehal.core.util.StringLengthComparator;
import net.freehal.core.xml.Word;

public abstract class StandardGrammar extends Grammar {
	private HashMap<String, Entity> symbolmapStrObj = new HashMap<String, Entity>();
	private HashMap<Entity, String> symbolmapObjStr = new HashMap<Entity, String>();
	private MultiMap<String, Entities> grammarmap = new MultiMap<String, Entities>();
	private List<MultiMap<String, Pair<Entity, Entities>>> reducemap = new ArrayList<MultiMap<String, Pair<Entity, Entities>>>();
	private List<List<String>> reducekeysSorted = new ArrayList<List<String>>();

	private Entity addEntity(Entity entity) {
		symbolmapStrObj.put(entity.toString(), entity);
		symbolmapObjStr.put(entity, entity.toString());

		return entity;
	}

	private Entity addSymbol(String str) {
		Entity key = s2o(str);
		if (key != null) {
			return key;
		}

		key = new Entity(this, str);
		if (!key.toString().equals(str)) {
			LogUtils.e("Error! key.to_str() != line: " + key.toString() + " != " + str);
		}
		addEntity(key);
		return key;
	}

	public Entity modifySymbol(Entity obj, List<String> _str) {
		String str = new String();
		for (String it : _str) {
			str += "/" + it;
		}

		Entity modified = new Entity(obj);
		modified.add(str);

		return addEntity(modified);
	}

	private String allToKey(Entities entities) {
		if (entities.size() == 0)
			return "";
		StringBuilder str = new StringBuilder();
		str.append(" ");
		for (Entity it : entities) {
			str.append(it.toKey()).append(" ");
		}
		return str.toString();
	}

	private String allToString(Entities entities) {
		if (entities.size() == 0)
			return "";
		StringBuilder str = new StringBuilder();
		str.append(" ");
		for (Entity it : entities) {
			str.append(it.toString()).append(" ");
		}
		return str.toString();
	}

	private void buildReducemap() {

		// List<HashMap<String, Pair<Entity, Entities>>> reducemap = new
		// ArrayList<HashMap<String, Pair<Entity, Entities>>>();
		// List<List<String>> reducekeysSorted;

		reducemap.clear();
		reducekeysSorted.clear();

		List<Set<String>> reducekeys = new ArrayList<Set<String>>();

		for (Map.Entry<String, Entities> it : grammarmap.multiEntrySet()) {
			Entity target = s2o(it.getKey());
			if (target != null && target.getRepl().isEmpty()) {
				final String keys = this.allToKey(it.getValue());
				if (keys.length() > 0) {
					int order = target.getOrder();
					if (order >= reducemap.size()) {
						for (int i = reducemap.size(); i <= order; ++i) {
							reducemap.add(new MultiMap<String, Pair<Entity, Entities>>());
							reducekeys.add(new HashSet<String>());
						}
					}
					reducemap.get(order).multiPut(keys, new Pair<Entity, Entities>(target, it.getValue()));
					reducekeys.get(order).add(keys);
				}
			}
		}

		for (int i = 0; i < reducemap.size(); ++i) {
			reducekeysSorted.add(new ArrayList<String>(reducekeys.get(i)));
			Collections.sort(reducekeysSorted.get(i), new StringLengthComparator());
		}
	}

	public void expand() {
		boolean complete = false;
		int run = 0;
		while (!complete) {
			Mutable<Integer> expanded = new Mutable<Integer>(0);

			complete = expandStep(expanded);

			LogUtils.d("Expanding grammar: Step " + ++run + " (" + expanded + ")");
		}

		buildReducemap();
	}

	private boolean expandStep(Mutable<Integer> expanded) {
		Mutable<Boolean> complete = new Mutable<Boolean>(true);
		MultiMap<String, Entities> newGrammarmap = new MultiMap<String, Entities>();

		for (Map.Entry<String, Entities> it : grammarmap.multiEntrySet()) {

			Entities oldvalue = it.getValue();
			List<Entities> newvalues = expandEntry(oldvalue, expanded, complete);

			for (Entities newvalue : newvalues) {
				newGrammarmap.multiPut(it.getKey(), newvalue);
			}
		}

		grammarmap = null;
		grammarmap = newGrammarmap;

		return complete.get();
	}

	private List<Entities> expandEntry(Entities oldvalue, Mutable<Integer> expanded, Mutable<Boolean> complete) {

		List<Entities> newvalues = new ArrayList<Entities>();
		newvalues.add(new Entities());

		for (final Entity toReplace : oldvalue) {

			final String keyOfToReplace = toReplace.toKey();
			int count = grammarmap.count(keyOfToReplace);

			if (toReplace.type() == 'r') {

				if (count == 0) {
					LogUtils.e("Error! Count of symbol is 0: " + toReplace.toString());
				} else if (count == 1) {
					Entities replacement = grammarmap.get(keyOfToReplace).iterator().next();
					for (Entities it : newvalues) {
						it.add(replacement, toReplace, this);
					}
				} else { // count >= 2
					if (expanded.get() != null)
						expanded.set(expanded.get() + 1);
					if (newvalues.size() == 1) {
						// make count minus 1 copies
						int g;
						for (g = 1; g < count; ++g) {
							newvalues.add(new Entities(newvalues.get(0)));
						}

						// for each copy, add the replacement at the end
						Set<Entities> replacements = grammarmap.get(keyOfToReplace);
						g = 0;
						for (Entities replacement : replacements) {
							newvalues.get(g++).add(replacement, toReplace, this);
						}
					} else {
						// we need one more run of this function to handle this
						// just copy the rest
						complete.set(false);
						for (Entities it : newvalues) {
							it.add(toReplace);
						}
					}
				}
			} else {
				for (Entities it : newvalues) {
					it.add(toReplace);
				}
			}
		}
		return newvalues;
	}

	private Entities parseInput(List<Word> words) {
		Entities wordsI = new Entities();

		// marker
		{
			Entity obj = this.addEntity(new Entity(this, "d-^"));
			obj.setText("");
			wordsI.add(obj);
		}

		// for each word in the input sentence
		for (Word word : words) {
			// ignore invalid words
			if (word.getWord().isEmpty() || word.equals("null")) {
				continue;
			}

			// ignore words without part of speech
			if (!word.hasTags()) {
				continue;
			}

			// construct objects
			Entity obj = addEntity(new Entity(this, word.getTags().getGrammarType()));
			obj.setText(word.getWord());
			wordsI.add(obj);
		}

		// marker
		{
			Entity obj = addEntity(new Entity(this, "d-$"));
			obj.setText("");
			wordsI.add(obj);
		}

		return wordsI;
	}

	private List<Entities> reduce(Entities oldWordsI) {

		LogUtils.i("reduce: " + print(oldWordsI));

		List<Entities> finalList = new ArrayList<Entities>();

		Map<String, Entities> wordsList = new HashMap<String, Entities>();
		wordsList.put(this.allToString(oldWordsI), oldWordsI);
		int step = 0;
		while (step++ <= 50) {
			LogUtils.i("reduce step " + step + "... " + wordsList.size() + " possibilities...");
			if (wordsList.size() == 0)
				break;

			Map<String, Entities> newWordsList = new HashMap<String, Entities>();
			for (Entities entities : wordsList.values()) {
				LogUtils.d("  with " + print(entities));
				newWordsList.putAll(this.reduceStep(entities));
			}
			wordsList = null;
			wordsList = newWordsList;

			for (Entities entities : wordsList.values()) {
				if (entities.size() == 1 && entities.get(0).toKey().equals("s-all")) {

					LogUtils.d(" !!! done: " + print(entities));
					finalList.add(entities);
				}
			}
		}

		return finalList;
	}

	private HashMap<String, Entities> reduceStep(Entities oldWordsI) {
		final String oldImpression = this.allToKey(oldWordsI);
		HashMap<String, Map.Entry<Integer, HashMap<String, Entities>>> newWordsComplexityMap = new HashMap<String, Map.Entry<Integer, HashMap<String, Entities>>>();
		String inThisStepReduceTo = "";

		boolean found = false;
		int order = -1;
		// while we haven't found anything, and in the right order
		while (!found && ++order < reducemap.size()) {
			// for each key
			for (final String key : reducekeysSorted.get(order)) {
				// does it match?
				if (oldImpression.contains(key)) {
					LogUtils.d("    found: '" + key + "' in '" + oldImpression + "'");

					// for each rule we found
					for (final Pair<Entity, Entities> rule : reducemap.get(order).get(key)) {

						LogUtils.d("    rule: " + key + " --> " + print(rule.getFirst()) + " ; "
								+ print(rule.getSecond()));

						String complexityKey = print(rule.getFirst());
						if (complexityKey.contains("$")) {
							complexityKey = "{" + complexityKey.substring(complexityKey.indexOf("$"));
						}
						if (!inThisStepReduceTo.isEmpty() && !complexityKey.equals(inThisStepReduceTo)) {
							LogUtils.d("      wrong target entity(" + complexityKey
									+ "), in this step we'll reduce to " + inThisStepReduceTo);
							continue;
						}

						int complexity = rule.getSecond().size();
						int best_complexity = 0;
						if (newWordsComplexityMap.containsKey(complexityKey)) {
							best_complexity = newWordsComplexityMap.get(complexityKey).getKey();
						}
						if (complexity >= best_complexity) {
							if (complexity > best_complexity) {
								newWordsComplexityMap.remove(complexityKey);
								newWordsComplexityMap.put(

								complexityKey,
										new AbstractMap.SimpleEntry<Integer, HashMap<String, Entities>>(
												complexity, new HashMap<String, Entities>()));
								inThisStepReduceTo = complexityKey;
								found = true;

								LogUtils.d("      best complexity. deleting worse data. (" + complexity
										+ " > " + best_complexity + ")");
							} else {
								LogUtils.d("      equal complexity. (" + complexity + " = " + best_complexity
										+ ")");

							}

							Entities newWordsI = this.replaceInList(oldWordsI, rule.getSecond(),
									rule.getFirst());

							newWordsComplexityMap.get(complexityKey).getValue().put(

							this.allToString(newWordsI), newWordsI);
							if (complexityKey.contains("*"))
								newWordsComplexityMap.get(complexityKey).getValue().put(

								this.allToString(oldWordsI), oldWordsI);

						} else {
							LogUtils.d("      too low complexity. (" + complexity + " < " + best_complexity
									+ ")");

						}
					}
				}
			}
		}

		HashMap<String, Entities> newWordsList = new HashMap<String, Entities>();
		for (Map.Entry<Integer, HashMap<String, Entities>> entry : newWordsComplexityMap.values()) {
			newWordsList.putAll(entry.getValue());
		}
		newWordsComplexityMap = null;
		return newWordsList;
	}

	private Entities replaceInList(Entities vec, Entities find, Entity replacement) {
		if (find.size() == 0)
			return (new Entities(vec));

		boolean replaceDone = false;
		Entities newList = new Entities();
		for (int i = 0; i < vec.size(); ++i)
			if (!replaceDone && vec.get(i).toKey().equals(find.get(0).toKey())) {
				boolean found = true;
				int f, j;
				for (f = 1, j = i + 1; j < vec.size() && f < find.size(); ++f, ++j) {
					if (!vec.get(j).toKey().equals(find.get(f).toKey())) {
						found = false;
						break;
					}
				}
				if (found) {
					Entities foundList = new Entities();
					for (f = 0, j = i; j < vec.size() && f < find.size(); ++f, ++j) {
						Entity e = this.modifySymbol(vec.get(j), find.get(f).getMarker());

						foundList.add(e); // /////////////////////////
					}

					Entity e = new Entity(this, replacement.toString(), foundList);
					this.addEntity(e);
					newList.add(e);
					i = j - 1;

					replaceDone = true;
				} else {
					newList.add(vec.get(i));
				}
			} else {
				newList.add(vec.get(i));
			}

		LogUtils.d("      replace " + print(find) + " with " + print(replacement) + "\n      in "
				+ print(vec) + "\n      is " + print(newList) + "\n");

		return newList;

	}

	private String print(Entity e) {
		return "{" + e.print() + "}";
	}

	private String print(Entities e) {
		return e.print();
	}

	public StandardGrammar() {
		super();
	}

	public boolean readGrammar(FreehalFile filename) {
		Iterable<String> lines = FileUtils.readLines(Storages.getStorage().getLanguageDirectory(), filename);

		if (!lines.iterator().hasNext()) {
			LogUtils.e("Error! Could not open grammar file: " + filename);
			return false;
		}

		Entity keyObj = null;
		for (String line : lines) {
			if (line.startsWith("#"))
				continue;

			if (line.endsWith(":")) {
				line = line.replaceAll(":$", "");
				keyObj = addSymbol(line);
			}
			if (line.startsWith("  ")) {
				line = line.replaceAll("^[ ]+", "");

				Entities valueI = new Entities();
				String[] parts = line.split("[ ]+");
				for (String it : parts) {
					Entity valObj = addSymbol(it);
					if (valObj.type() != 0) {
						valueI.add(valObj);
					}
				}

				grammarmap.multiPut(keyObj.toKey(), valueI);
			}
		}

		buildReducemap();
		expand();

		return true;
	}

	public String toString() {
		StringBuilder grammarStr = new StringBuilder();
		for (Map.Entry<String, Entities> entry : grammarmap.multiEntrySet()) {

			Entity first = s2o(entry.getKey());
			grammarStr.append(first != null ? first.toString() : "#null").append(" = ");

			Entities value = entry.getValue();
			for (Entity entity : value) {
				if (entity != value.get(0)) {
					grammarStr.append(", ");
				}
				grammarStr.append(entity.toString());
			}
			if (value.size() == 0) {
				grammarStr.append("EMPTY");
			}

			grammarStr.append("\n");
		}
		return grammarStr.toString();
	}

	public List<Entities> parse(List<Word> words) {

		LogUtils.i("========================================");
		LogUtils.i("============  Grammar 2012  ============");
		LogUtils.i("========================================");
		LogUtils.i("input: ");
		LogUtils.i(printInput(words));

		Entities wordsI = parseInput(words);
		List<Entities> reduced = reduce(wordsI);

		LogUtils.i("output:");
		LogUtils.i(printOutput(reduced));

		return reduced;
	}

	public String o2s(Entity entity) {
		if (entity == null) {
			return "null";
		} else if (symbolmapObjStr.containsKey(entity)) {
			return symbolmapObjStr.get(entity);
		} else {
			return "unknown";
		}
	}

	public Entity s2o(String str) {
		if (str.equals("null") || str.length() == 0) {
			return null;
		} else if (symbolmapStrObj.containsKey(str)) {
			return symbolmapStrObj.get(str);
		} else {
			return null;
		}
	}

	public static String printInput(List<Word> words) {
		StringBuilder ss = new StringBuilder();

		// for each word
		for (Word word : words) {
			// ignore invalid words
			if (word.getWord().isEmpty() || word.equals("null")) {
				continue;
			}

			// print it
			ss.append("  - ");
			if (word.hasTags())
				ss.append(word.getTags().getGrammarType());

			else
				ss.append("(no tags)");
			ss.append(": '").append(word.getWord()).append("'\n");
		}

		return ss.toString();
	}

	public static String printOutput(List<Entities> list) {
		StringBuilder ss = new StringBuilder();

		for (Entities output : list) {
			for (Entity entity : output) {
				// long
				ss.append(entity.printLong());

				// perl
				StringMultiMap perlmap = entity.toGroups();
				ss.append(Entity.printPerl(perlmap));
			}
		}

		return ss.toString();
	}

	public static String printPerl(List<Entities> list) {
		StringBuilder ss = new StringBuilder();

		for (Entities output : list) {
			for (Entity entity : output) {
				StringMultiMap perlmap = entity.toGroups();
				ss.append(Entity.printPerl(perlmap));
			}
		}

		return ss.toString();
	}

	public static String printGraph(List<Entities> list) {
		StringBuilder ss = new StringBuilder();
		ss.append("digraph parsed {\n");

		for (Entities output : list) {
			for (Entity entity : output) {
				ss.append(entity.toXml());
			}
			break;
		}

		ss.append("}\n");
		return ss.toString();
	}

	public static String printXml(List<Entities> list) {
		StringBuilder ss = new StringBuilder();

		for (Entities output : list) {
			for (Entity entity : output) {
				ss.append(entity.toXml());
			}
			break;
		}

		return ss.toString();
	}

}
