package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An utility class for holding the currently used {@link FactProvider}s.
 * 
 * @author "Tobias Schulz"
 */
public class FactProviders {

	private static CompositeFactProvider provider = null;

	static {
		provider = new CompositeFactProvider();
	}

	private FactProviders() {}

	/**
	 * Get the currently used synonym provider.
	 * 
	 * @return an instance of a class implementing the {@link SynonymProvider}
	 *         interface
	 */
	public static FactProvider getFactProvider() {
		return provider;
	}

	/**
	 * Set the synonym provider to use; run at the beginning of your code!
	 * 
	 * @param provider
	 *        the synonym provider to set
	 */
	public static void addFactProvider(FactProvider provider) {
		FactProviders.provider.add(provider);
	}

	private static class CompositeFactProvider implements FactProvider {

		List<FactProvider> providers = new ArrayList<FactProvider>();

		@Override
		public Set<XmlFact> findFacts(List<Word> words) {
			Set<XmlFact> set = new HashSet<XmlFact>();
			for (FactProvider provider : providers) {
				Set<XmlFact> result = provider.findFacts(words);
				if (result != null)
					set.addAll(result);
			}
			return set;
		}

		public void add(FactProvider provider) {
			providers.add(provider);
		}
	}
}
