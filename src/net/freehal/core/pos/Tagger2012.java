package net.freehal.core.pos;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.Mutable;
import net.freehal.core.util.RegexUtils;
import net.freehal.core.util.StringUtils;
import net.freehal.core.xml.Word;

public abstract class Tagger2012 implements AbstractTagger {

	private TagContainer staticTags = new TagMap();
	private TagContainer regexTags = new TagList();
	private Map<String, String> togglemap = new HashMap<String, String>();

	public Tagger2012(TaggerCache storage) {
		staticTags = storage.newContainer("staticTags");
	}

	@SuppressWarnings("unused")
	private Tagger2012() {
	}

	@Override
	public Tags getPartOfSpeech(final String _word) {
		String word = StringUtils.toAscii(_word);
		if (word.length() == 0) {
			return null;
		}
		LogUtils.i("get part of speech: " + word);

		if (word.contains("?"))
			throw new IllegalArgumentException("");

		Tags tags = null;

		if (tags == null)
			tags = this.getStaticTags(word);
		if (tags == null)
			tags = this.getStaticTags(StringUtils.ucfirst(word));
		if (tags == null)
			tags = this.getStaticTags(word.toLowerCase());

		if (tags == null) {
			if (word.equals(",") || word.equals(";")) {
				tags = new Tags("komma", null);
				LogUtils.i("  builtin: " + tags);
			}
			List<String> matched = null;
			if ((matched = RegexUtils.imatch(word, "[{]{3}(.*?)[}]{3}")) != null) {
				tags = new Tags(matched.get(0), null);
				LogUtils.i("  predefined: " + tags);
			}
		}
		if (tags == null) {
			if (this.isName(word)) {
				tags = new Tags("n", null);
				LogUtils.i("  is name: " + tags);
			}
		}

		if (tags == null)
			tags = this.getRegexTags(word);

		if (tags == null) {
			if (RegexUtils.find(word, "[ABCDEFGHIJKLMNOPQRSTUVWXYZ]")) {
				tags = new Tags("n", null);
				LogUtils.i("  is upper case: " + tags);
			}
		}

		if (tags == null)
			tags = guess(word);
		if (tags == null)
			tags = askUser(word);

		if (tags == null)
			LogUtils.i("  not found: " + tags);

		return tags;
	}

	private Tags askUser(String word) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	private Tags guess(String word) {
		// TODO Automatisch generierter Methodenstub
		return null;
	}

	@Override
	public Word toggle(Word word) {
		if (togglemap.containsKey(word.getWord())) {
			return new Word(togglemap.get(word.getWord()), word.getTags());
		}

		if (!word.hasTags()) {
			word.setTags(this);
		}

		if (word.hasTags() && word.getTags().isType("v")) {

			final String previousWord = word.getWord();
			String newWord = word.getWord();

			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "([gdm])elst$",
						"\\1le");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "([gdm])le$",
						"\\1elst");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "te$", "test");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "test$", "te");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "sst$", "sse");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "est$", "e");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "st$", "e");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "[td]e$", "\\1est");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "e$", "st");
			if (newWord.equals(previousWord))
				newWord = RegexUtils.replace(previousWord, "sss", "ss");

			if (newWord.equals(previousWord))
				return word;
			else
				return new Word(newWord, word.getTags());
		} else
			return word;
	}

	/**
	 * To be implemented in a language-specific tagger class!
	 */
	public abstract boolean isName(final String word);

	public static void writeTagsTo(File filename, Word word) {
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

	private void readFrom(File filename, Mutable<TagContainer> container) {
		String word = null;
		Tags tags = null;
		int n = 0;

		LogUtils.i("read part of speech file: " + filename);
		container.get().add(filename);

		List<String> lines = FileUtils.readLines(
				FreehalConfig.getLanguageDirectory(), filename);
		lines.add(":");
		for (String line : lines) {
			line = RegexUtils.trimRight(line, "\\s");

			if (line.endsWith(":")) {
				if (word != null && tags != null) {
					container.get().add(word, tags);
					word = null;
					tags = null;
				}

				line = RegexUtils.trim(line, ":\\s");
				word = line;
			} else if (line.startsWith(" ")) {
				line = RegexUtils.trim(line, ":,;\\s");
				if (line.startsWith("type")) {
					line = line.substring(4);
					line = RegexUtils.trimLeft(line, ":\\s");
					line = Tags.getUniqueType(line);
					tags = new Tags(tags, line, null, word);
				} else if (line.startsWith("genus")) {
					line = line.substring(5);
					line = RegexUtils.trimLeft(line, ":\\s");
					tags = new Tags(tags, null, line, word);
				}
			}

			if (++n % 10000 == 0) {
				LogUtils.i("\\r  " + n + " lines...          ");
				LogUtils.flush();
			}
		}

		LogUtils.i("\\r  " + n + " lines...          ");
		LogUtils.flush();
	}

	@Override
	public void readTagsFrom(File filename) {
		readFrom(filename, new Mutable<TagContainer>(staticTags));
	}

	@Override
	public void readRegexFrom(File filename) {
		readFrom(filename, new Mutable<TagContainer>(regexTags));
	}

	@Override
	public void readToggleWordsFrom(File filename) {
		LogUtils.i("read verbs file: " + filename);

		List<String> lines = FileUtils.readLines(
				FreehalConfig.getLanguageDirectory(), filename);

		for (String line : lines) {
			line = line.trim();
			String[] verbs = line.split("[,]+");
			if (verbs.length >= 2) {
				togglemap.put(verbs[0], verbs[1]);
				togglemap.put(verbs[1], verbs[0]);
			}
		}
	}

	private Tags getStaticTags(final String word) {
		if (staticTags.containsKey(word))
			return staticTags.get(word);
		else
			return null;
	}

	private Tags getRegexTags(final String word) {

		for (Map.Entry<String, Tags> entry : regexTags) {
			final String regex = entry.getKey();
			final Tags tags = entry.getValue();

			if (RegexUtils.ifind(word, regex)) {
				return tags;
			}
		}
		return null;
	}
}
