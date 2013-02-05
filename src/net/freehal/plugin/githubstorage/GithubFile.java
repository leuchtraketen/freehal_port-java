package net.freehal.plugin.githubstorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.freehal.core.util.FreehalFile;
import net.freehal.core.util.FreehalFileImpl;
import net.freehal.core.util.FreehalFiles;
import net.freehal.core.util.LogUtils;
import net.freehal.core.util.RegexUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a read-only file in a github repository.
 * 
 * @author "Tobias Schulz"
 */
public class GithubFile implements FreehalFileImpl {

	private String user;
	private String repo;
	private String path;

	private boolean isDirectory = false;
	private String[] files;

	private boolean isFile = false;

	public GithubFile(String str) {
		String[] parts = StringUtils.split(str, "/", 3);
		if (parts.length != 3)
			throw new IllegalArgumentException("Invalid Github path: " + str);
		user = parts[0];
		repo = parts[1];
		path = parts[2];

		download();
	}

	public GithubFile(String user, String repo, String path) {
		this.user = user;
		this.repo = repo;
		this.path = path;

		download();
	}

	public static FreehalFiles.Factory newFactory() {
		return new FreehalFiles.Factory() {
			@Override
			public FreehalFileImpl newInstance(String path) {
				return new GithubFile(path);
			}
		};
	}

	private void download() {
		if (getName().contains(".")) {
			isFile = true;
		} else {
			FreehalFile http = new FreehalFile("http", "http://github.com/" + user + "/" + repo
					+ "/tree/master/" + path);
			List<String> files = new ArrayList<String>();
			for (String line : http.readLines()) {
				List<String> matches;
				if ((matches = RegexUtils.match(line, "<td class=\"content\"><a href=\"/" + user + "/" + repo
						+ "/blob/master/" + path + "/(.*?)\"")) != null) {
					files.add(matches.get(0));
				}
			}
			LogUtils.i("github directory [" + this + "]: " + files);
			this.files = files.toArray(new String[] {});
			if (files.size() > 0) {
				isDirectory = true;
			} else {
				isFile = true;
			}
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRepo() {
		return repo;
	}

	public void setRepo(String repo) {
		this.repo = repo;
	}

	public void setPathInRepo(String path) {
		this.path = path;
	}

	public String getPathInRepo() {
		return path;
	}

	@Override
	public int compareTo(FreehalFileImpl arg0) {
		return getPath().compareTo(arg0.getPath());
	}

	@Override
	public FreehalFile getChild(String subpath) {
		return new FreehalFile(new GithubFile(user, repo, path + "/" + subpath));
	}

	@Override
	public FreehalFile getChild(FreehalFileImpl path) {
		if (path instanceof FreehalFile)
			return getChild(((FreehalFile) path).getImpl());
		else if (path instanceof GithubFile)
			return new FreehalFile(new GithubFile(user, repo, path + "/"
					+ ((GithubFile) path).getPathInRepo()));
		else
			return new FreehalFile(new GithubFile(user, repo, path + "/" + path.getPath()));
	}

	@Override
	public File getFile() {
		return null;
	}

	@Override
	public boolean isAbsolute() {
		return true;
	}

	@Override
	public String getAbsolutePath() {
		return getPath();
	}

	@Override
	public String getPath() {
		return StringUtils.join(new String[] { user, repo, path }, "/");
	}

	@Override
	public String getName() {
		String[] parts = StringUtils.split(path, "/");
		return parts[parts.length - 1];
	}

	@Override
	public boolean isFile() {
		return isFile;
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public FreehalFile[] listFiles() {
		List<FreehalFile> list = new ArrayList<FreehalFile>();
		for (String name : files) {
			list.add(getChild(name));
		}
		LogUtils.i("list files: " + list);
		return list.toArray(new FreehalFile[] {});
	}

	@Override
	public long length() {
		return getRawFile().length();
	}

	@Override
	public boolean mkdirs() {
		return false;
	}

	@Override
	public boolean delete() {
		return false;
	}

	private FreehalFile getRawFile() {
		return new FreehalFile("http", "https://raw.github.com/" + user + "/" + repo + "/master/" + path);
	}

	@Override
	public Iterable<String> readLines() {
		return getRawFile().readLines();
	}

	@Override
	public String read() {
		return getRawFile().read();
	}

	@Override
	public void append(String s) {}

	@Override
	public void write(String s) {}

	@Override
	public int countLines() {
		return 0;
	}

	@Override
	public void touch() {}

	@Override
	public String toString() {
		return "{github://" + user + "/" + repo + "/" + path + "}";
	}
}
