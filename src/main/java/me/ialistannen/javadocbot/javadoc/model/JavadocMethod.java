package me.ialistannen.javadocbot.javadoc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ialistannen.javadocbot.javadoc.parsing.MethodParser;

/**
 * A Javadoc method
 *
 * @author jwachter
 */
public class JavadocMethod extends JavadocElement {

  private JavadocClass containingClass;
  private MethodParser methodParser;

  private String returnValue;
  private String declaration;
  private String shortDescription;

  /**
   * @param name The name of the element
   * @param url The URL to the element
   * @param returnValue The return value of the Method
   * @param declaration The declaration of the method
   * @param shortDescription A short description of the method
   * @param methodParser The {@link MethodParser}
   */
  public JavadocMethod(String name, String url,
      String returnValue, String declaration, String shortDescription,
      JavadocClass containingClass, MethodParser methodParser) {
    super(name, null, url);
    this.methodParser = Objects.requireNonNull(methodParser, "methodParser can not be null!");
    this.declaration = Objects.requireNonNull(declaration, "declaration can not be null!");
    this.returnValue = Objects.requireNonNull(returnValue, "returnValue can not be null!");
    this.shortDescription = Objects
        .requireNonNull(shortDescription, "shortDescription can not be null!");
    this.containingClass = Objects
        .requireNonNull(containingClass, "containingClass can not be null!");
  }

  @SuppressWarnings("Duplicates")
  @Override
  public String getDescription() {
    synchronized (this) {
      String description = super.getDescription();
      if (description != null) {
        return description;
      }
      description = methodParser.parseDescription(this);

      setDescription(description);
      return description;
    }
  }

  /**
   * @return The declaration of the method
   */
  public String getDeclaration() {
    return declaration;
  }

  /**
   * @return The return value
   */
  public String getReturnValue() {
    return returnValue;
  }

  /**
   * @return A short descriütion of the method, if any
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * @return The {@link JavadocClass} containing this {@link JavadocMethod}
   */
  public JavadocClass getContainingClass() {
    return containingClass;
  }

  /**
   * @return The declaration without the throws modifier
   */
  public String getDeclarationWithoutExceptions() {
    return getDeclaration().replaceAll(" ?throws.+", "");
  }

  /**
   * @return The exceptions it may throw
   */
  public String getExceptions() {
    Matcher matcher = Pattern.compile(
        "( ?throws .+)",
        Pattern.CASE_INSENSITIVE
    ).matcher(getDeclaration());

    if (!matcher.find()) {
      return "";
    }

    return matcher.group(1);
  }

  /**
   * Returns the parameters of the method
   * <p>
   * Format:
   * <br><em>Key:</em> Type
   * <br><em>Value:</em> Name
   *
   * @return The method parameters. Empty if none
   */
  public Map<String, String> getParameters() {
    Map<String, String> map = new HashMap<>();

    String declaration = getDeclaration();
    declaration = declaration.substring(declaration.indexOf("(") + 1);
    declaration = declaration.substring(0, declaration.indexOf(")"));

    // Non-Breaking-Space hell
    declaration = declaration.replace("\u00A0", " ");

    Matcher matcher = Pattern.compile("([^\\s,]+)").matcher(declaration);

    String tmp = null;
    while (matcher.find()) {
      if (tmp == null) {
        tmp = matcher.group(1);
      } else {
        map.put(tmp, matcher.group(1));
        tmp = null;
      }
    }

    return map;
  }

  @Override
  public String toString() {
    return "JavadocMethod{" +
        "methodParser=" + methodParser +
        ", returnValue='" + returnValue + '\'' +
        ", declaration='" + declaration + '\'' +
        ", shortDescription='" + shortDescription + '\'' +
        ", containingClass='" + containingClass.getName() + '\'' +
        ", name='" + getName() + '\'' +
        ", url='" + getUrl() + '\'' +
        "} " + super.toString();
  }
}
