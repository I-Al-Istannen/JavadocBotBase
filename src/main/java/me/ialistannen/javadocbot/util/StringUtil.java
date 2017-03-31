package me.ialistannen.javadocbot.util;

import net.dv8tion.jda.core.MessageBuilder;

/**
 * Some String utility methods
 *
 * @author jwachter
 */
public class StringUtil {

  /**
   * Trims a String to a given size
   *
   * @param input The input String
   * @param max The max size
   * @return The trimmed String, or the original if it was small enough
   */
  public static String trimToSize(String input, int max) {
    if (input.length() < max) {
      return input;
    }
    return input.substring(0, max - 3) + "...";
  }

  /**
   * Strips all formatting from the String
   *
   * @param input The input String
   * @return The String without any formatting
   */
  public static String stripFormatting(String input) {
    String strippedContent = new MessageBuilder().append(input).build().getStrippedContent();
    return strippedContent.replaceAll("\\[(.+?)]\\(.+?\\)", "$1");
  }
}
