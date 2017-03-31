package me.ialistannen.javadocbot.javadoc.parsing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.ialistannen.javadocbot.javadoc.model.ClassNameCollection;
import me.ialistannen.javadocbot.util.JsoupUtil;
import me.ialistannen.javadocbot.util.LinkUtil;
import me.ialistannen.javadocbot.util.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the {@code all-classes} page in order to build an index
 *
 * @author jwachter
 */
public class AllClassParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(AllClassParser.class);
  private static final String APPENDIX = "/allclasses-noframe.html";

  /**
   * Parses the given URL and returns all classes found in the javadoc
   *
   * @param baseUrl The base URL for the javadoc
   * @return All class names with their link
   */
  public ClassNameCollection parse(String baseUrl) {
    Document document = JsoupUtil
        .parseUrl(LinkUtil.concatLinksIgnoreDoubleSlash(baseUrl, APPENDIX));

    Multimap<String, String> map = document.getElementsByTag("li")
        .stream()
        .filter(element -> element.childNodeSize() > 0)
        .filter(element -> element.child(0).tagName().equalsIgnoreCase("a"))
        .map(element -> element.child(0))
        .map(this::parseListItem)
        .collect(HashMultimap::create,
            (multimap, pair) -> multimap.put(pair.getKey(), pair.getValue()),
            Multimap::putAll
        );

    return new ClassNameCollection(map);
  }

  private Pair<String, String> parseListItem(Element element) {
    return new Pair<>(element.text(), element.absUrl("href"));
  }
}
