package me.ialistannen.javadocbot.javadoc;

/**
 * Contains Javadoc settings
 *
 * @author jwachter
 */
public class JavadocSettings {

  private String baseUrl = "https://docs.oracle.com/javase/8/docs/api/";

  /**
   * @return The current base url
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * @param baseUrl The new base url for javadoc lookup
   */
  public JavadocSettings setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }
}
