package net.freehal.ui.shell;

import net.freehal.core.answer.AnswerProvider;
import net.freehal.core.parser.Sentence;

/**
 * This is just for testing the AnswerProvider API
 * 
 * @author tobias
 */
class FakeAnswerProvider implements AnswerProvider {

	@Override
	public String getAnswer(Sentence s) {
		return "Hello World!";
	}

}