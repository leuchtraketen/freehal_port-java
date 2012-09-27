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

import java.io.File;

import net.freehal.core.grammar.AbstractGrammar;
import net.freehal.core.grammar.FakeGrammar;
import net.freehal.core.phrase.AbstractPhrase;
import net.freehal.core.phrase.FakePhrase;
import net.freehal.core.pos.AbstractTagger;
import net.freehal.core.pos.FakeTagger;

public class FreehalConfig {

	private static FreehalConfigImpl instance = null;

	private static AbstractTagger tagger = null;
	private static AbstractGrammar grammar = null;
	private static AbstractPhrase phrase = null;
	
	static {
		instance = new NoConfiguration();
		tagger = new FakeTagger();
		grammar = new FakeGrammar();
		phrase = new FakePhrase();
	}

	public static void set(FreehalConfigImpl instance) {
		FreehalConfig.instance = instance;
	}

	public static File getLanguageDirectory() {
		return instance.getLanguageDirectory();
	}

	public static File getCacheDirectory() {
		return instance.getCacheDirectory();
	}

	public static String getLanguage() {
		return instance.getLanguage();
	}

	public static boolean isLanguage(final String otherLanguage) {
		return instance.getLanguage().equals(otherLanguage);
	}

	public static File getPath() {
		return instance.getPath();
	}

	public static AbstractTagger getTagger() {
		return tagger;
	}

	public static void setTagger(AbstractTagger tagger) {
		FreehalConfig.tagger = tagger;
	}

	public static AbstractGrammar getGrammar() {
		return grammar;
	}

	public static void setGrammar(AbstractGrammar grammar) {
		FreehalConfig.grammar = grammar;
	}

	public static AbstractPhrase getPhrase() {
		return phrase;
	}

	public static void setPhrase(AbstractPhrase phrase) {
		FreehalConfig.phrase = phrase;
	}
}
