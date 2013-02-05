package net.freehal.plugin.githubstorage;

import net.freehal.core.lang.Languages;
import net.freehal.core.storage.Storage;
import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.LogUtils;

/**
 * This class is for downloading missing files in an empty base directory from a
 * github directory. If there are no files missing, everything is delegated to
 * the underlying real storage.
 * 
 * @author "Tobias Schulz"
 */
public class GithubStorage implements Storage {

	private Storage storage;
	private boolean checked = false;
	private String prefix;

	/**
	 * The storage to store the downloaded files in.
	 * 
	 * @param storage
	 *        the real storage
	 */
	public GithubStorage(String prefix, Storage storage) {
		this.prefix = prefix;
		this.storage = storage;
	}

	private void check() {
		if (!checked) {
			checked = true;
			FreehalFile langdir = this.getLanguageDirectory();
			if (!containsFiles(langdir, ".xml")) {
				downloadTemplates(langdir);
			}
		}
	}

	private void downloadTemplates(FreehalFile langdir) {
		GithubFile dir = new GithubFile(prefix
				+ Languages.getCurrentLanguage().getCode());
		for (FreehalFile remote : dir.listFiles()) {
			LogUtils.i("remote file: " + remote);
			FreehalFile local = langdir.getChild(remote.getName());
			LogUtils.i("local file:  " + local);
			if (local.isFile())
				local.delete();
			for (String line : remote.readLines()) {
				local.append(line + net.freehal.core.util.StringUtils.NEWLINE);
			}
		}
	}

	private boolean containsFiles(FreehalFile dir, String suffix) {
		FreehalFile[] files = dir.listFiles();
		for (FreehalFile file : files) {
			if (file.getName().endsWith(suffix))
				return true;
		}
		return false;
	}

	@Override
	public FreehalFile getLanguageDirectory() {
		check();
		return storage.getLanguageDirectory();
	}

	@Override
	public FreehalFile getPath() {
		check();
		return storage.getPath();
	}

	@Override
	public FreehalFile getCacheDirectory() {
		check();
		return storage.getCacheDirectory();
	}

}
