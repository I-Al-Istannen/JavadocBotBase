package me.ialistannen.javadocbot.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A util class for dealing with {@link org.jsoup.Jsoup}
 *
 * @author jwachter
 */
public class JsoupUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsoupUtil.class);

  /**
   * Parses an URL to a {@link Jsoup} {@link Documentstring}
   *
   * @param url The url
   * @return The parsed document
   * @throws RuntimeException if an error occurred
   */
  public static Document parseUrl(String url) {
    try {
      return Jsoup.parse(getUrl(url), 10000);
    } catch (IOException e) {
      LOGGER.warn("Couldn't connect to url", e);
      throw new RuntimeException("Error while connecting", e);
    }
  }

  /**
   * Finds the first matching element
   *
   * @param elementPredicate The predicate for the element
   * @param first The first element to find
   * @return The found element or null if not found
   */
  public static Element findFirstMatching(Predicate<Element> elementPredicate, Element first) {
    Element tmp = first;
    while (!elementPredicate.test(tmp)) {
      tmp = tmp.nextElementSibling();
      if (tmp == null) {
        return null;
      }
    }
    return tmp;
  }

  /**
   * Finds all Children matching the filter predicate
   *
   * @param filter The filter the children must match
   * @param parent The parent element
   * @return A List with all children {@link Element}s matching the filer
   */
  public static List<Element> findChildren(Predicate<Element> filter, Element parent) {
    return parent.children().stream().filter(filter).collect(Collectors.toList());
  }

  /**
   * Returns the HTML code for all elements in the list
   *
   * @param elements The elements to get the HTML for
   * @param toStringFunction The Function converting the {@link Element}s to Strings
   * @return The combined HTML
   */
  public static String toHtml(Collection<Element> elements,
      Function<Element, String> toStringFunction) {
    return elements.stream().map(toStringFunction)
        .collect(Collectors.joining());
  }

  private static URL getUrl(String stringUrl) {
    try {
      return new URL(stringUrl);
    } catch (MalformedURLException e) {
      LOGGER.warn("Not a valid url", e);
      throw new RuntimeException("Not a valid url", e);
    }
  }

}
