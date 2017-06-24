package me.ialistannen.javadocbot.javadoc.model;

import java.util.Objects;

/**
 * A Javadoc Element
 */
public abstract class JavadocElement {

  private String name;
  private String description;
  private String url;

  /**
   * @param name The name of the element
   * @param description The description of the element
   * @param url The URL to the element
   */
  JavadocElement(String name, String description, String url) {
    this.name = name;
    this.description = description;
    this.url = url;
  }

  /**
   * @return The name of the element
   */
  public String getName() {
    return name;
  }

  /**
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return The URL to the element
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param name The new name
   */
  protected void setName(String name) {
    this.name = name;
  }

  /**
   * @param description The new description
   */
  void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JavadocElement that = (JavadocElement) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(description, that.description) &&
        Objects.equals(url, that.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, url);
  }

  @Override
  public String toString() {
    return "JavadocElement{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", url='" + url + '\'' +
        '}';
  }
}
