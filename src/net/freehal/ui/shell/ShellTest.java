package net.freehal.ui.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.compat.sunjava.FileUtilsStandard;
import net.freehal.compat.sunjava.FreehalConfigStandard;
import net.freehal.compat.sunjava.LogUtilsStandard;
import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.answer.AnswerProviders;
import net.freehal.core.database.DatabaseAnswerProvider;
import net.freehal.core.database.FakeDatabase;
import net.freehal.core.filter.FactFilters;
import net.freehal.core.filter.FilterNoNames;
import net.freehal.core.filter.FilterNot;
import net.freehal.core.filter.FilterQuestionExtra;
import net.freehal.core.filter.FilterQuestionWhat;
import net.freehal.core.filter.FilterQuestionWho;
import net.freehal.core.grammar.Grammar2012;
import net.freehal.core.parser.AbstractParser;
import net.freehal.core.parser.NullParser;
import net.freehal.core.parser.Sentence;
import net.freehal.core.pos.FakeTagger;
import net.freehal.core.pos.Tagger2012;
import net.freehal.core.predefined.PredefinedAnswerProvider;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.StringUtils;

public class ShellTest {
	private static void init() {
		FileUtils.set(new FileUtilsStandard());
		LogUtils.set(new LogUtilsStandard());
		
		FreehalConfig.set(new FreehalConfigStandard().setLanguage("de")
				.setPath(new File("..")));
		
		FreehalConfig.setGrammar(new Grammar2012());
		FreehalConfig.getGrammar().readGrammar(new File("grammar.txt"));
		
		FreehalConfig.setTagger(new FakeTagger());
		
		FactFilters.getInstance().add(new FilterNot()).add(new FilterNoNames())
				.add(new FilterQuestionWho()).add(new FilterQuestionWhat())
				.add(new FilterQuestionExtra());

		AnswerProviders.getInstance().add(new PredefinedAnswerProvider())
				.add(new DatabaseAnswerProvider(new FakeDatabase()))
				.add(new FakeAnswerProvider());
	}

	public static void main(String[] args) {
		init();

		for (String input : args) {
			AbstractParser p = new NullParser(input);
			final List<Sentence> inputParts = p.getSentences();

			List<String> outputParts = new ArrayList<String>();
			for (Sentence s : inputParts) {
				outputParts.add(AnswerProviders.getInstance().getAnswer(s));
			}
			final String output = StringUtils.join(" ", outputParts);
			System.out.println("Input: " + input);
			System.out.println("Output: " + output);
		}
	}
}

/**
 * This is just for testing whether the AnswerProvider list works or not!
 * 
 * @author tobias
 */
class FakeAnswerProvider implements AnswerProvider {

	@Override
	public String getAnswer(Sentence s) {
		return "Hello World!";
	}

}