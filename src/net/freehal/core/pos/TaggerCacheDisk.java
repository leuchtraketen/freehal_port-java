package net.freehal.core.pos;

public class TaggerCacheDisk implements TaggerCache {

	@Override
	public TagContainer newContainer(String name) {
		return new TagMapDisk(name);
	}

}
