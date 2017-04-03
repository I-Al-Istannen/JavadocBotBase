import me.ialistannen.javadocbot.javadoc.model.*;

public class JavadocUtil {

  /**
   * Trims a String to a given size
   *
   * @param input The input String
   * @param max The max size
   * @return The trimmed String, or the original if it was small enough
   */
  private static String trimToSize(String input, int max) {
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
  private static String stripFormatting(String input) {
    String strippedContent = input.replaceAll("[*`_~]", "");
    return strippedContent.replaceAll("\\[(.+?)]\\(.+?\\)", "$1");
  }
  
   /**
   * Gets the icon URL for a specified java class
   *
   * @param javadocClass The java class.
   * @return The icon url for the specific class
   */
  private String getIconUrlForClass(JavadocClass javadocClass) {
    if (javadocClass.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeAbstract.png";
    } else if (javadocClass.getType().equalsIgnoreCase("interface")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeInterface.png";
    } else if (javadocClass.getType().equalsIgnoreCase("enum")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeEnum.png";
    } else if (javadocClass.getDeclaration().contains("final")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeFinal.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2016.3/classTypeJavaClass.png";
  }
  
   /**
   * Gets the icon URL for a specified java method
   *
   * @param javadocClass The java method.
   * @return The icon url for the specific method
   */
  private String getIconForMethod(JavadocMethod method) {
    if (method.getDeclaration().contains("abstract")) {
      return "https://www.jetbrains.com/help/img/idea/2016.3/method_abstract.png";
    }
    return "https://www.jetbrains.com/help/img/idea/2016.3/method.png";
  }

}
