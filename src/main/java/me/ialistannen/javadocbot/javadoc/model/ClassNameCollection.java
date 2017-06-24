package me.ialistannen.javadocbot.javadoc.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

/**
 * A list of Javadoc class names.
 */
public class ClassNameCollection implements Iterable<Entry<String, Collection<String>>> {

  private Multimap<String, String> links;

  /**
   * @param links The map with Name {@code ->} Link
   */
  public ClassNameCollection(Multimap<String, String> links) {
    this.links = HashMultimap.create(links);
  }

  @Override
  public String toString() {
    return "ClassNameCollection{" +
        "links=" + links +
        '}';
  }

  @Override
  @Nonnull
  public Iterator<Entry<String, Collection<String>>> iterator() {
    return links.asMap().entrySet().iterator();
  }
}
