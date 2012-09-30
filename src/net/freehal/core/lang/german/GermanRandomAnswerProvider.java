package net.freehal.core.lang.german;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.freehal.core.parser.Sentence;
import net.freehal.core.predefined.RandomAnswerProvider;
import net.freehal.core.util.RegexUtils;

public class GermanRandomAnswerProvider extends RandomAnswerProvider {

	@Override
	protected String getRandomQuestion(Sentence s, String input) {
		Random random = new Random();
		List<String> outputs = new ArrayList<String>();

		if (RegexUtils.ifind(" " + input + " ", "((\\sja\\s)|(\\snein\\s)|(\\swas\\s)|(\\swer\\s)|"
				+ "isunknown|(\\swie\\s)|(\\swo\\s)|(\\swann\\s)|" + "(\\swen\\s)|(\\swem\\s)|(\\swelch))")) {

			outputs.add("Da kann ich dir leider keine Antwort geben.");
			outputs.add("Keine Ahnung.");
			outputs.add("Keine Ahnung, das kann ich dir nicht sagen.");
			outputs.add("Keine Ahnung, das kann ich nicht sagen.");
			outputs.add("Tut mir leid, das weiss ich nicht.");
			outputs.add("Sorry, Keine Ahnung.");
			outputs.add("Tut mir leid, das kann ich nicht sagen.");
			outputs.add("Hm, das kann ich nicht sagen.");
			outputs.add("Hm, das kann ich dir nicht sagen.");
			outputs.add("Das weiss ich nicht.");
			outputs.add("Ich weiss das nicht, kannst du es mir sagen?");
			outputs.add("Wenn ich das wuesste...");
			outputs.add("Ich stehe gerade auf dem Schlauch.");
			outputs.add("Ich stehe wohl gerade auf dem Schlauch.");
			outputs.add("Diese Eingabe kann ich nicht verarbeiten.");
			outputs.add("Auf diese Eingabe kann ich nicht antworten.");
			outputs.add("Darauf kann ich noch nicht antworten.");
			outputs.add("Das kann ich leider nicht beantworten.");
			outputs.add("Fuer diese Eingabe fehlt mir das notwendige Wissen.");
			outputs.add("Auf alles habe ich leider keine Antwort...");
			outputs.add("Die Antwort auf diese Eingabe ist mir unbekannt.");
			outputs.add("Zur Zeit kann ich diese Eingabe nicht beantworten.");
			outputs.add("Momentan ist es mir nicht moeglich diese Eingabe zu beantworten.");
			outputs.add("Mein Wissensspeicher ist begrenzt, diese Eingabe kann ich nicht verarbeiten.");
			outputs.add("Fuer diese Eingabe ist keine Antwort in meinen Daten vorhanden.");
			outputs.add("Tut mir leid, aber auf diese Eingabe habe ich keine korrekte Antwort. ");
			outputs.add("Das kann ich momentan noch nicht beantworten.");
			outputs.add("Fuer diese Eingabe fehlt mir momentan das notwendige Wissen, um diese genau zu beantworten.");
			outputs.add("Bitte nicht boese sein, aber auf diese Eingabe habe ich keine Antwort.");
		}

		else if (RegexUtils.ifind(" " + input + " ", "((\\sja\\s)|(\\snein\\s))")) {
			outputs.add("Wieso nicht?");
			outputs.add("Ja.");
			outputs.add("Ja, klar.");
			outputs.add("Ja klar.");
			outputs.add("Natuerlich.");
		}

		else if (RegexUtils.ifind(" " + input + " ", "((\\swarum\\s)|(\\swieso\\s)|(\\swes))")) {
			outputs.add("Wieso nicht?");
			outputs.add("Keine Ahnung.");
			outputs.add("Keine Ahnung, das kann ich dir nicht sagen.");
			outputs.add("Keine Ahnung, das kann ich nicht sagen.");
			outputs.add("Hm, das kann ich nicht sagen.");
			outputs.add("Hm, das kann ich dir nicht sagen.");
			outputs.add("Das weiss ich nicht.");
			outputs.add("Ich weiss das nicht, kannst du es mir sagen?");
		}

		else {
			for (int i = 0; i < 20; ++i)
				outputs.add("Nein.");
			for (int i = 0; i < 5; ++i)
				outputs.add("Nein!");
			for (int i = 0; i < 5; ++i)
				outputs.add("Nein.,.");
			if (RegexUtils.ifind(" " + input + " ", "(\\shast\\s)")) {
				outputs.add("Nein, habe ich leider verliehen.");
				outputs.add("Nein, ich bin nuechtern.");
				outputs.add("Nein, ich habe eine Menge Freunde.");
				outputs.add("Nein, ich habe viele Freunde und Bekannte.");
			}
			if (RegexUtils.ifind(" " + input + " ", "(\\smagst\\s)")) {
				outputs.add("Nein, ich mag das nicht.");
			}
			outputs.add("Nein eigentlich so gut wie nie.");
			outputs.add("Nein, ganz im Gegenteil.");
			outputs.add("Nein, ganz und gar nicht.");
			outputs.add("Nein, keinesfalls.");
			outputs.add("Nein, ich sehe keinen Grund dazu .");
			outputs.add("Nein, ich vertreibe mir mit chatten die Langeweile.");
			outputs.add("Nein, tu ich sicher nicht.");
			outputs.add("Nein, nicht direkt, darueber will ich nicht reden.");
			outputs.add("Nein, nur ganz selten.");
			outputs.add("Nicht falsch verstehen, aber ich denke nicht das dich das etwas angeht.");
			if (RegexUtils.ifind(" " + input + " ", "(\\sviel\\s)")) {
				outputs.add("Nicht gerade wenig.");
				outputs.add("Nicht mehr und nicht weniger als andere.");
			}
			if (RegexUtils.ifind(" " + input + " ", "(\\sfeig)")) {
				outputs.add("Nicht feige, nur vorsichtig.");
			}
		}

		for (int i = 0; i < random.nextInt(5); ++i)
			Collections.shuffle(outputs, random);
		String output = outputs.get(0);

		return output;
	}

	@Override
	protected String getRandomStatement(Sentence s, String input) {
		Random random = new Random();
		List<String> outputs = new ArrayList<String>();

		outputs.add("Ich habe dich leider nicht verstanden.");
		outputs.add("Hmm.");
		outputs.add("Ah.");
		outputs.add("Aha.");
		outputs.add("Hmm.");
		outputs.add("Gut.");
		outputs.add("Ja.");
		outputs.add("Oh.");
		outputs.add("Klar.");

		for (int i = 0; i < random.nextInt(5); ++i)
			Collections.shuffle(outputs, random);
		String output = outputs.get(0);

		return output;
	}
}
