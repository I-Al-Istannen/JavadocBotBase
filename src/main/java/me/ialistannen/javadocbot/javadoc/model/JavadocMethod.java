package me.ialistannen.javadocbot.javadoc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.ialistannen.javadocbot.javadoc.parsing.MethodParser;
import me.ialistannen.javadocbot.util.Pair;
import me.ialistannen.javadocbot.util.StringUtil;

/**
 * A Javadoc method
 */
public class JavadocMethod extends JavadocElement {

  private JavadocClass containingClass;
  private MethodParser methodParser;

  private String returnValue;
  private String declaration;
  private String shortDescription;

  private boolean inherited;

  /**
   * @param name The name of the element
   * @param url The URL to the element
   * @param returnValue The return value of the Method
   * @param declaration The declaration of the method
   * @param shortDescription A short description of the method
   * @param containingClass The {@link JavadocClass} this method is contained inside.
   * @param methodParser The {@link MethodParser}
   * @param inherited Whether the method is inherited.
   */
  public JavadocMethod(String name, String url,
      String returnValue, String declaration, String shortDescription,
      JavadocClass containingClass, MethodParser methodParser, boolean inherited) {
    super(name, null, url);
    this.methodParser = Objects.requireNonNull(methodParser, "methodParser can not be null!");
    this.declaration = Objects.requireNonNull(declaration, "declaration can not be null!");
    this.returnValue = Objects.requireNonNull(returnValue, "returnValue can not be null!");
    this.shortDescription = Objects
        .requireNonNull(shortDescription, "shortDescription can not be null!");
    this.containingClass = Objects
        .requireNonNull(containingClass, "containingClass can not be null!");
    this.inherited = inherited;
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
   * Returns the method declaration.
   *
   * <p>It has the form "{@code execute(CommandSender sender, String currentAlias, String[] args)}".
   * <br>As always it <em>will</em> already be markdown formatted. This is important, as links will
   * look different.
   *
   * <p>This method will <em>not</em> cause an additional web request, if {@link #isInherited()} is
   * false.
   *
   * @return The declaration of the method
   */
  @SuppressWarnings("WeakerAccess")
  public String getDeclaration() {
    return declaration;
  }

  /**
   * Returns the name of the method with the parameters (type and name).
   * <p>It has the following format: "{@code scalb(float f, int scaleFactor)}"
   *
   * <p>This method will <em>not</em> cause an additional web request, if {@link #isInherited()} is
   * false.
   *
   * @return The name of the method with parameters.
   */
  @SuppressWarnings("unused")
  public String getNameWithParameters() {
    String parameters = getParameters().stream()
        .map(entry -> entry.getKey() + " " + entry.getValue())
        .collect(Collectors.joining(", "));
    return getName() + "(" + parameters + ")";
  }

  /**
   * Returns the return value of the method.
   *
   * <p>This method will <em>not</em> cause an additional web request, if {@link #isInherited()} is
   * false.
   *
   * @return The return value
   */
  @SuppressWarnings("unused")
  public String getReturnValue() {
    return returnValue;
  }

  /**
   * Returns the (typically) first line of the javadoc of the method
   *
   * <p>This method will <em>not</em> cause an additional web request, if {@link #isInherited()} is
   * false.
   *
   * @return A short description of the method, if any
   */
  @SuppressWarnings("unused")
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * This methods returns the {@link JavadocClass} the method is defined inside.
   * <p>
   * This method will <em>not</em> cause an additional web request.
   *
   * @return The {@link JavadocClass} containing this {@link JavadocMethod}
   */
  public JavadocClass getContainingClass() {
    return containingClass;
  }

  /**
   * @return The declaration without the throws modifier
   */
  @SuppressWarnings("unused")
  public String getDeclarationWithoutExceptions() {
    return getDeclaration().replaceAll(" ?throws.+", "");
  }

  /**
   * @return The <em>checked</em> exceptions it may throw (declared using {@code throws})
   */
  @SuppressWarnings("unused")
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
   * Returns the parameters of the method <p> Format: <br><em>Key:</em> Type <br><em>Value:</em>
   * Name
   *
   * <p>This method will <em>not</em> cause an additional web request, if {@link #isInherited()} is
   * false.
   *
   * @return The method parameters. Empty if none
   */
  public List<Pair<String, String>> getParameters() {
    List<Pair<String, String>> parameters = new ArrayList<>(3);

    String declaration = StringUtil.stripFormatting(getDeclaration());
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
        parameters.add(new Pair<>(tmp, matcher.group(1)));
        tmp = null;
      }
    }

    return parameters;
  }

  /**
   * Checks if a method is inherited.
   * <p>Getting the short description or parameters for one involves a blocking web request.
   *
   * @return True if the method is inherited.
   */
  @SuppressWarnings({"WeakerAccess", "unused"})
  public boolean isInherited() {
    return inherited;
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
