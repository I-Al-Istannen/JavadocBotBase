package me.ialistannen.javadocbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some utilities for Strings
 */
public class StringUtil {

  private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*");       // **xXx**
  private static final Pattern ITALIC_ASTERISK = Pattern.compile("\\*(.+?)\\*");  // *xXx*
  private static final Pattern UNDERLINE = Pattern.compile("__(.+?)__");          // __xXx__
  private static final Pattern ITALIC_UNDERSCORE = Pattern.compile("_(.+?)_");    // _xXx_
  private static final Pattern INLINE_CODE = Pattern.compile("`(.+?)`");          // `xXx`

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

    return replaceAllOccurrencesWithFirstGroup(LINK_PATTERN, replaced);
  }

  /**
   * Replaces <b>bold</b>, <i>italic</i> and <u>underlined</u> text.
   *
   * @param string The string to strip formatting from
   * @return A mostly stripped version of it
   */
  private static String replaceBasic(String string) {
    // Order is important
    String replaced = replaceAllOccurrencesWithFirstGroup(BOLD, string);
    replaced = replaceAllOccurrencesWithFirstGroup(ITALIC_ASTERISK, replaced);
    replaced = replaceAllOccurrencesWithFirstGroup(UNDERLINE, replaced);
    replaced = replaceAllOccurrencesWithFirstGroup(ITALIC_UNDERSCORE, replaced);
    replaced = replaceAllOccurrencesWithFirstGroup(INLINE_CODE, replaced);
    return replaced;
  }

  private static String replaceAllOccurrencesWithFirstGroup(Pattern pattern, String string) {
    Matcher matcher = pattern.matcher(string);
    return matcher.replaceAll("$1");
  }
}
