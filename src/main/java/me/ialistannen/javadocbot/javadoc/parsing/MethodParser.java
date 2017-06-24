package me.ialistannen.javadocbot.javadoc.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.ialistannen.javadocbot.javadoc.JavadocManager;
import me.ialistannen.javadocbot.javadoc.JavadocSettings;
import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.javadoc.model.JavadocMethod;
import me.ialistannen.javadocbot.util.HtmlToDiscordUtil;
import me.ialistannen.javadocbot.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MethodParser {

  private JavadocSettings settings;
  private JavadocManager javadocManager;

  /**
   * @param settings The {@link JavadocSettings} to use
   * @param javadocManager The {@link JavadocManager} to use
   */
  public MethodParser(JavadocSettings settings, JavadocManager javadocManager) {
    this.settings = settings;
    this.javadocManager = javadocManager;
  }

  /**
   * Parses the whole description of a method
   *
   * @param javadocMethod The {@link JavadocMethod} to parse it for
   * @return The complete description of the method
   */
  public String parseDescription(JavadocMethod javadocMethod) {
    String url = javadocMethod.getUrl();
    Document document = JsoupUtil.parseUrl(url);
    String anchorname = url.split("#")[1];
    Element anchor = ensureExists(
        document.getElementsByAttributeValue("name", anchorname).get(0), "anchorName"
    );

    Element surroundingList = anchor.nextElementSibling();

    List<Element> content = new ArrayList<>();
    for (Element listItem : surroundingList.children()) {
      Elements children = listItem.children();
      // skip the heading and the <pre> element
      content.addAll(children.subList(2, children.size()));
    }

    String html = JsoupUtil.toHtml(content, Element::outerHtml);
    return HtmlToDiscordUtil.convert(
        html, javadocMethod.getContainingClass().getUrl(), settings.isSilentlyIgnoreUnknownTags()
    );
  }

  public List<JavadocMethod> getMethods(JavadocClass javadocClass) {
    Document document = JsoupUtil.parseUrl(javadocClass.getUrl());

    List<JavadocMethod> methods = new ArrayList<>();

    methods.addAll(getMethodsNoInherited(javadocClass, document, false));
    methods.addAll(getAllInheritedMethodsRecurse(document));

    return methods;
  }

  private List<JavadocMethod> getMethodsNoInherited(JavadocClass javadocClass, Document document,
      boolean inherited) {
    Elements anchors = document.getElementsByAttributeValue("name", "method.summary");
    if (anchors.isEmpty()) {
      throw new IllegalArgumentException("Couldn't find anchor");
    }
    Element table = JsoupUtil.findFirstMatching(
        element -> element.tagName().equalsIgnoreCase("table"),
        anchors.get(0)
    );

    if (table == null) {
      throw new NoSuchElementException("Couldn't find table");
    }

    List<JavadocMethod> methods = new ArrayList<>();
    for (Element row : table.getElementsByTag("tr")) {
      Element firstColumn = ensureExists(
          row.getElementsByClass("colFirst").get(0), "colFirst"
      );
      Element secondColumn = ensureExists(
          row.getElementsByClass("colLast").get(0), "colLast"
      );
      // skip header
      if (firstColumn.tagName().equalsIgnoreCase("th")) {
        continue;
      }
      String returnType = HtmlToDiscordUtil.convert(
          extractReturnType(firstColumn),
          javadocClass.getUrl(),
          settings.isSilentlyIgnoreUnknownTags()
      );
      String url = HtmlToDiscordUtil.convert(
          extractUrl(secondColumn),
          javadocClass.getUrl(),
          settings.isSilentlyIgnoreUnknownTags()
      );
      String declaration = HtmlToDiscordUtil.convert(
          extractFullDeclaration(secondColumn, url),
          javadocClass.getUrl(),
          settings.isSilentlyIgnoreUnknownTags()
      );
      String shortDescription = HtmlToDiscordUtil.convert(
          extractShortDescription(secondColumn),
          javadocClass.getUrl(),
          settings.isSilentlyIgnoreUnknownTags()
      );
      String name = extractName(secondColumn);

      JavadocMethod method = new JavadocMethod(
          name, url,
          returnType, declaration, shortDescription,
          javadocClass, this,
          inherited
      );

      methods.add(method);
    }
    return methods;
  }


  /**
   * @param firstColumn The <em>first</em> column in the table
   * @return The extracted return type
   */
  private String extractReturnType(Element firstColumn) {
    return firstColumn.html();
  }

  /**
   * @param secondColumn The <em>second</em> column
   * @param url The URL of the method
   * @return The full method declaration
   */
  private String extractFullDeclaration(Element secondColumn, String url) {
    Document document = secondColumn.ownerDocument();
    String anchorname = url.split("#")[1];

    Element anchor = ensureExists(
        document.getElementsByAttributeValue("name", anchorname).get(0), "anchorName"
    );
    Element element = anchor.nextElementSibling().child(0).children().get(1);
    return element.html();
  }

  /**
   * @param secondColumn The <em>second</em> column
   * @return The method url
   */
  private String extractUrl(Element secondColumn) {
    Element anchor = ensureExists(
        secondColumn.getElementsByClass("memberNameLink").get(0),
        "memberNameLink"
    );
    Element link = ensureExists(
        anchor.getElementsByTag("a").get(0),
        "link itself"
    );
    return link.absUrl("href");
  }

  /**
   * @param secondColumn The <em>second</em> column
   * @return A short description of the method
   */
  private String extractShortDescription(Element secondColumn) {
    List<Element> shortDescription = JsoupUtil
        .findChildren(element -> element.hasClass("block"), secondColumn);

    return JsoupUtil.toHtml(shortDescription, Element::html);
  }

  /**
   * @param secondColumn The <em>second</em> column
   * @return The name of the method
   */
  private String extractName(Element secondColumn) {
    Element memberNameLink = ensureExists(
        secondColumn.getElementsByClass("memberNameLink").get(0),
        "memberNameLink"
    );

    return memberNameLink.text();
  }

  private <T> T ensureExists(T value, String name) {
    if (value == null) {
      throw new NoSuchElementException(name + " was not found");
    }
    if (value instanceof Collection && ((Collection) value).isEmpty()) {
      throw new NoSuchElementException(name + " was not found");
    }
    return value;
  }

  private List<JavadocMethod> getAllInheritedMethodsRecurse(Document document) {
    List<JavadocClass> superClasses = getSuperclassAndInterfaceNames(document)
        .map(element -> javadocManager.getClassEndingIn(element))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    List<JavadocMethod> methods = new ArrayList<>(superClasses.size());

    for (JavadocClass javadocClass : superClasses) {
      Document superClassDocument = JsoupUtil.parseUrl(javadocClass.getUrl());
      List<JavadocMethod> inheritedMethods = getMethodsNoInherited(
          javadocClass,
          superClassDocument,
          true
      );
      Set<String> inheritedMethodNames = getInheritedMethodNames(document).stream()
          .map(Element::text)
          .collect(Collectors.toSet());

      methods.addAll(
          inheritedMethods.stream()
              .filter(method -> inheritedMethodNames.contains(method.getName()))
              .collect(Collectors.toList())
      );
    }

    return methods;
  }

  /**
   * Returns all classes this class inherits methods from.
   *
   * @param document The {@link Document} to get it from
   * @return A Stream with the names of all implemented Interfaces and an eventual superclass.
   */
  private Stream<String> getSuperclassAndInterfaceNames(Document document) {
    return document.getElementsByAttribute("name").stream()
        .map(element -> element.attr("name"))
        .filter(element -> element.startsWith("methods.inherited.from.class"))
        .map(element -> element.replace("methods.inherited.from.class.", ""));
  }

  /**
   * Returns the names of all inherited methods.
   *
   * @param document The {@link Document} to get it from
   * @return The names of all inherited methods
   */
  private List<Element> getInheritedMethodNames(Document document) {
    return document.getElementsByAttribute("name").stream()
        .filter(element -> element.attr("name").startsWith("methods.inherited.from.class"))
        .map(Element::siblingElements)
        .flatMap(Collection::stream)
        .filter(element -> element.tagName().equalsIgnoreCase("code"))
        .flatMap(element -> element.getElementsByTag("a").stream())
        .filter(element -> element.hasAttr("href"))
        .collect(Collectors.toList());
  }
}
