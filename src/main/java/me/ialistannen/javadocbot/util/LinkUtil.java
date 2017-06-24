package me.ialistannen.javadocbot.util;

/**
 * A small util for links
 */
public class LinkUtil {

  /**
   * @param first The first link
   * @param second The second link
   * @return A concatenated link, with a slash between the two. ONE slash. All other are trimmed
   */
  public static String concatLinksIgnoreDoubleSlash(String first, String second) {
    if (!first.endsWith("/") && !second.startsWith("/")) {
      return first + "/" + second;
    }
    if (first.endsWith("/") && second.startsWith("/")) {
      return first + second.substring(1);
    }

    return first + second;
  }
}
