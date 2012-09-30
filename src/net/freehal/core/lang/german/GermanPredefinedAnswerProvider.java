package net.freehal.core.lang.german;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import net.freehal.core.predefined.PredefinedAnswerProvider;
import net.freehal.core.util.RegexUtils;

public class GermanPredefinedAnswerProvider extends PredefinedAnswerProvider {

	@Override
	protected String tryGreeting(String input) {
		String output = null;
		if (RegexUtils.ifind(input, "(^|[,;.?!-~]|\\s)+" + "(hallo|hi|hey|mahlzeit|"
				+ "((gute|guten|schoene|schoenen)(.+)" + "(tag|morgen|abend|nachmittag|vormittag|nacht)))"
				+ "(\\s|[,;.?!-~]|$)+")) {
			GregorianCalendar cal = new GregorianCalendar();
			int hour = cal.get(Calendar.HOUR_OF_DAY)+1;
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
