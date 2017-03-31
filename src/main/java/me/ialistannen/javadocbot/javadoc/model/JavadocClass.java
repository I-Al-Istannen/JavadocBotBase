package me.ialistannen.javadocbot.javadoc.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ialistannen.javadocbot.javadoc.parsing.ClassParser;

/**
 * @author jwachter
 */
public class JavadocClass extends JavadocElement {

  private ClassParser classParser;
  private Package parentPackage;

  private String declaration;

  /**
   * @param name The name of the element
   * @param url The URL to the class
   * @param parentPackage The containing package
   * @param classParser The {@link ClassParser} to use
   */
  public JavadocClass(String name, String url, Package parentPackage, ClassParser classParser) {
    super(name, null, url);
    this.parentPackage = Objects.requireNonNull(parentPackage, "parentPackage can not be null!");
    this.classParser = Objects.requireNonNull(classParser, "classParser can not be null!");

    parentPackage.addClass(this);
  }

  @SuppressWarnings("Duplicates")
  @Override
  public String getDescription() {
    synchronized (this) {
      String description = super.getDescription();
      if (description != null) {
        return description;
      }
      description = classParser.parseDescription(this);

      setDescription(description);
      return description;
    }
  }

  /**
   * Returns the class declaration.
   *
   * May involve a blocking web request
   *
   * @return The class declaration
   */
  public String getDeclaration() {
    synchronized (this) {
      if (declaration != null) {
        return declaration;
      }
      declaration = classParser.parseDeclaration(this);

      return declaration;
    }

  }

  /**
   * @return The parent {@link Package}
   */
  public Package getParentPackage() {
    return parentPackage;
  }

  /**
   * Returns the class name with modifiers, but without other stuff
   * <p>
   * <em><strong>Annotations may be included</strong></em>
   *
   * @return The name with the modifiers, but not extends, implements and stuff
   */
  public String getNameWithModifiers() {
    return getDeclaration().replaceAll(" ?(extends|implements).+", "");
  }

  /**
   * Returns the superclasses and interfaces
   * <p>
   * <em><strong>Annotations <u>may</u> be included</strong></em>
   *
   * @return The "extends, implements and stuff" clause after a class name
   */
  public String getExtendsImplements() {
    Matcher matcher = Pattern.compile(" ?(extends|implements)([\\s\\S]+)")
        .matcher(getDeclaration());
    if (!matcher.find()) {
      return "";
    }
    return matcher.group(1) + matcher.group(2);
  }

  //@formatter:off
  /**
   * The type of the class
   * <p>
   * Valid types are
   * <ul>
   *     <li>interface</li>
   *     <li>class</li>
   *     <li>enum</li>
   *     <li>nothing?</li>
   * </ul>
   *
   * @return The type of the class
   */
  //@formatter:on
  public String getType() {
    String declaration = getDeclaration().toLowerCase();
    if (declaration.contains("interface")) {
      return "interface";
    } else if (declaration.contains("class")) {
      return "class";
    } else if (declaration.contains("enum")) {
      return "enum";
    }
    return "nothing?";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    JavadocClass that = (JavadocClass) o;
    return Objects.equals(parentPackage, that.parentPackage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), parentPackage);
  }
}
