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
package net.freehal.core.lang.german;

import java.util.List;

import net.freehal.core.lang.Languages;
import net.freehal.core.parser.Parser;
import net.freehal.core.pos.Taggers;
import net.freehal.core.pos.Tags;
import net.freehal.core.storage.Storages;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;

public class GermanParser extends Parser {

	protected String cleanInput(String str) {

		str = RegexUtils.replace(str, "[?]+", "?");
		str = RegexUtils.replace(str, "([?])", "$1.");
		str = RegexUtils.replace(str, "[.!~]\\s*?[.!~]", "~");
		str = RegexUtils.replace(str, "[.!~]\\s*?[.!~]", "~");
		str = RegexUtils.replace(str, "[.!](\\s|$)", " STOP ");
		str = RegexUtils.replace(str, "([0-9]) STOP ", "$1.");
		str = RegexUtils.replace(str, " STOP \\((.*)\\)", " ($0) STOP . ");

		str = RegexUtils.replace(str, "[=]{1,3}[>]", " reasonof ");
		str = RegexUtils.replace(str, "[-]{1,3}[>]", " reasonof ");
		str = RegexUtils.replace(str, "\\(reasonof\\)", " reasonof ");
		str = RegexUtils.replace(str, "\\(reason of\\)", " reasonof ");
		str = RegexUtils.replace(str, "\\(reason\\)", " reasonof ");
		str = RegexUtils.replace(str, "\\(r\\)", " reasonof ");

		List<String> m = null;
		if (str.contains(" reasonof ")) {
			buildPairSentences(str, " reasonof ", "reasonof");
		} else if (str.contains("learn:")) {
			str = StringUtils.replace(str, "learn:", "");
			buildPairSentences(str, ",", "=");
		} else if ((m = RegexUtils.imatch(str, "properties:(.*?):")) != null) {
			str = RegexUtils.replace(str, "properties:(.*?):", "");
			buildPairSentences(str, ",", "ist", m.get(0));
		} else if (RegexUtils.find(str, "[=].*?[=]")) {
			buildPairSentences(str, "=", "=");
		}

		str = RegexUtils.replace(str, "\\s+", " ");
		str = StringUtils.trim(str);

		str = RegexUtils.replace(str, "@", "AT");
		str = RegexUtils.replace(str, "\\s*STOP\\s*", "@");
		str = RegexUtils.trim(str, "@");

		return str;
	}

	private void buildPairSentences(String str, String string, String string2) {
		// TODO Automatisch generierter Methodenstub

	}

	private void buildPairSentences(String str, String string, String string2, String string3) {
		// TODO Automatisch generierter Methodenstub

	}

	protected String simplifyInput(String str, Mutable<Boolean> isQuestion) {
		List<String> m = null;

		isQuestion.set(RegexUtils.find(str, "[?]"));

		str = RegexUtils.ireplace(str, "9637", "\\$\\$");
		str = RegexUtils.ireplace(str, "9489", "\\$\\$");
		str = RegexUtils.ireplace(str, "[?][=][>]", " questionnext ");
		str = RegexUtils.ireplace(str, "[!][=][>]", " factnext ");
		str = RegexUtils.ireplace(str, "[=][>]", " questionnext ");
		str = RegexUtils.ireplace(str, "\\s*[?]", " ?");
		str = RegexUtils.ireplace(str, "^[und]<ws>[,]<ws>", "");

		if (Languages.getCurrentLanguage().isCode("en")) {
			if (str.length() > 7) {
				str = RegexUtils.ireplace(str, "(^|\\s)no(\\s|$)", "$1not a$2");
			}
			str = RegexUtils.ireplace(str, "(^|\\s)an\\s(...)", "$1a $2");
			str = RegexUtils.ireplace(str, "(^|\\s)(are|is|be|was|were|am)\\s(.+?)\\sable\\sto\\s",
					"$1can $3 ");
			str = RegexUtils.ireplace(str, "(^|\\s)(are|is|be|was|were|am)\\sable\\sto\\s", "$1 can ");
			str = RegexUtils.ireplace(str, "(^|\\s)(are|is|be|was|were|am)\\s(.+?)\\sunable\\sto\\s",
					"$1not can $3 ");
			str = RegexUtils.ireplace(str, "(^|\\s)(are|is|be|was|were|am)\\sunable\\sto\\s", "$1 not can ");
		}
		str = RegexUtils.ireplace(str, "[,]", " , ");
		str = RegexUtils.ireplace(str, "\\s+", " ");
		str = RegexUtils.ireplace(str, "<ws>[,]<ws>(und|oder|or|and)", " $1");
		str = RegexUtils.ireplace(str, "^na<ws>[,]<ws>", "");
		str = RegexUtils.ireplace(str, "^naja<ws>[,]<ws>", "");
		str = RegexUtils.ireplace(str, "^und[,]<ws>", "");
		str = RegexUtils.ireplace(str, "^na[,]<ws>", "");
		if (str.length() < 8) {
			str = RegexUtils.ireplace(str, "^und\\s+", "");
			str = RegexUtils.ireplace(str, "^ok\\s+", "");
			str = RegexUtils.ireplace(str, "^gut\\s+", "");
		}
		str = RegexUtils.ireplace(str, "^nein\\s+", "");
		if (str.length() > 5) {
			str = RegexUtils.ireplace(str, "^ja\\s+", "");
		}
		str = RegexUtils.ireplace(str, "^oder\\s+", "");
		str = RegexUtils.ireplace(str, "^na\\s+", " ");
		str = RegexUtils.ireplace(str, "^naja\\s+", " ");
		str = RegexUtils.ireplace(str, "^h[m]+?\\s+", " ");
		str = RegexUtils.ireplace(str, "^(wie|was)<ws>?[,]<ws>?", " ");
		str = RegexUtils.ireplace(str, "\\s\\s", " ");
		str = RegexUtils.ireplace(str, "^[,]", " ");
		str = RegexUtils.ireplace(str, "\\s\\s", " ");

		str = RegexUtils.ireplace(str, "^du\\s([a-zA-Z0-9]+?.?.?)$", "$1");

		str = RegexUtils.ireplace(str, "\\(true\\)", "_(true)_");
		str = RegexUtils.ireplace(str, "\\(maybe\\)", "_(maybe)_");
		str = RegexUtils.ireplace(str, "\\(false\\)", "_(false)_");
		str = RegexUtils.ireplace(str, "\\(logic\\)", "_(logic)_");

		str = RegexUtils.ireplace(str, "(^|\\s)es\\s(.*?\\s)zu\\s(.?ein)em\\s(.*?)\\s((?:kommt)|(?:kommen))",
				"$1$2 $3 $4 ist");
		str = RegexUtils.ireplace(str,
				"(^|\\s)es\\s(.*?\\s)zu\\s(.?ein)em\\s(.*?)\\s((?:gekommen ist)|(?:gekommen war)|(?:kam))",
				"$1$2 $3 $4 war");
		str = RegexUtils.ireplace(str, "(^|\\s)es\\s(.*?\\s)zu\\s(.?ein)er\\s(.*?)\\s((?:kommt)|(?:kommen))",
				"$1$2 $3 $4 ist");
		str = RegexUtils.ireplace(str,
				"(^|\\s)es\\s(.*?\\s)zu\\s(.?ein)er\\s(.*?)\\s((?:gekommen ist)|(?:gekommen war)|(?:kam))",
				"$1$2 $3e $4 war");

		str = RegexUtils.ireplace(str, "jeden Tag ", "_jeden_tag_ ");

		str = RegexUtils.replace(str, "Sie([^a-zA-Z])", "du$1");
		str = RegexUtils.replace(str, "Ihnen([^a-zA-Z])", "dir$1");
		str = RegexUtils.replace(str, "ihnen([^a-zA-Z])", "dir$1");

		str = RegexUtils.replace(str, "eine Menge", "viele");
		str = RegexUtils.replace(str, "(\\d)\\.(\\d)", "$1$2");

		if ((m = RegexUtils.match(str, "^(ist|war|sind|waren)\\s")) != null) {
			final String verb = m.get(0);
			str = RegexUtils.ireplace(str, "^(ist|war|sind|waren)\\s", verb + " ");
		}
		str = RegexUtils.replace(str,
				"^(ist|war|sind|waren)\\s([a-z]+?)\\s(ein.?.?)\\s([A-Z][a-z]+?)(\\s?[?]?)$",
				"$1 _$2_ $3 $4 $5");

		str = RegexUtils.ireplace(str, "^(.*?)du mir deinen Namen.*?$", "Wie ist dein Name? ");
		str = RegexUtils.ireplace(str, "^(.*?)du mir.*?deinen Namen.*?$", "Wie ist dein Name? ");

		if (RegexUtils.find(str, "\\(bad\\)")) {
			str = RegexUtils.ireplace(str, "\\s*?\\(bad\\)", "");
			str = StringUtils.trim(str);
			str = RegexUtils.ireplace(str, "\\s+ ", "_");
			str = "_" + str + "_ = _(bad)_";
		}
		if (RegexUtils.find(str, "\\(good\\)")) {
			str = RegexUtils.ireplace(str, "\\s*?\\(good\\)", "");
			str = StringUtils.trim(str);
			str = RegexUtils.ireplace(str, "\\s+ ", "_");
			str = "_" + str + "_ = _(good)_";
		}

		str = RegexUtils.ireplace(str, "sowohl\\s(.+?)als auch([a-z0-9\\s]*?[A-Z][a-z]+?)(\\s|$)",
				"$1 \\$\\$aswellas\\$\\$ $2$3");
		str = RegexUtils.ireplace(str, "sowohl\\s(.+?)als auch(.*)", "$1 \\$\\$aswellas\\$\\$ $2");
		str = RegexUtils.ireplace(str, "weder\\s(.+?)noch([a-z0-9\\s]*?[A-Z][a-z]+?)(\\s|$)",
				"nicht $1 \\$\\$aswellas\\$\\$ $2$3");
		str = RegexUtils.ireplace(str, "weder\\s(.+?)noch(.*)", "nicht $1 \\$\\$aswellas\\$\\$ $2");

		str = RegexUtils
				.ireplace(
						str,
						"(Anfang|Ende) (januar|jaenner|februar|maerz|april|mai|juni|juli|august|september|oktober|november|dezember)",
						"in dem _$2_");

		str = RegexUtils.ireplace(str, "jaenner", "januar");
		final String months[] = { "januar", "jaenner", "februar", "maerz", "april", "mai", "juni", "juli",
				"august", "september", "oktober", "november", "dezember" };
		int month_num = 1;
		final String year = "2012";
		for (final String month : months) {
			str = RegexUtils
					.ireplace(str, "(seit|bis) " + month + " ([0-9]+)", "$1 01." + month_num + ".$2s");
			str = RegexUtils.ireplace(str, "(seit|bis) " + month, "$1 01." + month_num + "." + year);
			++month_num;
		}

		str = RegexUtils.ireplace(str, "(^)([A-Z][a-z]+?en) (ist) ", "$1_$2_ $3 ");

		str = RegexUtils.ireplace(str, "(^|\\s)genauso (.*?) wie ", "$1genauso $2 wie{{{adj}}} ");
		str = RegexUtils.ireplace(str, "(^|\\s)so (.*?) wie ", "$1so $2 wie{{{adj}}} ");

		str = RegexUtils.ireplace(str, "was ist ", "was ist ");
		str = RegexUtils.replace(str, "was ist ([A-Z][a-z]+?)([\\.?!+,;\\s-]*?)$", "was ist _$1_$2");

		str = RegexUtils.ireplace(str, "wie war ", "wie ist ");
		str = RegexUtils.ireplace(str, "\\s+kein", " nicht ein");
		str = RegexUtils.ireplace(str, "(^|\\s)?und sonst(\\s|\\$)", " wie geht es dir ");
		str = RegexUtils.ireplace(str, "(^|\\s)?bevor\\s", " , bevor ");
		str = RegexUtils.ireplace(str, "(^|\\s)?kurz \\, bevor\\s", " , kurz bevor ");
		str = RegexUtils.ireplace(str, "^ \\,", " ");
		str = RegexUtils.ireplace(str, "^\\,", " ");
		str = RegexUtils.ireplace(str, " mehr als ", " mehr als{{{adj}}} ");
		str = RegexUtils.ireplace(str, " lust zu ", " lust , zu ");
		str = RegexUtils.ireplace(str, " weisst du was (.*) ist ", " was ist $1 ");
		if (str.length() < 14) {
			str = RegexUtils.ireplace(str, " weisst du ", " weisst du , ");
		}
		str = RegexUtils.ireplace(str, " weniger als ", " wenigerals ");
		str = RegexUtils.ireplace(str, " bis zu ", " biszu ");
		str = RegexUtils.ireplace(str, " bis in ", " bisin ");
		str = RegexUtils.ireplace(str, " bis auf ", " bisauf ");
		str = RegexUtils.ireplace(str, "^bis zu ", " biszu ");
		str = RegexUtils.ireplace(str, "^bis in ", " bisin ");
		str = RegexUtils.ireplace(str, "^bis auf ", " bisauf ");
		str = RegexUtils.ireplace(str, "^kein(.*)", "ein$1 nicht");
		str = RegexUtils.ireplace(str, "wozu braucht man ", "was ist ");
		str = RegexUtils.ireplace(str, "(brauch)(st|e|en)(.*?)zu\\s(haben)", "$1$2$3 $4");

		str = RegexUtils.replace(str, "(^|\\s)X(\\s|$)", "$1\\$a\\$$2");
		str = RegexUtils.replace(str, "(^|\\s)Y(\\s|$)", "$1\\$b\\$$2");
		str = RegexUtils.replace(str, "(^|\\s)Z(\\s|$)", "$1\\$c\\$$2");

		str = RegexUtils.ireplace(str, "([0-9]|\\$)\\s*?(mal)\\s*?([0-9]|\\$)", "$1*$3");

		str = RegexUtils.ireplace(str, "(^|\\s)([+-])ein(\\s|$)", "$1$2 1$3");
		final String numbers[] = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht",
				"neun", "zehn", "elf", "zwoelf", "dreizehn", "vierzehn", "fuenfzehn", "sechzehn", "siebzehn",
				"achtzehn", "neunzehn", "zwanzig" };

		int i = 0;
		for (final String number : numbers) {
			str = RegexUtils.ireplace(str, "(^|\\s)" + number + "(\\s|$)", "$1 " + i + "$3");
			str = RegexUtils.ireplace(str, "(^|\\s)[+]" + number + "(\\s|$)", "$1+" + i + "$3");
			str = RegexUtils.ireplace(str, "(^|\\s)[-]" + number + "(\\s|$)", "$1-" + i + "$3");
			++i;
		}

		str = RegexUtils.ireplace(str, "(tag|datum|monat|woche|jahr) war\\s", "$1 ist ");
		str = RegexUtils.ireplace(str, "welche uhrzeit\\s", "wie uhr ");
		str = RegexUtils.ireplace(str, "\\suhr\\shaben\\swir\\s", " uhr ist es ");
		str = RegexUtils.ireplace(str, "\\suhr\\shaben\\swir[?]", " uhr ist es?");
		str = RegexUtils.ireplace(str, "aneinander ", "aneinander");
		str = RegexUtils.ireplace(str, "\\shaben\\swir\\sheute", " haben wir ");
		str = RegexUtils.ireplace(str, "\\sist\\sheute", " ist ");
		str = RegexUtils.ireplace(str, "(\\s|^)(ist|bist)\\s(.*?)\\sheute\\s", "$1$2 $3 ");
		str = RegexUtils.ireplace(str, "welchen\\stag\\shaben\\swir\\s", "welch Datum haben wir ");
		str = RegexUtils.ireplace(str, "welcher\\stag\\sist\\s", "welch Datum haben wir ");

		str = RegexUtils.ireplace(str, " hab ", " habe ");

		if (!RegexUtils.find(str, "(heiss|name)") && Languages.getCurrentLanguage().isCode("de")
				&& str.length() > 20) {
			str = RegexUtils.ireplace(str, " FreeHAL(.?.?.?.?)$", " $1");
		}

		str = " " + str + " ";
		str = RegexUtils.ireplace(str, "\\snoch\\s(nie|nicht)([\\s!.,?]+)", " noch-$1$2");
		str = RegexUtils.ireplace(str, "\\snoch([\\s!.,?]+)", "$1");
		str = RegexUtils.ireplace(str, "\\snoch[-](nie|nicht)([\\s!.,?]+)", " noch $1$2");
		str = RegexUtils.ireplace(str, "(^|[\\s!.,?]+)(so)\\setwas([\\s!.,?]+)", "$1_$2_etwas_$3");
		str = RegexUtils.ireplace(str, "(^|[\\s!.,?]+)(so)was([\\s!.,?]+)", "$1_$2_etwas_$3");

		// TODO!!!

		/*
		 * fs::ifstream remove_words_file;
		 * remove_words_file.open(get_language_directory() /
		 * "remove-words.csv"); if (remove_words_file.is_open()) { string line;
		 * vector<string> remove_words_file_lines; while
		 * (std::getline(remove_words_file, line)) {
		 * remove_words_file_lines.push_back(line); } foreach (string
		 * remove_word, remove_words_file_lines) { remove_word=
		 * StringUtils.trim(remove_word); boolean at_beginning =
		 * algo::starts_with(remove_word, "^"); boolean at_end =
		 * algo::ends_with(remove_word, "$"); if (at_beginning) { str =
		 * RegexUtils.ireplace(str, "^([\\s!.,?]+)" + remove_word +
		 * "([\\s!.,?]+)", "$1$2"); } else if (at_end) { str =
		 * RegexUtils.ireplace(str, "([\\s!.,?]+)" + remove_word +
		 * "([\\s!.,?]+)$", "$1$2"); } else { str = RegexUtils.ireplace(str,
		 * "([\\s!.,?]+)" + remove_word + "([\\s!.,?]+)", "$1$2"); } str =
		 * RegexUtils.ireplace(str, "\\s+", " "); } }
		 */
		str = RegexUtils.ireplace(str, "do you know ", "");
		str = StringUtils.trim(str);
		str = RegexUtils.ireplace(str, "^you know (wh)", "$1");

		str = RegexUtils.ireplace(str, "\\sreally", "");

		str = RegexUtils.ireplace(str, "world wide web", "_world_wide_web_");
		str = RegexUtils.ireplace(str, "Hersteller von", "Hersteller fuer");

		// str = RegexUtils.ireplace(str, "mein name", "mein_name");
		// str = RegexUtils.ireplace(str, "dein name", "dein_name");

		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?januar(\\s|$)", "$1$2.01.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?jaenner(\\s|$)", "$1$2.01.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?februar(\\s|$)", "$1$2.02.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?maerz(\\s|$)", "$1$2.03.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?april(\\s|$)", "$1$2.04.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?mai(\\s|$)", "$1$2.05.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?juni(\\s|$)", "$1$2.06.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?juli(\\s|$)", "$1$2.07.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?august(\\s|$)", "$1$2.08.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?september(\\s|$)", "$1$2.09.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?oktober(\\s|$)", "$1$2.10.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?november(\\s|$)", "$1$2.11.$4");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.\\s*?dezember(\\s|$)", "$1$2.12.$4");

		str = RegexUtils.ireplace(str, "(^|\\s)(\\d)\\.(\\d+?)\\.(\\d+?)(\\s|$)", "$1 0$2.$3.$4$5");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.(\\d)\\.(\\d+?)(\\s|$)", "$1$2.0$3.$4$5");
		str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?)\\.(\\d+?)\\.(\\d\\d)(\\s|$)", "$1$2.$3.19$4$5");
		str = RegexUtils.replace(str, "(^|\\s)(\\d+?)\\.(\\d+?)\\.(\\d+?)(\\s|$)", "$2.$3.$4$5");
		str = RegexUtils.replace(str, "(^|\\s)(\\d+?)\\.(\\d+?)\\.?(\\s|$)", "$2.$3.0000$5");

		str = StringUtils.trim(str);

		str = RegexUtils.ireplace(str, "(^|\\s)sein\\s([A-Z])", "$1sein{{{art}}} $2");

		str = RegexUtils.ireplace(str, "\\s([mds])eines\\s(.*?[a-z])s(\\s|$)", " von $1einem $2$3");
		str = RegexUtils.ireplace(str, "\\s([mds])eines\\s", " von $1einem ");
		str = RegexUtils.ireplace(str, "\\s([mds])eines\\s", " von $1einem ");
		str = RegexUtils.ireplace(str, "\\s([mds])einer\\s", " von $1einer ");
		str = RegexUtils.ireplace(str, "\\s(aus|von|in|an)\\svon\\s([mds])eine([rs])\\s", " $1 $2eine$3 ");

		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\sder\\s([A-Z])", "$1$2 von der _$3_");
		str = RegexUtils.replace(str,
				"(\\s)([A-Z][a-z]*?)\\sdes\\s((?:[a-z]+?\\s)*[A-Z][a-z]*?)s([^a-zA-Z])",
				"$1$2 von dem _$3_$4");
		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\sdes\\s([A-Z])", "$1$2 von dem _$3_");

		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\sjeder\\s([A-Z])", "$1$2 von jeder _$3_");
		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\sjedes\\s([A-Z][a-z]*?)s([^a-zA-Z])",
				"$1$2 von jedem _$3_$4");
		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\sjedes\\s([A-Z])", "$1$2 von jedem _$3_");

		str = RegexUtils.replace(str, "(\\s)([A-Z][a-z]*?)\\s(mehrer|viel|wenig|einig)er\\s([A-Z])",
				"$1$2 von $3en $4");

		if (RegexUtils.find(str, "(^|\\s)(.+?)\\s(den|dem|der|des)\\s([A-Z])")) {
			if ((m = RegexUtils.match(str.toLowerCase(), "(^|\\s)(laut|nach)\\s(den|dem|der|des)\\s")) != null) {
				final String preposition = m.get(1);
				str = RegexUtils.ireplace(str, "(^|\\s)(laut|nach)\\s(den|dem|der|des)\\s", "$1_"
						+ preposition + "_ $3 ");
			}
		}

		str = RegexUtils.ireplace(str, "\\s?ist dasselbe wie\\s?", " = ");
		str = RegexUtils.ireplace(str, "\\s?ist dasgleiche wie\\s?", " = ");
		str = RegexUtils.ireplace(str, "\\s?ist das selbe wie\\s?", " = ");
		str = RegexUtils.ireplace(str, "\\s?ist das gleiche wie\\s?", " = ");

		str = RegexUtils.ireplace(str, "\\s?is the same as\\s?", " = ");
		str = RegexUtils.ireplace(str, "\\s?is same as\\s?", " = ");

		str = RegexUtils.replace(str, "befindet sich", "liegt");
		str = RegexUtils.replace(str, "sich (.*?)befindet", "$1liegt");
		str = RegexUtils.ireplace(str, "teil von ein.?.?\\s", "teil von ");

		if (isQuestion.get()
				&& !RegexUtils
						.ifind(str,
								"^\\s*?(?:wie|wer|was|wo|wann|warum|wieso|weshalb|welcher|welchem|welches|welche|who|how|where|when|if|what)\\s")) {
			if ((m = RegexUtils.match(str, "^(.*?) hat (.*?)((?:[?].*?)?)$")) != null) {
				final String subject = m.get(1);
				final String prop = m.get(0);

				str = subject + " is-property " + prop + " ?";
			}
		} else {
			if ((m = RegexUtils.match(str, "^(.*?) hat (?:der|die|das)\\s([^A-Z]*?[A-Z][^\\s]+?)\\s(.*?)$")) != null) {
				final String subject = m.get(0);
				String prop = m.get(1);
				String value = m.get(2);

				prop = toName(prop);
				prop = toName(value);

				str = subject + " is-property " + prop + " _:_ " + value;
			}

			/*
			 * if (RegexUtils.find(m, str, "^(.*?) ist ([a-z]+?)\\s*?$")) {
			 * const string& subject = m.get(0); const string& prop = ""; string
			 * value = m.get(1);
			 * ////////////////////////////////////////////////
			 * ////////////////// // TODO //set new flag contains_verb to
			 * contains_part_of_speech with var value, var lang, var path //if
			 * (flag contains_verb) { // value = "" //} if (value.size() > 0) {
			 * trim(value); regex_replace(value, "\\s+", " "); set new array
			 * possible_props to find_property_by_value with var value, var
			 * lang, var path foreach (new var _prop in array possible_props) {
			 * print "prossible property foreach (value: " concat var _prop
			 * concat " = " concat var value concat new line prop = var _prop }
			 * if (var prop) { prop = toName with var prop value = toName with
			 * var value string expr = var prop concat " _:_ " concat var value
			 * str = "var subject is-property var expr" } } }
			 */
		}

		if (!str.contains("?") && !str.contains(",")) {
			if ((m = RegexUtils
					.imatch(str,
							"^(.+?)\\s+?((?:gehoert zu)|(?:liegt in)|(?:\\s?ist in)|(?:\\s?ist .*?teil von))\\s+?(.+?)$")) != null) {
				String a = m.get(0);
				String b = m.get(2);
				Mutable<String> adverbs = new Mutable<String>("");

				a = removeAdverbs(a, adverbs);
				b = removeAdverbs(b, adverbs);

				a = toName(a);
				b = toName(b);

				str = a + " is-part " + b + " " + adverbs;
			}
			if ((m = RegexUtils.imatch(str, "^(.+?)\\s+?(gehoert)\\s+?(.+?)$")) != null) {
				String a = m.get(0);
				String b = m.get(2);
				Mutable<String> adverbs = new Mutable<String>("");

				a = removeAdverbs(a, adverbs);
				b = removeAdverbs(b, adverbs);

				a = toName(a);
				b = toName(b);

				str = a + " is-own " + b + " " + adverbs;
			}
			if ((m = RegexUtils.imatch(str, "^(.+?)\\s+?(beinhaltet)\\s+?(.+?)$")) != null) {
				String a = m.get(2);
				String b = m.get(0);
				Mutable<String> adverbs = new Mutable<String>("");

				a = removeAdverbs(a, adverbs);
				b = removeAdverbs(b, adverbs);

				a = toName(a);
				b = toName(b);

				str = a + " is-part " + b + " " + adverbs;
			}
			if ((m = RegexUtils.imatch(str, "^(.+?)\\s+?(besitzt)\\s+?(.+?)$")) != null) {
				String a = m.get(2);
				String b = m.get(0);
				Mutable<String> adverbs = new Mutable<String>("");

				a = removeAdverbs(a, adverbs);
				b = removeAdverbs(b, adverbs);

				a = toName(a);
				b = toName(b);

				str = a + " is-own " + b + " " + adverbs;
			}
			/*
			 * if (RegexUtils.find(str, "^(.+?)\\s+?(ist|sind|[=])\\s+?(.+?)$")
			 * and flag is_question is false) { String a = m.get(0); String b =
			 * m.get(2); Mutable<String> adverbs = new Mutable<String>("");
			 * removeAdverbs(a, adverbs); removeAdverbs(b, adverbs);
			 * ////////////
			 * ////////////////////////////////////////////////////// // TODO
			 * set new flag contains_verb to contains_part_of_speech with var b,
			 * var lang, var path if (flag contains_verb is false) { a = toName
			 * with var a b = toName with var b if (RegexUtils.find(a not,
			 * "\\(a\\)") and RegexUtils.find(b, "\\(a\\)") and
			 * RegexUtils.find(input not, "[=]")) { str = var a concat " is-a "
			 * concat var b concat " " concat var adverbs } else { str = var a
			 * concat " = " concat var b concat " " concat var adverbs } }}
			 */

			/*
			 * if (!RegexUtils.find(str, "ein|eine|der|die|das")) { boolean
			 * contains_verb = contains_part_of_speech(str, lang, path); if
			 * (!contains_verb) { str = RegexUtils.ireplace(str,
			 * "^(.+?)\\s+?(ist|sind)\\s+?(.+?)\\s?$", "$1 = $3"); } }
			 */

			str = RegexUtils.ireplace(str, "^(ein|eine)\\s", " ");
		}

		/*
		 * set new var __input to var input while var __input matches
		 * /(ein.?.?)\s(<[A-Z]><[a-z]>+?)(\s|$)/ do set new var word to lc with
		 * $1 set new array _collective_nouns to global array collective_nouns
		 * push into array _collective_nouns, var word print "collective noun: "
		 * concat var word concat new line do regex with var __input:
		 * /(ein.?.?)\s(var word)/ -> "" :global:i done
		 */

		str = RegexUtils.ireplace(str, "(^|\\s)du hast (.*?) aus ", "$1du bekommst $2 aus ");
		str = RegexUtils.ireplace(str, "(^|\\s)woher hast du ", "$1woher bekommst du ");
		str = RegexUtils.ireplace(str, "(^|\\s)woher hast du ", "$1woher bekommst du ");

		str = RegexUtils.ireplace(str, " wehnig ", " wenig ");
		str = RegexUtils.ireplace(str, "niss(\\s|$)", "nis$1");
		str = RegexUtils.ireplace(str, "^wovon ", "was ");
		str = RegexUtils.ireplace(str, " wovon ", " was ");
		str = RegexUtils.ireplace(str, "^von was ", "was ");
		str = RegexUtils.ireplace(str, " von was ", " was ");

		str = RegexUtils.ireplace(str, "^([A-Z][a-z]+?en) ist ", "_$1_ ist ");

		str = RegexUtils.ireplace(str, " wirst du genannt", " heisst du ");
		str = RegexUtils.ireplace(str, " wird (.*?) genannt", " $1 ist ");

		str = RegexUtils.ireplace(str, "^das\\s([a-z]+?)\\s(ich|du)", "$2 $1 das ");

		str = RegexUtils.ireplace(str, " ein jeder ", " _jeder_ ");
		str = RegexUtils.ireplace(str, " sinn des lebens", " _sinn_des_lebens_ ");

		str = RegexUtils.ireplace(str, " du jetzt ", " du ");
		str = RegexUtils.ireplace(str, " ich jetzt ", " ich ");

		str = RegexUtils.ireplace(str, " befindet sich ", " liegt ");
		str = RegexUtils.ireplace(str, " befinden sich ", " liegen ");
		str = RegexUtils.ireplace(str, " befinde mich ", " liege ");
		str = RegexUtils.ireplace(str, " befindest dich ", " liegst ");
		str = RegexUtils.ireplace(str, " sich befindet ", " liegt ");
		str = RegexUtils.ireplace(str, " sich befinden ", " liegen ");
		str = RegexUtils.ireplace(str, " mich befinde ", " liege ");
		str = RegexUtils.ireplace(str, " dich befindest ", " liegst ");

		str = RegexUtils.ireplace(str, "(^|\\s)da([r]?)(durch|auf|fuer|an) ", " $3 _das_ ");

		str = RegexUtils.ireplace(str, "([0-9])([a-z]|[A-Z])", "$1 $2");
		str = RegexUtils.ireplace(str, "([a-z]|[A-Z])([0-9])", "$1 $2");
		str = RegexUtils.ireplace(str, "([0-9])\\.([a-zA-Z])", "$1 $2");
		str = RegexUtils.ireplace(str, "([a-z]|[A-Z])\\.([0-9])", "$1 $2");

		str = RegexUtils.ireplace(str, "\\smacht man mit\\s", " ist ");
		str = RegexUtils.ireplace(str, "\\sist mit\\s", " ist-mit ");

		str = RegexUtils.ireplace(str, "Was fuer (.*?) kennst du.*", "was ist $1 ?");

		str = RegexUtils.ireplace(str, "dem Begriff der ", "der ");
		str = RegexUtils.ireplace(str, "den Begriff der ", "die ");
		str = RegexUtils.ireplace(str, "der Begriff der ", "die ");
		str = RegexUtils.ireplace(str, "dem Begriff des ", "dem ");
		str = RegexUtils.ireplace(str, "den Begriff des ", "das ");
		str = RegexUtils.ireplace(str, "der Begriff des ", "das ");

		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils
				.ireplace(
						str,
						"([-_a-zA-Z]+?\\s+?[-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?)",
						"$1 $3 $2 $3 $4");
		str = RegexUtils.ireplace(str,
				"([-_a-zA-Z]+?)\\s*?[,]\\s*?([-_a-zA-Z]+?)\\s*?(und|oder)\\s*?([-_a-zA-Z]+?)",
				"$1 $3 $2 $3 $4");

		str = RegexUtils.ireplace(str, "^was\\sgeht<ws>[?]", "wie geht es dir?");
		str = RegexUtils.ireplace(str, "^was\\sgeht$", "wie geht es dir?");
		str = RegexUtils.ireplace(str, "^was\\sgeht\\sab<ws>[?]", "wie geht es dir?");
		str = RegexUtils.ireplace(str, "^was\\sgeht\\sab$", "wie geht es dir?");

		str = RegexUtils.ireplace(str, "^wie\\slang\\s", "wie ");
		str = RegexUtils.ireplace(str, "^wie\\slange\\s", "wie ");

		str = RegexUtils.ireplace(str, "Ihnen", "dir");
		str = RegexUtils.ireplace(str, "\\sdenn<ws>?[?]", " ?");
		str = RegexUtils.ireplace(str, "\\sdenn[?]", " ?");
		str = RegexUtils.ireplace(str, "\\sdann<ws>?[?]", " ?");
		str = RegexUtils.ireplace(str, "\\sdann[?]", " ?");
		str = RegexUtils.ireplace(str, "St\\.", "St");
		str = RegexUtils.ireplace(str, "bitte (sag|erzaehl)", "$1");
		str = RegexUtils.ireplace(str, "Kannst du mir sagen[,]+", "");
		str = RegexUtils.ireplace(str, "Kannst du mir sagen", "");
		str = RegexUtils.ireplace(str, "sage mir ", "was ist ");
		str = RegexUtils.ireplace(str, "sag was ", "was ist ");
		str = RegexUtils.ireplace(str, "sag etwas ", "was ist ");
		str = RegexUtils.ireplace(str, "sag ", "was ist ");

		str = RegexUtils.ireplace(str, "(ich glaube) ([a-zA-Z])", "$1 , $2");
		str = RegexUtils.ireplace(str, "(ich denke) ([a-zA-Z])", "$1 , $2");
		str = RegexUtils.ireplace(str, "stelle mir eine frage", "was ist ");
		str = RegexUtils.ireplace(str, "stell mir eine frage", "was ist ");
		str = RegexUtils.ireplace(str, "stelle eine frage", "was ist ");
		str = RegexUtils.ireplace(str, "stell eine frage", "was ist ");
		str = RegexUtils.ireplace(str, "Was kannst du mir ueber (.*?) sagen", "was ist $1");
		str = RegexUtils.ireplace(str, "Was weisst du ueber (.*?)$", "was ist $1");
		str = RegexUtils.ireplace(str, "Was kannst du mir ueber (.*?) erzaehlen", "was ist $1");
		str = RegexUtils.ireplace(str, "Was kannst du ueber (.*?) sagen", "was ist $1");
		str = RegexUtils.ireplace(str, "Was weisst du alles", "was ist");

		str = RegexUtils.ireplace(str, "frag mich was", "was ist");
		str = RegexUtils.ireplace(str, "frag mich etwas", "was ist");
		str = RegexUtils.ireplace(str, "frag mich<ws>?[,]", "");
		str = RegexUtils.ireplace(str, "was ist ([dsmk]?ein)([a-zA-Z]+?)\\s", "was ist $1$2 ");
		str = RegexUtils.ireplace(str, "was denkst du ueber ", "was ist ");
		str = RegexUtils.ireplace(str, "wie denkst du ueber ", "was ist ");
		str = RegexUtils.ireplace(str, "was haeltst du von ", "was ist ");
		str = RegexUtils.ireplace(str, "was haelst du von ", "was ist ");
		str = RegexUtils.ireplace(str, "erzaehl mir was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehl mir etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle mir was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle mir etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehl mir bitte was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehl mir bitte etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle mir bitte was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle mir bitte etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzael mir was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzael mir etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaele mir was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaele mir etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehl was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehl etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaehle etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzael was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzael etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaele was(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "erzaele etwas(?:([\\s!?.]?.*?$)|$)", "was ist $1 ?");
		str = RegexUtils.ireplace(str, "Erzaehlst du .*", "was ist?");
		str = RegexUtils.ireplace(str, "was ist\\s+?ueber ", "was ist ");

		if ((m = RegexUtils.imatch(str, "(was heisst) ([a-z])")) != null) {
			final String temp = StringUtils.ucfirst(m.get(1));
			str = RegexUtils.ireplace(str, "(was heisst) ([a-z])", "$1 " + temp);
		}

		str = RegexUtils.ireplace(str, "(was\\sheisst\\s)", "was heisst ");
		str = RegexUtils.replace(str, "(was\\sheisst\\s)([A-Z][a-z]+?\\s[a-z]+?)((?:\\s.?.?.?.?.?)|$)",
				"$1 _$2_$3");

		str = RegexUtils.ireplace(str,
				"(ich weiss nicht)\\s+?(?:(global var questionwords_reg_ex) (.*?))($|[.,])",
				"_$2_ _no-question_ $3 , weiss ich nicht$4");
		str = RegexUtils.ireplace(str,
				"(du weisst nicht)\\s+?(?:(global var questionwords_reg_ex) (.*?))($|[.,])",
				"_$2_ _no-question_ $3 , weisst du nicht$4");
		str = RegexUtils.ireplace(str, "(weiss ich)\\s+?(global var questionwords_reg_ex)", "$1 , $2");
		str = RegexUtils.ireplace(str, "(weisst du)\\s+?(global var questionwords_reg_ex)", "$1 , $2");

		if (!str.contains("_no-question_")
				&& RegexUtils
						.ifind(str,
								"^\\s*?(?:wie|wer|was|wo|wann|warum|wieso|weshalb|welcher|welchem|welches|welche|who|how|where|when|if|what)\\s")
				&& (!str.endsWith("?") || RegexUtils.find(str, "[?].*?[?]"))) {
			str = RegexUtils.replace(str, "\\s*[?]", "");
			str = StringUtils.trim(str);
			str += " ?";
		}

		str = RegexUtils.ireplace(str, "(was (?:machst|tust) du).*?(heute|jetzt|momentan|gerade|grade).*?$",
				"$1 ?");
		str = RegexUtils.ireplace(str, "(das weiss ich,)$", "$1 ?");
		str = RegexUtils.ireplace(str, "(das weiss ich)$", "$1 ?");
		str = RegexUtils.ireplace(str, "sag mir ", "");
		str = RegexUtils.ireplace(str, "sag mir[,]", "");
		str = RegexUtils.ireplace(str, "^<ws>?ob\\s", "");
		str = RegexUtils.ireplace(str, "can you remmember that ", "");
		str = RegexUtils.ireplace(str, "do you know whether ", "");
		str = RegexUtils.ireplace(str, "you know whether ", "");
		str = RegexUtils.ireplace(str, "von wo ", "woher ");
		str = RegexUtils.ireplace(str, "(^|\\s)was fuer eine\\s", " welche ");
		str = RegexUtils.ireplace(str, "(^|\\s)was fuer einen\\s", " welchen ");
		str = RegexUtils.ireplace(str, "(^|\\s)was fuer einem\\s", " welchem ");
		str = RegexUtils.ireplace(str, "(^|\\s)was fuer ein\\s", " welches ");
		str = RegexUtils.ireplace(str, "(^|\\s)was fuer\\s", " welch ");
		str = RegexUtils.ireplace(str, "was (.+?) fuer eine\\s(.+)", "welche $2 $1");
		str = RegexUtils.ireplace(str, "was (.+?) fuer einen\\s(.+)", "welchen $2 $1");
		str = RegexUtils.ireplace(str, "was (.+?) fuer einem\\s(.+)", "welchem $2 $1");
		str = RegexUtils.ireplace(str, "was (.+?) fuer ein\\s(.+)", "welches $2 $1");
		str = RegexUtils.ireplace(str, "was (.+?) fuer\\s(.+)", "welch $2 $1");
		str = RegexUtils.ireplace(str, "can you tell me whether\\s", "");
		str = RegexUtils.ireplace(str, "can you tell me (who|how|where|when|if|what)", "$1 ");
		str = RegexUtils.ireplace(str, "can you tell me\\s", "what is ");

		str = RegexUtils.ireplace(str, "^sobald\\s*?(.*),\\s*?(.*)$", "$1, wenn $2");
		str = RegexUtils.ireplace(str, "^(.*),\\s*?sobald\\s*?(.*)$", "$2, wenn $1");
		str = RegexUtils.ireplace(str, "^wenn\\s*?(.*),\\s*?(.*)$", "$2, wenn $1");

		if (!RegexUtils.find(str, " aus.?.?.?.?$")) {
			str = RegexUtils.ireplace(str, "kennst du ein(e|en) ", "what-nowiki ist ");
			str = RegexUtils.ireplace(str, "kennst du ", "was ist ");
		}

		str = RegexUtils.ireplace(str, "wie heisst du mit ", "du ");

		str = RegexUtils.ireplace(str, "was macht ", "was ");

		str = RegexUtils.ireplace(str, "kannst du (.*?isch)($|(?:.?.?.?.?$))", "kannst du $1 sprechen $2");
		str = RegexUtils.ireplace(str, "kann ich (.*?isch)($|(?:.?.?.?.?$))", "kann ich $1 sprechen $2");

		str = RegexUtils.ireplace(str, "(^|\\s)?wie wird ", "$1 wie ist ");
		str = RegexUtils.ireplace(str, "Wie ist das Wetter heute", "Wie ist das Wetter ");
		str = RegexUtils.ireplace(str, "dir heute", "dir ");
		if (str.length() > 10) {
			str = RegexUtils.ireplace(str, " ja ", " ");
		}

		str = RegexUtils.ireplace(str, "es ist<ws>?$", "ist es ");
		str = RegexUtils.ireplace(str, "es ist<ws>?[?]<ws>?$", "ist es ?");

		// cout << "parser2012: step 3: " << str << endl;

		if ((m = RegexUtils.match(str, "\\s(|d|m|k)ein\\s([a-z]+?en)(\\s|$)")) != null) {
			final String noun = StringUtils.ucfirst(m.get(1));
			str = RegexUtils.replace(str, "\\s(|d|m|k)ein\\s([a-z]+?en)(\\s|$)", " $1ein " + noun + "$3");
		}

		if (RegexUtils.find(str, "(([?])|global var questionwords_reg_ex)")) {
			str = RegexUtils.ireplace(str, " (seinen|ihren|seiner|ihrer|seines|ihres|seine|ihre) ", " das ");
		}

		str = RegexUtils.ireplace(str, "Weisst du etwas ueber ", "was ist ");
		str = RegexUtils.ireplace(str, "was weisst du ueber ", "was ist ");
		str = RegexUtils.ireplace(str, "heise", "heisse");
		str = RegexUtils.ireplace(str, "heist", "heisst");
		str = RegexUtils.ireplace(str, " has to ", " must ");
		str = RegexUtils.ireplace(str, " have to ", " must ");
		str = RegexUtils.ireplace(str, " had to ", " must ");
		str = RegexUtils.ireplace(str, " is able to ", " can ");
		str = RegexUtils.ireplace(str, " am able to ", " can ");
		str = RegexUtils.ireplace(str, "m able to ", " can ");
		str = RegexUtils.ireplace(str, " are able to ", " can ");
		str = RegexUtils.ireplace(str, " were able to ", " can ");
		str = RegexUtils.ireplace(str, " was able to ", " can ");
		str = RegexUtils.ireplace(str, " has been able to ", " can ");
		str = RegexUtils.ireplace(str, " have been able to ", " can ");
		str = RegexUtils.ireplace(str, "don['`']t", "do not");
		str = RegexUtils.ireplace(str, "can['`']t", "can not");
		str = RegexUtils.ireplace(str, "cannot", "can not");
		str = RegexUtils.ireplace(str, "hasn['`']t", "has not");
		str = RegexUtils.ireplace(str, "havn['`']t", "have not");
		str = RegexUtils.ireplace(str, "didn['`']t", "did not");
		str = RegexUtils.ireplace(str, "mustn['`']t", "must not");
		str = RegexUtils.ireplace(str, "n['`']t", " not");
		str = RegexUtils.ireplace(str, "gehts(.?.?.?.?)$", "geht es dir ?");
		str = RegexUtils.ireplace(str, "gehts", "geht es");
		str = RegexUtils.ireplace(str, "geht['`']s", "geht es");
		str = RegexUtils.ireplace(str, "^(.?.?)gibt es ", "$1was ist ");
		str = RegexUtils.ireplace(str, "^(.?.?)gibt es", "$1was ist");
		str = RegexUtils.ireplace(str, "was ist neues", "was gibt es neues");

		str = RegexUtils.ireplace(str, "geht es so[?\\s]", "geht es$1");
		str = RegexUtils.ireplace(str, "wie geht es [?]", "wie geht es dir ?");
		str = RegexUtils.ireplace(str, "wie geht es\\s*?$", "wie geht es dir ?");
		str = RegexUtils.ireplace(str, "wie geht es<ws>?$", "wie geht es dir ?");

		// cout << "parser2012: step 4: " << str << endl;

		for (int j = 1; j < 20; ++j) {
			str = RegexUtils.ireplace(str,
					"([a-zA-Z0-9_]+)<ws>[,]<ws>([a-zA-Z0-9_]+)\\s+(und|oder|or|and)<ws>", "$1 $3 $2 $3 ");
			str = RegexUtils.ireplace(str,
					"<ws>[,]<ws>([a-zA-Z0-9_]+\\s+[a-zA-Z0-9_]+)\\s+(und|oder|or|and)<ws>", " $2 $1 $2 ");
		}

		// str = RegexUtils.ireplace(str, "wie heisst\\sdu", "wer bin ich");
		// str = RegexUtils.ireplace(str, "wie heisse\\s", "wer bist ");
		str = RegexUtils.ireplace(str, "heisst\\sdu", "ist dein name");
		str = RegexUtils.ireplace(str, "du\\sheisst", "dein name ist");
		str = RegexUtils.ireplace(str, "heisse\\sich", "ist mein name");
		str = RegexUtils.ireplace(str, "ich\\sheisse", "mein name ist");
		str = RegexUtils.ireplace(str, "wer\\sbist\\sdu", "was ist dein name");
		str = RegexUtils.ireplace(str, "wer\\sdu\\sbist", "wer ist dein name");

		str = RegexUtils.replace(str, "(ist) ([A-Z][a-z]+?) (ein)", "$1 _$2_ $3");

		str = RegexUtils.ireplace(str, ", die du kennst\\s", " ");
		str = RegexUtils.ireplace(str, "die du kennst\\s", " ");

		str = RegexUtils.ireplace(str, "(^|\\s)es leben", "$1leben");

		str = RegexUtils
				.ireplace(
						str,
						" (\\d+?)\\s*?(gb|gigabyte|mb|megabyte|kb|kilobyte|byte)\\s*?(ram|arbeitsspeicher|festplatte|speicher)",
						" _$1_$2_$3_ ");

		str = RegexUtils.ireplace(str, " brauchst du ", " du brauchst ");
		str = RegexUtils.ireplace(str, " brauche ich ", " ich brauche ");

		str = RegexUtils.ireplace(str, "nenne mir (.*)", "zaehle $1 auf");
		str = RegexUtils.ireplace(str, "nenne (.*)", "zaehle $1 auf");

		str = RegexUtils.ireplace(str, "Zaehle mir alle (.*)en auf.*", "ENUMALL ein $1e");
		str = RegexUtils.ireplace(str, "Zaehle mir die (.*)en auf.*", "ENUMALL eine $1e");
		str = RegexUtils.ireplace(str, "Zaehle mir den (.*)en auf.*", "ENUMALL ein $1e");
		str = RegexUtils.ireplace(str, "Zaehle mir das (.*)en auf.*", "ENUMALL ein $1e");
		str = RegexUtils.ireplace(str, "Zaehle alle (.*)en auf.*", "ENUMALL ein $1e");
		str = RegexUtils.ireplace(str, "Zaehle die (.*)en auf.*", "ENUMALL eine $1e");
		str = RegexUtils.ireplace(str, "Zaehle den (.*)en auf.*", "ENUMALL ein $1e");
		str = RegexUtils.ireplace(str, "Zaehle das (.*)en auf.*", "ENUMALL ein $1e");

		str = RegexUtils.ireplace(str, "Zaehle mir alle (.*)n auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir die (.*)n auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle mir den (.*)n auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir das (.*)n auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle alle (.*)n auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle die (.*)n auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle den (.*)n auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle das (.*)n auf.*", "ENUMALL ein $1");

		str = RegexUtils.ireplace(str, "Zaehle mir alle (.*)s auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir die (.*)s auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle mir den (.*)s auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir das (.*)s auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle alle (.*)s auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle die (.*)s auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle den (.*)s auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle das (.*)s auf.*", "ENUMALL ein $1");

		str = RegexUtils.ireplace(str, "Zaehle mir alle (.*[rmndtp])e auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle alle (.*[rmndtp]) auf.*", "ENUMALL ein $1");

		str = RegexUtils.ireplace(str, "Zaehle mir alle (.*) auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir die (.*) auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle mir den (.*) auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle mir das (.*) auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle alle (.*) auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle die (.*) auf.*", "ENUMALL eine $1");
		str = RegexUtils.ireplace(str, "Zaehle den (.*) auf.*", "ENUMALL ein $1");
		str = RegexUtils.ireplace(str, "Zaehle das (.*) auf.*", "ENUMALL ein $1");

		str = RegexUtils.ireplace(str, "Zaehle (.*) auf.*", "ENUMALL $1");
		str = RegexUtils.ireplace(str, "ENUMALL mir (.*?) auf.*", "ENUMALL $1");
		str = RegexUtils.ireplace(str, "ENUMALL mir (.*)", "ENUMALL $1");

		// cout << "parser2012: step 4.1: " << str << endl;

		str = RegexUtils.ireplace(str, "http[:/]+", "http_");

		str = RegexUtils.ireplace(str,
				"(^|\\s)(eigentlich|wirklich|doch|nun|wenigstens|schliesslich|denn)(\\s|$)", "$1$3");

		str = RegexUtils.ireplace(str, " das verlangen ", " das _verlangen_ ");

		str = RegexUtils.ireplace(str, "wie viel uhr", "wie uhr");
		str = RegexUtils.ireplace(str, "wie viel[a-zA-Z]*\\s", "wie ");
		str = RegexUtils.ireplace(str, "wieviel[a-zA-Z]*\\s", "wie ");
		str = RegexUtils.ireplace(str, "wie spaet", "wie uhr");
		str = RegexUtils.ireplace(str, "wie frueh", "wie uhr");
		str = RegexUtils.ireplace(str, "wie uhr ", "wie _$$time$$_ ");

		str = RegexUtils.ireplace(str, "[=]", " = ");
		str = RegexUtils.replace(str, "opposite", " opposite ");
		str = RegexUtils.ireplace(str, "wofuer steht ", "was ist ");
		str = RegexUtils.ireplace(str, " schon mal ", " ");
		str = RegexUtils.ireplace(str, "hast du schon mal von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du schon von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du mal von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du schon mal was von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du schon was von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du was von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "hast du was mal von (.*?) gehoert", "was ist $1");
		str = RegexUtils.ireplace(str, "^(...*?) hast du ", "$1 du hast ");
		str = RegexUtils.ireplace(str, "^(...*?) habe ich ", "$1 ich habe ");

		// cout << "parser2012: step 4.2: " << str << endl;

		if (str.length() > 24 && RegexUtils.find(str, "(question|fact)next")) {
			str = RegexUtils.ireplace(str, "^weisst du<ws>[,]*<ws>", "");
			str = RegexUtils.ireplace(str, "^weisst du", "");
		}
		str = RegexUtils.ireplace(str, "^weisst du<ws>[,]*<ws>", "");
		str = RegexUtils.ireplace(str, "^weisst du", "");

		str = " " + str + " ";
		str = RegexUtils.ireplace(str, "\\snoch\\s(nie|nicht)([\\s!.,?]+)", " noch-$1$2");
		str = RegexUtils.ireplace(str, "\\snoch([\\s!.,?]+)", "$1");
		str = RegexUtils.ireplace(str, "\\snoch[-](nie|nicht)([\\s!.,?]+)", " noch $1$2");
		str = RegexUtils.ireplace(str, "(^|[\\s!.,?]+)(so)\\setwas([\\s!.,?]+)", "$1_$2_etwas_$3");
		str = RegexUtils.ireplace(str, "(^|[\\s!.,?]+)(so)was([\\s!.,?]+)", "$1_$2_etwas_$3");

		if (str.length() > 24 && str.contains("?")) {
			str = RegexUtils.ireplace(str, "\\sgerne([\\s!.,?]+)", "$1");
		}

		str = RegexUtils.ireplace(str, "\\s(kein|keine|keinen|keiner|keinem|nicht)\\skein(|e|en|er|em)\\s",
				" kein$2 ");
		str = RegexUtils.ireplace(str, "\\s(kein|keine|keinen|keiner|keinem|nicht)\\snicht\\s", " $1 ");
		str = RegexUtils.ireplace(str, "(^|\\s)?k(ein|eine|einen|einer|einem)\\s", "$1nicht $2 ");
		str = RegexUtils.ireplace(str, "\\sim\\s", " in dem ");
		str = RegexUtils.ireplace(str, "\\sbeim\\s", " bei dem ");
		if (Languages.getCurrentLanguage().isCode("de")) {
			str = RegexUtils.ireplace(str, "\\sam\\s([a-zA-Z]*?)ten($|\\s|[,])", " am_$1ten{{{adj}}} $2 ");
			str = RegexUtils.ireplace(str, "\\sam\\s", " an dem ");
			str = RegexUtils.ireplace(str, "\\sins\\s", " in das ");
			str = RegexUtils.ireplace(str, "^im\\s", " in dem ");
			str = RegexUtils.ireplace(str, "^am\\s", " an dem ");
			str = RegexUtils.ireplace(str, "^ins\\s", " in das ");
		}

		if ((m = RegexUtils.match(str, "\\szu[mr]\\s+([a-zA-Z_]+)\\s+([a-zA-Z_]+)(<ws>?[,.?!]*?<ws>?)$")) != null) {
			final String _match = m.get(1);
			if (_match.endsWith("t")) {

				str = RegexUtils.ireplace(str,
						"\\szu([mt])\\s+([a-zA-Z_]+)\\s+([a-zA-Z_]+)(<ws>?[,.?!]*?<ws>?)$",
						" zu$1_\\l$2_\\l$3 $4");
			}
		}

		str = RegexUtils.ireplace(str, "\\szu([mr])\\s+([a-zA-Z_]+)\\s+([A-Z_][a-zA-Z_]+)",
				" zu$1_\\l$2_\\l$3 ");
		str = RegexUtils.ireplace(str, "\\szu([mr])\\s+([a-zA-Z_]+)", " zu$1_\\l$2 ");
		str = RegexUtils.ireplace(str, "^zu([mr])\\s+([a-zA-Z_]+)", " zu$1_\\l$2 ");
		str = RegexUtils.ireplace(str, "[,]\\s+[,]", ",");
		str = RegexUtils.ireplace(str, "^wozu\\s", "wie ");
		str = RegexUtils.ireplace(str, "\\swozu\\s", " wie ");
		str = RegexUtils.ireplace(str, "\\soppo\\s", " opposite ");
		str = RegexUtils.ireplace(str, "\\s.?.?opposite.?.?\\s", " opposite ");

		// cout << "parser2012: step 4.3: " << str << endl;

		str = RegexUtils.ireplace(str, "(^|\\s)(ich|du|das) (weiss) ", "$1$2 $3\\{\\{\\{v\\}\\}\\} ");
		str = RegexUtils.ireplace(str, " (weiss) (ich|du|das) ", " $1\\{\\{\\{v\\}\\}\\} $2 ");

		if (RegexUtils.find(str, " opposite ")) {
			str = StringUtils.trim(str);
			str = RegexUtils.replace(str, "\\s+", "_");

			str = StringUtils.replace(str, "_opposite_", "@");
			String[] opposites = str.split("[@]+");
			str = "_" + opposites[0] + "_ opposite _" + opposites[1] + "_";
		}

		str = RegexUtils.replace(str, "\\szu\\s([a-z]+?)\\s([a-z]+?)(.?.?.?)$", " $2 , _to_ $1 $3");

		{
			str = StringUtils.replace(str, "KOMMA", ",");
			String[] clauses = str.split("[,]+");
			for (String clause : clauses) {
				if ((m = RegexUtils.match(clause, "\\szu\\s([a-z]+?en)\\s")) != null) {
					LogUtils.d("found 'zu'.");
					final String zu_verb = m.get(0);

					clause = StringUtils.trim(clause);
					String[] words = clause.split("\\s+");

					boolean found_other_verb = false;
					boolean found_zu_verb = false;
					for (final String word : words) {
						if (word.length() > 0) {
							if (word != zu_verb) {
								Tags tags = Taggers.getTagger().getPartOfSpeech(word);
								if (tags.isCategory("linking") && found_zu_verb) {
									break;
								}
								if (tags.isCategory("v")) {
									found_other_verb = true;
									LogUtils.d("other verb: " + word);
								}
							} else {
								found_zu_verb = true;
							}
						}
					}
					LogUtils.d("found an other verb: " + (found_other_verb ? "true" : "false"));

					if (found_other_verb) {
						str = RegexUtils.replace(str, "\\szu\\s([a-z]+?en)\\s", " , _to_ $1 ");
					} else {
						str = RegexUtils.replace(str, "\\szu\\s([a-z]+?en)\\s", " _to_ $1 ");
					}
				}
			}
		}

		if (RegexUtils.find(str, "ist es.*?\\szu\\s")) {
			str = RegexUtils.ireplace(str, " ist es ", " ist ");
		}
		if (RegexUtils.find(str, "ist\\ses(\\s[A-Za-z]+?)?(\\s[A-Za-z]+?)?\\s(das|der|die)\\s")) {
			str = RegexUtils.ireplace(str, " ist es ", " ist ");
		}

		str = RegexUtils.trimLeft(str, " ,");

		// cout << "parser2012: step 4.4: " << str << endl;

		// ///////////////////////////////////////////////////
		// Here was the chapter about replacing user defined strings

		/*
		 * static if (is an empty global array replace_array) { string
		 * cache_file_name = '_cache_replace' if (exists: var cache_file_name,
		 * end test) { string cache_str = handle foreach (file name var
		 * cache_file_name, read foreach (new var line from var cache_input is
		 * rw) { push into global array replace_array, var line } } else {
		 * string csv_output = '>>>^0^0^0^0^just_verb^0^0^0^0^0^0' do
		 * hal2012_send_signal with "database_request", var csv_output string
		 * csv_str = hal2012_fetch_signal with "database_request" set new array
		 * csv_input_lines to split with /\\r?\\n/, var csv_input string
		 * cache_output = handle foreach (file name var cache_file_name, write
		 * foreach (new var line in array csv_input_lines) { do regex with var
		 * a: /[+]/ -> "\\\\+" :global:i push into global array replace_array,
		 * var line print into var cache_output data var line concat new line }
		 * do close with var cache_output do trigger_check_files without
		 * arguments } push into global array replace_array, "" } set new array
		 * _replace_array to global array replace_array foreach (new var line in
		 * array _replace_array) { set new array result to an empty array set
		 * new array rawresult to split using /\\^/, var line if (var line) {
		 * string a = from array rawresult element [ 2 ] string b = from array
		 * rawresult element [ 3 ] if (var a and var b and var a not matches var
		 * b) { str = RegexUtils.ireplace(str, "(^|\\s)var a(\\s|$)",
		 * "$1var b$2"); #print "#var a# --> #var b#" #print new line #print var
		 * input #print new line } } }
		 */

		// cout << "parser2012: step 4.5: " << str << endl;
		str = RegexUtils.ireplace(str, "kind of ", "kind_of_");
		str = RegexUtils.ireplace(str, " mal n ", " einen ");
		str = RegexUtils.ireplace(str, " mal nen ", " einen ");
		str = RegexUtils.ireplace(str, " n ", " einen ");
		str = RegexUtils.ireplace(str, " nen ", " einen ");
		str = RegexUtils.ireplace(str, " mal [']n ", " einen ");
		str = RegexUtils.ireplace(str, " mal [']nen ", " einen ");
		str = RegexUtils.ireplace(str, " [']n ", " einen ");
		str = RegexUtils.ireplace(str, " [']nen ", " einen ");
		str = RegexUtils.ireplace(str, " mal [`]n ", " einen ");
		str = RegexUtils.ireplace(str, " mal [`]nen ", " einen ");
		str = RegexUtils.ireplace(str, " [`]n ", " einen ");
		str = RegexUtils.ireplace(str, " [`]nen ", " einen ");

		str = RegexUtils.ireplace(str, " .... username .... ", " \\$\\$username\\$\\$ ");
		str = RegexUtils.ireplace(str, " ..... username ..... ", " \\$\\$username\\$\\$ ");
		str = RegexUtils.ireplace(str, " .... unknownproperty .... ", " \\$\\$unknownproperty\\$\\$ ");
		str = RegexUtils.ireplace(str, " ..... unknownproperty ..... ", " \\$\\$unknownproperty\\$\\$ ");

		if (RegexUtils.ifind(str, "ist\\s(\\d+?)")) {
			str = RegexUtils.ireplace(str, "(^|\\s)(\\d+?) ", "$1_$2_ ");
		}

		str = RegexUtils.ireplace(str, "(^|\\s)tobias schulz", "$1_tobias_schulz_");

		if (Languages.getCurrentLanguage().isCode("de")) {
			str = RegexUtils.ireplace(str, "(^|\\s)im jahre (\\d\\d\\d\\d) ", "$1$2 ");
			str = RegexUtils.ireplace(str, "(^|\\s)im jahr (\\d\\d\\d\\d) ", "$1$2 ");
			if (RegexUtils.find(str, "\\d\\d\\d\\d")) {
				if (!RegexUtils.find(str, "\\svon\\s(\\d\\d\\d\\d)\\s")
						&& !RegexUtils.ifind(str, "ist\\s(\\d\\d\\d\\d)")) {
					str = RegexUtils.ireplace(str, "(^|\\s)(\\d\\d\\d\\d) ", "$1in_jahre_$2 ");
				}
				if (!RegexUtils.find(str, "(\\d\\d\\d\\d)......")) {
					str = RegexUtils.ireplace(str, "(^|\\s)(\\d\\d\\d\\d) ", "$1_$2_ ");
				}
			}
		}

		// cout << "parser2012: step 5: " << str << endl;

		if (RegexUtils.find(str, "[?]")) {
			str = RegexUtils.ireplace(str, "(^|\\s)?(nicht|not)(\\s)", "$1");
		}

		str = RegexUtils.ireplace(str, "(herr|frau|mr|mrs|miss|doktor|dr|firma)\\.? (\\S\\S\\S+?)($|\\s)",
				"_$1_$2_ $3");

		str = RegexUtils.ireplace(str, "sth\\.", "something");
		str = RegexUtils.ireplace(str, "sth\\s", "something ");
		str = RegexUtils.ireplace(str, "do you know (what|who|where|how|when|which|whose)", "$1");
		str = RegexUtils.ireplace(str, "do you know something about ", "what is ");
		str = RegexUtils.ireplace(str, " do you do", " are you");
		str = StringUtils.trim(str);
		str = RegexUtils.ireplace(str, "what<ws>up\\s($|[?])", "how are you?");
		str = RegexUtils.ireplace(str, "what[']s<ws>up\\s($|[?])", "how are you?");
		str = RegexUtils.ireplace(str, "whats<ws>up\\s($|[?])", "how are you?");
		str = RegexUtils.ireplace(str, "how are you doing", "how are you");

		str = RegexUtils.ireplace(str, "what\\'s ", "what is ");
		str = RegexUtils.ireplace(str, "whats ", "what is ");
		str = RegexUtils.ireplace(str, "whos ", "what is ");
		str = RegexUtils.ireplace(str, "who\\'s ", "what is ");
		str = RegexUtils.ireplace(str, "whore ", "what is ");
		str = RegexUtils.ireplace(str, "who\\'re ", "what is ");
		str = RegexUtils.ireplace(str, "who are you.*", "what is your name?");

		str = RegexUtils.ireplace(str, "was ist mit (.*?) los", "was ist $1");
		str = RegexUtils.ireplace(str, "was ist ueber (.*?)", "was ist $1");
		str = RegexUtils.ireplace(str, "was ist los mit (.*?)", "was ist $1");

		str = RegexUtils.ireplace(str, "^(.*?) muss man ", "Man muss $1 ");

		// cout << "parser2012: step 6: " << str << endl;

		str = RegexUtils.ireplace(str, "^weisst du denn noch ", "weisst du ");
		str = RegexUtils.ireplace(str, "^weisst du denn ", "weisst du ");
		str = RegexUtils.ireplace(str, "^weisst du noch ", "weisst du ");
		str = RegexUtils.ireplace(str, "^weisst du (w[^\\s]*?)\\s([^?!.,]*)", "$2 $1");
		str = RegexUtils.ireplace(str, "^weisst du ", "");

		str = RegexUtils.ireplace(str, "wie vie[a-zA-Z]+\\s", "wie ");
		str = RegexUtils.ireplace(str, "^hm\\, ", " ");
		str = RegexUtils.ireplace(str, "^hm \\, ", " ");
		str = RegexUtils.ireplace(str, "\\shm\\, ", " ");
		str = RegexUtils.ireplace(str, "\\shm \\, ", " ");

		{
			str = StringUtils.replace(str, "KOMMA", ",");
			String[] clauses = str.split("[,]+");
			int clause_no = 0;
			for (String clause : clauses) {
				++clause_no;

				if (clause_no > 1
						&& (m = RegexUtils.match(clause, "^\\s*?(der|die|das|den|dem|dessen)\\s([a-z]+?)\\s")) != null) {
					LogUtils.d("maybe found a relative clause.");
					final String rel_verb = m.get(1);
					String[] words = clause.split("\\s+");

					Tags tags = Taggers.getTagger().getPartOfSpeech(rel_verb);
					if (tags.isCategory("v") || words.length < 3) {
						LogUtils.d("not found an relative clause verb: " + rel_verb);
						str = RegexUtils.replace(str, "\\s*?(der|die|das|den|dem|dessen)\\s(" + rel_verb
								+ ")\\s", " _$1_ $2 ");
					} else {
						LogUtils.d("found an relative clause verb: " + rel_verb);
						str = RegexUtils.replace(str, "\\s*?(der|die|das|den|dem|dessen)\\s(" + rel_verb
								+ ")\\s", " $1\\{\\{\\{questionword\\}\\}\\} $2 ");
					}
				}
			}
		}

		str = toUnixtime(str);

		final String mark = "\"";

		str = RegexUtils.ireplace(str, "\\s" + mark + "\\s?([A-Za-z0-9_" + mark + "]+?)\\s?" + mark + "", " "
				+ mark + "$1" + mark + "");

		str = RegexUtils.ireplace(str, "in dem jahr ([\\d]+)", "in dem " + mark + "jahre $1" + mark + "");
		str = RegexUtils.ireplace(str, "in dem jahre ([\\d]+)", "in dem " + mark + "jahre $1" + mark + "");

		int e = 50;
		while (e >= 0) {
			str = RegexUtils.ireplace(str, "" + mark + "([^\\s" + mark + "]+?)\\s([^" + mark + "]*?)" + mark
					+ "", "" + mark + "$1_$2" + mark + "");
			--e;
		}
		str = RegexUtils.ireplace(str, "" + mark + "", "_");

		// cout << "parser2012: step 8: " << str << endl;

		str = StringUtils.trim(str);
		str = RegexUtils.ireplace(str, "questionnext", "q=>");
		str = RegexUtils.ireplace(str, "factnext", "f=>");
		str = RegexUtils.ireplace(str, "[?]<ws>[=]<ws>[>]", "?=>");
		str = RegexUtils.ireplace(str, "\\s+[?][=][>]", ", ?=>");
		str = RegexUtils.ireplace(str, "[!]<ws>[=]<ws>[>]", "!=>");
		str = RegexUtils.ireplace(str, "\\s+[!][=][>]", ", !=>");
		str = RegexUtils.ireplace(str, "[f]<ws>[=]<ws>[>]", "f=>");
		str = RegexUtils.ireplace(str, "\\s+[f][=][>]", ", f=>");
		str = RegexUtils.ireplace(str, "[q]<ws>[=]<ws>[>]", "q=>");
		str = RegexUtils.ireplace(str, "\\s+[q][=][>]", ", q=>");
		str = RegexUtils.ireplace(str, "[=]\\s+[>]", "=>");
		str = RegexUtils.ireplace(str, "\\s+[=][>]", ", =>");
		str = RegexUtils.ireplace(str, "[,]+", " , ");
		str = RegexUtils.ireplace(str, "\\s+", " ");
		str = RegexUtils.ireplace(str, "[_]+", "_");

		if (RegexUtils.find(str, "[?]")) {
			str = StringUtils.lcfirst(str);
		}

		// cout << "parser2012: step 9: " << str << endl;

		Iterable<String> histMale = Storages.inLanguageDirectory("male.history").readLines();
		String last_male_substantive = null;
		for (String line : histMale) {
			if (line.length() > 1)
				last_male_substantive = line;
		}
		if (last_male_substantive != null) {
			str = replaceHe(str, last_male_substantive);
		}

		Iterable<String> histFemale = Storages.inLanguageDirectory("female.history").readLines();
		String last_female_substantive = null;
		for (String line : histFemale) {
			if (line.length() > 1)
				last_female_substantive = line;
		}
		if (last_female_substantive != null) {
			str = replaceShe(str, last_female_substantive);
		}

		return str;
	}

	private String replaceHe(String str, String replacement) {
		RegexUtils.ireplace(str, "(^|\\s)(er|ihn)(\\s|$)", "$1" + replacement + "$3");
		RegexUtils.ireplace(str, "(^|\\s)(he|him)(\\s|$)", "$1" + replacement + "$3");
		return str;
	}

	private String replaceShe(String str, String replacement) {
		RegexUtils.ireplace(str, "(^|\\s)(sie)(\\s|$)", "$1" + replacement + "$3");
		RegexUtils.ireplace(str, "(^|\\s)(she)(\\s|$)", "$1" + replacement + "$3");
		return str;
	}

	private String removeAdverbs(String text, Mutable<String> adverbs) {
		List<String> m;
		if ((m = RegexUtils.match(text, "^(.*?)\\s(von.*?)$")) != null) {
			text = m.get(0);
			if (adverbs.get().length() > 0)
				adverbs.set(adverbs.get() + " ");
			adverbs.set(adverbs.get() + m.get(1));
		}
		return text;
	}

	private String toName(String text) {
		boolean article_undef = RegexUtils.ifind(text, "(^|\\s)(ein|eine)\\s");
		RegexUtils.replace(text, "(^|\\s)(ein|eine)\\s", "");
		boolean article_def = RegexUtils.ifind(text, "(^|\\s)(der|die|das)\\s");
		RegexUtils.replace(text, "(^|\\s)(der|die|das)\\s", "");

		RegexUtils.trim(text, " _");
		RegexUtils.replace(text, "\\s+", "_");

		if (article_undef) {
			text = "(a) " + text;
		} else if (article_def) {
			text = "(the) " + text;
		}

		RegexUtils.replace(text, "_(und|and)_", "_ & _");
		RegexUtils.replace(text, "_(oder|or)_", "_ | _");

		return text;
	}

	private String toUnixtime(String str) {
		return str;
	}

	@Override
	protected String extendInput(String str) {
		return str;
	}
}
