package me.ialistannen.javadocbot.javadoc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import me.ialistannen.javadocbot.javadoc.model.ClassNameCollection;
import me.ialistannen.javadocbot.javadoc.model.JavadocClass;
import me.ialistannen.javadocbot.javadoc.model.JavadocMethod;
import me.ialistannen.javadocbot.javadoc.model.Package;
import me.ialistannen.javadocbot.javadoc.parsing.AllClassParser;
import me.ialistannen.javadocbot.javadoc.parsing.ClassParser;
import me.ialistannen.javadocbot.javadoc.parsing.MethodParser;
import me.ialistannen.javadocbot.javadoc.parsing.PackageParser;
import me.ialistannen.javadocbot.util.ArrayUtil;

/**
 * Manages the retrieval of Javadoc
 *
 * @author jwachter
 */
public class JavadocManager {

  private JavadocSettings settings;

  private PackageParser packageParser;
  private ClassParser classParser;
  private MethodParser methodParser;

  private Map<String, Package> packageMap = new HashMap<>();
  private Multimap<String, JavadocClass> classMap = HashMultimap.create();

  public JavadocManager() {
    settings = new JavadocSettings();
    packageParser = new PackageParser(settings);
    classParser = new ClassParser(settings);
    methodParser = new MethodParser();

    settings.setBaseUrl("https://docs.oracle.com/javase/8/docs/api/");
  }

  /**
   * Indexes all classes
   */
  public void index() {
    AllClassParser allClassParser = new AllClassParser();
    ClassNameCollection allClasses = allClassParser.parse(settings.getBaseUrl());

    for (Entry<String, Collection<String>> entry : allClasses) {
      for (String classUrl : entry.getValue()) {
        Package aPackage = packageParser.parse(classUrl);
        packageMap.put(aPackage.getName(), aPackage);
      }
    }

    for (Entry<String, Collection<String>> entry : allClasses) {
      for (String classUrl : entry.getValue()) {
        String packageName = packageParser.getNameFromLink(classUrl);
        Package aPackage = packageMap.get(packageName);
        JavadocClass javadocClass = classParser.parse(aPackage, entry.getKey(), classUrl);
        classMap.put(javadocClass.getName(), javadocClass);
      }
    }
  }

  /**
   * @param name The name of the class
   * @return The classes, if any
   */
  public Collection<JavadocClass> getClass(String name) {
    return Collections.unmodifiableCollection(classMap.get(name));
  }

  /**
   * @param name The name of the class
   * @return Any class ending in that sequence, including package names
   */
  public Collection<JavadocClass> getClassEndingIn(String name) {
    String className = ArrayUtil.getLast(name.split("\\."));
    return getAllClasses().stream()
        .filter(javadocClass -> javadocClass.getName().equalsIgnoreCase(className))
        .filter(javadocClass ->
            (javadocClass.getParentPackage().getName() + "." + javadocClass.getName())
                .endsWith(name)
        )
        .collect(Collectors.toList());
  }

  /**
   * Returns all classes in the Javadoc
   *
   * @return All classes in the Javadoc
   */
  public Collection<JavadocClass> getAllClasses() {
    return Collections.unmodifiableCollection(classMap.values());
  }

  /**
   * @param name The name of the package
   * @return The package, if any
   */
  public Optional<Package> getPackage(String name) {
    return Optional.ofNullable(packageMap.get(name));
  }

  /**
   * @param javadocClass The class to get it for
   * @param name The name of the method
   * @return The first {@link JavadocMethod} if found
   */
  public Optional<JavadocMethod> getMethod(JavadocClass javadocClass, String name) {
    Collection<String> parameters = getParameters(name);
    String methodName = name.replaceAll("\\(.+\\)", "");
    return methodParser.getMethods(javadocClass).stream()
        .filter(javadocMethod -> javadocMethod.getName().equals(methodName))
        .filter(javadocMethod -> hasMatchingParameters(parameters, javadocMethod))
        .findAny();
  }

  /**
   * Returns all methods for the class
   *
   * @param javadocClass The class to get the methods for
   * @return All methods for the class
   */
  public Collection<JavadocMethod> getAllMethods(JavadocClass javadocClass) {
    return methodParser.getMethods(javadocClass);
  }

  /**
   * @return The {@link JavadocSettings}
   */
  public JavadocSettings getSettings() {
    return settings;
  }

  /**
   * @param methodName The name of the method, the user entered
   * @return All parameters the user entered
   */
  private Collection<String> getParameters(String methodName) {
    if (!methodName.contains("(")) {
      return Collections.emptyList();
    }
    String parameters = methodName.substring(methodName.indexOf("(") + 1);
    parameters = parameters.replace(")", "");
    return Arrays.asList(parameters.split(" "));
  }

  /**
   * Checks if the parameters match
   *
   * @param userEntered The parameters the user entered
   * @param method The method to check
   * @return True if the parameters match
   */
  private boolean hasMatchingParameters(Collection<String> userEntered, JavadocMethod method) {
    Map<String, String> parameters = method.getParameters();
    if (parameters.size() != userEntered.size()) {
      return false;
    }
    for (String s : userEntered) {
      if (!parameters.containsKey(s)) {
        return false;
      }
    }
    return true;
  }
}
