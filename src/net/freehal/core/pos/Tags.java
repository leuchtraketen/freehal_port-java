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
package net.freehal.core.pos;

import net.freehal.core.grammar.Grammar;

/**
 * A part of speech tag which consists of a specific word class and a specific
 * grammatical gender and is immutable.
 * 
 * @author "Tobias Schulz"
 */
public final class Tags {

	/** a comma (if used between main and sub clauses) **/
	public static final String COMMA = "komma";
	/** a verb */
	public static final String VERB = "v";
	/** an article */
	public static final String ARTICLE = "art";
	/** an adjective */
	public static final String ADJECTIVE = "adj";
	/** a preposition */
	public static final String PREPOSITION = "prep";
	/** a question word or a coordinating or subordinating conjunction */
	public static final String QUESTIONWORD = "questionword";
	/** a logical or correlative conjunction */
	public static final String LINKING = "linking";
	/** a noun or a pronoun */
	public static final String NOUN = "n";

	private String category = null;
	private String gender = null;
	private String word = null;
	private Boolean isName = null;

	/**
	 * Construct a new part of speech tag with the given word class and the
	 * given grammatical gender.
	 * 
	 * @param category
	 *        the word class
	 * @param gender
	 *        the grammatical gender
	 */
	public Tags(String category, String gender) {
		init(null, category, gender, null);
	}

	/**
	 * Construct a new part of speech tag with the given word class and the
	 * given grammatical gender and the given word (which is used for
	 * {@link #getLexicalClassForGrammar()}).
	 * 
	 * @param category
	 *        the word class
	 * @param gender
	 *        the grammatical gender
	 * @param word
	 *        the word as a string
	 */
	public Tags(String category, String gender, String word) {
		init(null, category, gender, word);
	}

	/**
	 * Construct a new part of speech tag by first copying all attributes from
	 * the given {@link Tags} instance and then overriding the attributes of the
	 * constructed instance by other arguments (word class and grammatical
	 * gender) if they are not {@code null}.
	 * 
	 * @param tags
	 *        the tags instance to copy
	 * @param category
	 *        the word class
	 * @param gender
	 *        the grammatical gender
	 */
	public Tags(Tags tags, String category, String gender) {
		init(tags, category, gender, null);
	}

	/**
	 * Construct a new part of speech tag by first copying all attributes from
	 * the given {@link Tags} instance and then overriding the attributes of the
	 * constructed instance by other arguments (word class, grammatical gender
	 * and word string) if they are not {@code null}.
	 * 
	 * @param tags
	 *        the tags instance to copy
	 * @param category
	 *        the word class
	 * @param gender
	 *        the grammatical gender
	 * @param word
	 *        the word as a string
	 */
	public Tags(Tags tags, String category, String gender, String word) {
		init(tags, category, gender, word);
	}

	private void init(Tags tags, String category, String gender, String word) {
		if (tags != null) {
			this.category = tags.category;
			this.gender = tags.gender;
			this.word = tags.word;
			this.isName = tags.isName;
		}
		if (category != null)
			this.category = category;
		if (gender != null)
			this.gender = gender;
		if (word != null)
			this.word = word;
	}

	/**
	 * Check whether the category stored in this instance is equivalent to the
	 * given category.
	 * 
	 * @param otherCategory
	 *        the given category
	 * @return {@code true} if they match, {@code false} otherwise.
	 */
	public boolean isCategory(String otherCategory) {
		return category != null && category.equals(otherCategory);
	}

	/**
	 * Is there a category stored in this instance?
	 * 
	 * @return {@code true} if there is a category set, {@code false} otherwise.
	 */
	public boolean hasCategory() {
		return category != null;
	}

	/**
	 * Returns the category stored in this instance.
	 * 
	 * @return the category string
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Check whether the gender stored in this instance is equivalent to the
	 * given gender.
	 * 
	 * @param otherGender
	 *        the given gender
	 * @return {@code true} if they match, {@code false} otherwise.
	 */
	public boolean isGender(String otherGender) {
		return gender != null && gender.equals(otherGender);
	}

	/**
	 * Is there a gender stored in this instance?
	 * 
	 * @return {@code true} if there is a gender set, {@code false} otherwise.
	 */
	public boolean hasGender() {
		return gender != null;
	}

	/**
	 * Returns the gender stored in this instance.
	 * 
	 * @return the gender string
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Map the category stored in this instance to a grammar word class from
	 * {@link net.freehal.core.grammar.Grammar.LexicalClass}.
	 * 
	 * @see net.freehal.core.grammar.Grammar.LexicalClass
	 * @return a grammar word class constant from
	 *         {@link net.freehal.core.grammar.Grammar.LexicalClass}
	 */
	public String getLexicalClassForGrammar() {
		if (category == null)
			return Grammar.LexicalClass.NULL;
		else if (category.equals(COMMA))
			return Grammar.LexicalClass.KOMMA;
		else if (category.equals(VERB))
			return Grammar.LexicalClass.VERB;
		else if (category.equals(ARTICLE))
			return Grammar.LexicalClass.ARTICLE;
		else if (category.equals(ADJECTIVE))
			return Grammar.LexicalClass.ADJECTIVE;
		else if (category.equals(PREPOSITION))
			return Grammar.LexicalClass.PREPOSITION;
		else if (category.equals(QUESTIONWORD))
			return Grammar.LexicalClass.QUESTIONWORD;
		else if (category.equals(LINKING))
			return Grammar.LexicalClass.LINKING;
		else if (category.equals(NOUN)) {
			if (isName == null && word != null) {
				isName = Taggers.getTagger().isName(word);
			}
			if (isName != null && isName)
				return Grammar.LexicalClass.TITLE;
			else
				return Grammar.LexicalClass.NOUN;
		} else
			return Grammar.LexicalClass.NULL;
	}

	/**
	 * There are some deprecated names for word classes in older Freehal
	 * database files; this method returns the newest name.
	 * 
	 * @param category
	 *        the deprecated category name
	 * @return the the newest name if the given name was deprecates, or the same
	 *         name otherwise.
	 */
	public static String getUniqueCategory(String category) {
		if (category.equals("komma"))
			return COMMA;
		else if (category.equals("vi") || category.equals("vt") || category.equals("ci"))
			return VERB;
		else if (category.startsWith("a") && !category.equals("art"))
			return ADJECTIVE;
		else if (category.equals("n") || category.equals("f") || category.equals("m")
				|| category.startsWith("n,") || category.equals("pron") || category.equals("b"))
			return NOUN;
		else if (category.equals("fw") || category.startsWith("ques"))
			return QUESTIONWORD;
		else
			return category;
	}

	/**
	 * Print word class and gender as tags file format.
	 * 
	 * @return a string in tags file format
	 */
	public String toTagsFormat() {
		StringBuilder code = new StringBuilder();
		if (category != null)
			code.append(category);
		if (category != null && gender != null)
			code.append("|");
		if (gender != null)
			code.append(gender);
		return code.toString();
	}

	@Override
	public String toString() {
		return "{" + (hasCategory() ? "category=" + category : "")
				+ (hasCategory() && hasGender() ? "," : "") + (hasGender() ? "gender=" + gender : "") + "}";
	}
}
