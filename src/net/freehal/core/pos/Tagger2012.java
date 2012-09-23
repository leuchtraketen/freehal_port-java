package net.freehal.core.pos;

import java.io.File;

import net.freehal.core.typedefs.TagList;
import net.freehal.core.typedefs.TagMap;
import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.xml.Word;

public class Tagger2012 implements AbstractTagger {

	private TagMap type = new TagMap();
	private TagMap genus = new TagMap();
	private TagList regex_type = new TagList();
	private TagMap regex_genus = new TagMap();
	private TagMap togglemap = new TagMap();

	@Override
	public Tags getPartOfSpeech(String word) {
		return new Tags("n", "", word);
	}

	@Override
	public boolean isName(String word) {
		return false;
	}

	public static void writeTo(File filename, Word word) {
		if (word.hasTags()) {
			StringBuilder toAppend = new StringBuilder();

			toAppend.append(word.getWord()).append(":\n");
			toAppend.append(word).append(":\n");
			toAppend.append("  type: ").append(word.getTags().getType())
					.append("\n");
			toAppend.append("  genus: ").append(word.getTags().getGenus())
					.append("\n");

			LogUtils.i("write part of speech file: " + filename);
			LogUtils.i(toAppend.toString());

			FileUtils.append(FreehalConfig.getLanguageDirectory(), filename,
					toAppend.toString());
		}
	}
}
