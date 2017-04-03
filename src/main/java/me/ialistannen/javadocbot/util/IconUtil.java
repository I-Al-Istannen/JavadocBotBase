package me.ialistannen.javadocbot.util;

import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.javadoc.model.JavadocMethod;

public class IconUtil {

  /**
   * Gets the icon URL for a specified java class.
   * <p>
   * Distinguishes between different types of classes, e.g. Enums or Interfaces.
   * <br>Shamelessly links jetbrains icons.
   *
   * @param javadocClass The java class.
   * @return The icon url for the specific class
   */
  public static String getIconUrlForClass(JavadocClass javadocClass) {
    if (javadocClass.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2017.1/classTypeAbstract.png";
    } else if (javadocClass.getType().equalsIgnoreCase("interface")) {
      return "https://www.jetbrains.com/help/img/idea/2017.1/classTypeInterface.png";
    } else if (javadocClass.getType().equalsIgnoreCase("enum")) {
      return "https://www.jetbrains.com/help/img/idea/2017.1/classTypeEnum.png";
    } else if (javadocClass.getDeclaration().contains("final")) {
      return "https://www.jetbrains.com/help/img/idea/2017.1/classTypeFinal.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2017.1/classTypeJavaClass.png";
  }

  /**
   * Gets the icon URL for a specified java method.
   * <p>
   * Distinguishes abstract and non abstract methods.
   * <br>Shamelessly links jetbrains icons.
   *
   * @param method The java method.
   * @return The icon url for the specific method
   */
  public static String getIconForMethod(JavadocMethod method) {
    if (method.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2017.1/method_abstract.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2017.1/method.png";
  }
}
