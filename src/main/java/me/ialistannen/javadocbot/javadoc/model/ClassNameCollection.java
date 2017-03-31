package me.ialistannen.javadocbot.javadoc.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A list of Javadoc class name
 *
 * @author jwachter
 */
public class ClassNameCollection implements Iterable<Entry<String, Collection<String>>> {

  private Multimap<String, String> links;

  /**
   * Creates a new {@link ClassNameCollection} without any mappings
   */
  public ClassNameCollection() {
    this(HashMultimap.create());
  }

  /**
   * @param links The map with Name -> Link
   */
  public ClassNameCollection(Multimap<String, String> links) {
    this.links = HashMultimap.create(links);
  }

  /**
   * Finds a link to a class by the name of the class
   *
   * @param className The name of the class
   * @return The link to the class, if found.
   */
  public Collection<String> getLink(String className) {
    return Collections.unmodifiableCollection(links.get(className));
  }

  /**
   * Adds a class with its link to collection
   *
   * @param className The name of the class
   * @param link The link to the class
   */
  public void addLink(String className, String link) {
    links.put(className, link);
  }

  @Override
  public String toString() {
    return "ClassNameCollection{" +
        "links=" + links +
        '}';
  }

  @Override
  public Iterator<Entry<String, Collection<String>>> iterator() {
    return links.asMap().entrySet().iterator();
  }
}
