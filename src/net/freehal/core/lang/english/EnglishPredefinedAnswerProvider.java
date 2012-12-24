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
package net.freehal.core.lang.english;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import net.freehal.core.predefined.PredefinedAnswerProvider;
import net.freehal.core.util.RegexUtils;

public class EnglishPredefinedAnswerProvider extends PredefinedAnswerProvider {

	@Override
	protected String tryGreeting(String input) {
		String output = null;
		if (RegexUtils.ifind(input, "(^|[,;.?!-~]|\\s)+" + "(hallo|hi|hey|mahlzeit|"
				+ "((gute|guten|schoene|schoenen)(.+)" + "(tag|morgen|abend|nachmittag|vormittag|nacht)))"
				+ "(\\s|[,;.?!-~]|$)+")) {
			GregorianCalendar cal = new GregorianCalendar();
			int hour = cal.get(Calendar.HOUR_OF_DAY) + 1;
			if (hour < 5)
				output = "Immer noch wach...? Es ist " + hour + " Uhr nachts!";
			else if (hour < 12)
				output = "Guten Morgen!";
			else if (hour < 17)
				output = "Guten Tag!";
			else
				output = "Guten Abend!";
		}
		return output;
	}

	@Override
	protected String tryThanks(String input) {
		String output = null;
		Random random = new Random();
		if (input.contains("danke") && input.length() < 10) {
			List<String> outputs = new ArrayList<String>();
			outputs.add("Nichts zu danken.");
			outputs.add("Gern geschehen.");
			outputs.add("Nichts zu danken...");
			outputs.add("Gern geschehen...");
			outputs.add("Nichts zu danken!");
			outputs.add("Gern geschehen!");
			for (int i = 0; i < random.nextInt(5); ++i)
				Collections.shuffle(outputs, random);
			output = outputs.get(0);
		}

		if (input.contains("bitte") && input.length() < 10) {
			List<String> outputs = new ArrayList<String>();
			outputs.add("Danke!");
			outputs.add("Danke.");
			outputs.add("Danke...");
			for (int i = 0; i < random.nextInt(5); ++i)
				Collections.shuffle(outputs, random);
			output = outputs.get(0);
		}
		return output;
	}
}
