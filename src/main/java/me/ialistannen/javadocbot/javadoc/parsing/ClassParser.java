package me.ialistannen.javadocbot.javadoc.parsing;

import java.util.NoSuchElementException;
import me.ialistannen.javadocbot.javadoc.JavadocSettings;
import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.javadoc.model.Package;
import me.ialistannen.javadocbot.util.HtmlToDiscordUtil;
import me.ialistannen.javadocbot.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author jwachter
 */
public class ClassParser {

  private JavadocSettings settings;

  /**
   * @param settings The {@link JavadocSettings} to use
   */
  public ClassParser(JavadocSettings settings) {
    this.settings = settings;
  }

  /**
   * Parses a HTML element to a {@link JavadocClass}
   *
   * @param containingPackage The containing package
   * @param name The name of the class
   * @param fullUrl The full url
   * @return The parsed element
   */
  public JavadocClass parse(Package containingPackage, String name, String fullUrl) {
    return new JavadocClass(name, fullUrl, containingPackage, this);
  }

  /**
   * Parses the package description
   *
   * @param javadocClass The {@link JavadocClass} to get it for
   * @return The Description for the package as a discord markdown string
   */
  public String parseDescription(JavadocClass javadocClass) {
    Document document = JsoupUtil.parseUrl(javadocClass.getUrl());
    Elements description = document.select(".description > ul:nth-child(1) > li:nth-child(1)");
    Elements hrs = description.select("hr");
    if (hrs.isEmpty()) {
      throw new IllegalArgumentException("HR not found.");
    }
    Element sibling = hrs.get(0);

    // skip to "block" element
    while ((sibling = sibling.nextElementSibling()) != null) {
      if (sibling.hasClass("block")) {
        break;
      }
    }

    // concat the other elements
    StringBuilder total = new StringBuilder();

    while (sibling != null) {
      total.append(sibling.outerHtml());
      sibling = sibling.nextElementSibling();
    }

    return HtmlToDiscordUtil
        .convert(total.toString(), settings.getBaseUrl(), settings.isSilentlyIgnoreUnknownTags());
  }

  /**
   * Parses the class declaration
   *
   * @param javadocClass The {@link JavadocClass} to get it for
   * @return The declaration of the class as a discord markdown string
   */
  public String parseDeclaration(JavadocClass javadocClass) {
    Document document = JsoupUtil.parseUrl(javadocClass.getUrl());
    Elements description = document.select(".description > ul:nth-child(1) > li:nth-child(1)");
    Elements hrs = description.select("hr");
    if (hrs.isEmpty()) {
      throw new IllegalArgumentException("HR not found.");
    }
    Element sibling = hrs.get(0);

    // skip to "block" element
    while ((sibling = sibling.nextElementSibling()) != null) {
      if (sibling.tagName().equalsIgnoreCase("pre")) {
        return HtmlToDiscordUtil.convert(
            sibling.outerHtml(), settings.getBaseUrl(), settings.isSilentlyIgnoreUnknownTags()
        );
      }
    }
    throw new NoSuchElementException("Couldn't find the declaration");
  }
}
