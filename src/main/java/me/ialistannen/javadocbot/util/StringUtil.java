package me.ialistannen.javadocbot.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some utilities for Strings
 */
public class StringUtil {

  private static final Pattern BOLD = Pattern.compile("\\*\\*(.+)\\*\\*");      // **xXx**
  private static final Pattern ITALIC_ASTERISK = Pattern.compile("\\*(.+)\\*"); // *xXx*
  private static final Pattern UNDERLINE = Pattern.compile("___(.+)___");       // ___xXx___
  private static final Pattern ITALIC_UNDERSCORE = Pattern.compile("_(.+)_");   // _xXx_

  /**
   * Matches Markdown links in the
   */
  private static final Pattern LINK_PATTERN = Pattern.compile("\\[(.+?)]\\((.+?)\\)");

  /**
   * Strips formatting from the string
   *
   * @param string The string to strip formatting from
   * @return A mostly stripped version of it
   */
  public static String stripFormatting(String string) {
    String replaced = replaceBasic(string);

    return replaceAllOccurrences(LINK_PATTERN, replaced, matcher -> matcher.group(1));
  }

  /**
   * Replaces <b>bold</b>, <i>italic</i> and <u>underlined</u> text.
   *
   * @param string The string to strip formatting from
   * @return A mostly stripped version of it
   */
  private static String replaceBasic(String string) {
    // Order is important
    String replaced = replaceAllOccurrences(BOLD, string, matcher -> matcher.group(1));
    replaced = replaceAllOccurrences(ITALIC_ASTERISK, replaced, matcher -> matcher.group(1));
    replaced = replaceAllOccurrences(UNDERLINE, replaced, matcher -> matcher.group(1));
    replaced = replaceAllOccurrences(ITALIC_UNDERSCORE, replaced, matcher -> matcher.group(1));
    return replaced;
  }

  private static String replaceAllOccurrences(Pattern pattern, String string,
      Function<Matcher, String> replacement) {
    Matcher matcher = pattern.matcher(string);
    String replaced = string;
    while (matcher.find()) {
      replaced = matcher.replaceAll(replacement.apply(matcher));
      matcher = pattern.matcher(replaced);
    }

    return replaced;
  }
}
