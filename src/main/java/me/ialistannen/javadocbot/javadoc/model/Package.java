package me.ialistannen.javadocbot.javadoc.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import me.ialistannen.javadocbot.javadoc.parsing.PackageParser;

/**
 * A Javadoc Package
 *
 * @author jwachter
 */
public class Package extends JavadocElement {

  private PackageParser packageParser;
  private Collection<JavadocClass> classes;

  /**
   * @param name The name of the element
   * @param url The URL of the package
   * @param packageParser The {@link PackageParser} to use.
   */
  public Package(String name, String url, PackageParser packageParser) {
    super(name, null, url);
    this.packageParser = Objects.requireNonNull(packageParser, "packageParser can not be null!");
    classes = new HashSet<>();
  }

  /**
   * Adds a {@link JavadocClass} to the package
   *
   * @param javadocClass The {@link JavadocClass} to add
   */
  void addClass(JavadocClass javadocClass) {
    classes.add(javadocClass);
  }

  /**
   * @return All the classes in this package
   */
  @SuppressWarnings("unused")
  public Collection<JavadocClass> getClasses() {
    return Collections.unmodifiableCollection(classes);
  }

  /**
   * May involve a blocking web request
   *
   * @return The description of this package
   */
  @SuppressWarnings("Duplicates")
  @Override
  public String getDescription() {
    synchronized (this) {
      String description = super.getDescription();
      if (description != null) {
        return description;
      }
      description = packageParser.parseDescription(this);
      setDescription(description);
      return description;
    }
  }

  @Override
  public String toString() {
    return "Package{" +
        "classes=" + classes +
        ", name='" + getName() + '\'' +
        ", description='" + getDescription() + '\'' +
        ", url='" + getUrl() + '\'' +
        "} ";
  }
}
