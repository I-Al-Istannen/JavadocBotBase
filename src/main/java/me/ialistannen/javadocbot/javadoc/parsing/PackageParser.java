package me.ialistannen.javadocbot.javadoc.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.ialistannen.javadocbot.javadoc.JavadocSettings;
import me.ialistannen.javadocbot.javadoc.model.Package;
import me.ialistannen.javadocbot.util.HtmlToDiscordUtil;
import me.ialistannen.javadocbot.util.JsoupUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A parser for the package
 */
public class PackageParser {

  private static final Pattern PACKAGE_PATTERN = Pattern.compile(
      "(.+)(?=/)",
      Pattern.CASE_INSENSITIVE
  );

  private JavadocSettings settings;

  /**
   * @param settings The {@link JavadocSettings} to pull the base url from
   */
  public PackageParser(JavadocSettings settings) {
    this.settings = settings;
  }

  /**
   * @param url The full link to the package
   * @return The parsed name
   * @throws IllegalArgumentException if no package name could be extracted
   */
  public String getNameFromLink(String url) {
    String link = url.replace(settings.getBaseUrl(), "");

    Matcher matcher = PACKAGE_PATTERN.matcher(link);
    if (matcher.find()) {
      return matcher.group(1).replace("/", ".");
    }

    throw new IllegalArgumentException("Couldn't extract a package from the link name: " + link);
  }

  /**
   * Parses the package description
   *
   * @param javadocPackage The package to get it for
   * @return The Description for the package as a discord markdown string. An empty string if it has
   * no description.
   */
  public String parseDescription(Package javadocPackage) {
    Document document = JsoupUtil.parseUrl(javadocPackage.getUrl());

    Elements nameAnchor = document.getElementsByAttributeValue("name", "package.description");
    if (nameAnchor.isEmpty()) {
      return "";
    }

    Element anchor = nameAnchor.get(0);
    Element block = JsoupUtil.findFirstMatching(element -> element.hasClass("block"), anchor);
    if (block == null) {
      throw new IllegalArgumentException("Couldn't find the block class for the description");
    }
    return HtmlToDiscordUtil
        .convert(block.html(), settings.getBaseUrl(), settings.isSilentlyIgnoreUnknownTags());
  }
}
