package me.ialistannen.javadocbot.javadoc;

/**
 * Contains Javadoc settings
 *
 * @author jwachter
 */
public class JavadocSettings {

  private String baseUrl = "https://docs.oracle.com/javase/8/docs/api/";
  private boolean silentlyIgnoreUnknownTags;

  /**
   * @return The current base url
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * Checks whether to silently ignore unknown tags
   *
   * @return Whether to silently ignore unknown tags
   */
  public boolean isSilentlyIgnoreUnknownTags() {
    return silentlyIgnoreUnknownTags;
  }

  /**
   * @param baseUrl The new base url for javadoc lookup
   * @return This instance
   */
  public JavadocSettings setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  /**
   * Sets whether unknown tags will be silently ignored.
   *
   * @param ignore Whether to silently ignore unknown tags
   * @return This {@link JavadocSettings}
   */
  public JavadocSettings setSilentlyIgnoreUnknownTags(boolean ignore) {
    this.silentlyIgnoreUnknownTags = ignore;

    return this;
  }
}
