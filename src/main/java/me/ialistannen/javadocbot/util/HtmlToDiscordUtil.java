package me.ialistannen.javadocbot.util;

import me.ialistannen.htmltodiscord.HtmlConverter;
import me.ialistannen.htmltodiscord.MapperCollection;
import me.ialistannen.htmltodiscord.StandardMappers;

/**
 * A util for converting HTML to discord
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
   * Converts HTML to a more discord friendly format.
   * <p>
   * Throws an exception if a tag is not known.
   *
   * @param html The HTML to convert
   * @param baseUrl The base url to use for resolving links
   * @return The converted String
   * @see #convert(String, String, boolean)
   * @deprecated Use {@link #convert(String, String, boolean)} and decide yourself.
   */
  @Deprecated
  public static String convert(String html, String baseUrl) {
    return convert(html, baseUrl, false);
  }

  /**
   * Converts HTML to a more discord friendly format
   *
   * @param html The HTML to convert
   * @param baseUrl The base url to use for resolving links
   * @param silentlyIgnoreUnknownTags Whether to silently ignore unknown tags
   * @return The converted String
   */
  public static String convert(String html, String baseUrl, boolean silentlyIgnoreUnknownTags) {
    HtmlConverter converter = new HtmlConverter(html, instance.mapperCollection)
        .setSilentlyIgnoreUnknownTags(silentlyIgnoreUnknownTags);
    return converter.parse(baseUrl);
  }
}
