package net.freehal.core.pos;


public class TaggerCacheMemory implements TaggerCache {

	@Override
	public TagContainer newContainer(String string) {
		return new TagMap();
	}

}
