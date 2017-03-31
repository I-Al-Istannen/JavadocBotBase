package me.ialistannen.javadocbot.javadoc;

/**
 * Contains Javadoc settings
 *
 * @author jwachter
 */
public class JavadocSettings {

  private String baseUrl;

  /**
   * @return The current base url
   */
  public String getBaseUrl() {
    return baseUrl;
  }

  /**
   * @param baseUrl The new base url for javadoc lookup
   */
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
}
