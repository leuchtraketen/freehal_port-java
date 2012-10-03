package net.freehal.core.pos;

import net.freehal.core.util.FreehalFile;

import net.freehal.core.util.FileUtils;
import net.freehal.core.util.FreehalConfig;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;

public abstract class TagContainerMemory implements TagContainer {

	public boolean add(final FreehalFile filename) {

		LogUtils.i("read part of speech file: " + filename);

		String word = null;
		Tags tags = null;
		int n = 0;
		Iterable<String> lines = FileUtils.readLines(FreehalConfig.getLanguageDirectory(), filename);
		for (String line : lines) {
			line = RegexUtils.trimRight(line, "\\s");

			if (line.endsWith(":")) {
				if (word != null && tags != null) {
					this.add(word, tags);
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
		if (word != null && tags != null) {
			this.add(word, tags);
		}

		LogUtils.i("\\r  " + n + " lines...          ");
		LogUtils.flush();

		return n > 0 ? true : false;
	}
}
