package net.freehal.core.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.freehal.core.lang.ImplicitLanguageImplMap;
import net.freehal.core.lang.LanguageImplMap;
import net.freehal.core.util.Factory;

/**
 * An utility class for holding the currently used {@link FactProvider}s.
 * 
 * @author "Tobias Schulz"
 */
public class FactProviders {

	private static LanguageImplMap<CompositeFactProvider> provider = new ImplicitLanguageImplMap<CompositeFactProvider>(
			new Factory<CompositeFactProvider>() {
				@Override
				public CompositeFactProvider newInstance(String... params) {
					return new CompositeFactProvider();
				}
			});

	private FactProviders() {}

	/**
	 * Get the currently used synonym provider.
	 * 
	 * @return an instance of a class implementing the {@link SynonymProvider}
	 *         interface
	 */
	public static FactProvider getFactProvider() {
		return provider.getCurrent();
	}

	/**
	 * Set the synonym provider to use; run at the beginning of your code!
	 * 
	 * @param provider
	 *        the synonym provider to set
	 */
	public static void addFactProvider(FactProvider provider) {
		if (!FactProviders.provider.hasCurrent()) {
			FactProviders.provider.setCurrent(new CompositeFactProvider());
		}
		FactProviders.provider.getCurrent().add(provider);
	}

	public static class CompositeFactProvider implements FactProvider {

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
