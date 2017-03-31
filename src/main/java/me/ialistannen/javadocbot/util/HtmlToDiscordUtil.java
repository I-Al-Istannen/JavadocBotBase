package me.ialistannen.javadocbot.util;

import me.ialistannen.htmltodiscord.HtmlConverter;
import me.ialistannen.htmltodiscord.MapperCollection;
import me.ialistannen.htmltodiscord.StandardMappers;

/**
 * A util for converting HTML to discord
 *
 * @author jwachter
 */
public class HtmlToDiscordUtil {

  private static final HtmlToDiscordUtil instance = new HtmlToDiscordUtil();

  private final MapperCollection mapperCollection;

  private HtmlToDiscordUtil() {
    mapperCollection = new MapperCollection();

    for (StandardMappers mappers : StandardMappers.values()) {
      mapperCollection.addMapper(mappers);
    }
  }

  /**
   * Converts HTML to a more discord friendly format
   *
   * @param html The HTML to convert
   * @param baseUrl The base url to use for resolving links
   * @return The converted String
   */
  public static String convert(String html, String baseUrl) {
    HtmlConverter converter = new HtmlConverter(html, instance.mapperCollection);
    return converter.parse(baseUrl);
  }
}
