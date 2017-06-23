package me.ialistannen.javadocbot.javadoc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import me.ialistannen.javadocbot.util.ListUtil;
import me.ialistannen.javadocbot.util.Pair;

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

  /**
   * Uses the default {@link JavadocSettings}
   */
  public JavadocManager() {
    this(new JavadocSettings());
  }

  /**
   * @param settings The {@link JavadocSettings} to use. Will be the same as {@link #getSettings()}
   */
  public JavadocManager(JavadocSettings settings) {
    this.settings = settings;
    packageParser = new PackageParser(this.settings);
    classParser = new ClassParser(this.settings);
    methodParser = new MethodParser(this.settings, this);
  }

  /**
   * Indexes all classes
   */
  @SuppressWarnings("unused")
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
   * Returns all classes with the exact given name.
   * <p>
   * Does not consider packages. ONLY pass the class name.
   *
   * @param name The name of the class
   * @return The classes, if any
   */
  public List<JavadocClass> getClassesExact(String name) {
    return new ArrayList<>(classMap.get(name));
  }

  /**
   * Returns all classes ending in the given name. <em>Includes</em> packages.
   * <p>
   * You can pass a fully qualified name in here.
   *
   * @param name The name of the class
   * @return Any class ending in that sequence, including package names
   */
  public List<JavadocClass> getClassEndingIn(String name) {
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
  public List<JavadocClass> getAllClasses() {
    return new ArrayList<>(classMap.values());
  }

  /**
   * Returns a package by its name.
   *
   * @param name The name of the package. Fully qualified, case sensitive.
   * @return The package, if any
   */
  public Optional<Package> getPackage(String name) {
    return Optional.ofNullable(packageMap.get(name));
  }

  /**
   * Returns all methods with the given name and parameters.
   *
   * @param javadocClass The class to get it for
   * @param name The name of the method. Can contain parameters in the `(paramClass)` notation
   * @return The first {@link JavadocMethod} if found
   */
  public List<JavadocMethod> getMethodsWithNameAndParam(JavadocClass javadocClass, String name) {
    Collection<String> parameters = getParameters(name);
    String methodName = name.replaceAll("\\(.+\\)", "");
    List<JavadocMethod> methodsWithName = getAllMethods(javadocClass).stream()
        .filter(javadocMethod -> javadocMethod.getName().equals(methodName))
        .collect(Collectors.toList());

    Optional<JavadocMethod> exactMatch = findExactMatch(methodsWithName, parameters);

    return exactMatch.map(Arrays::asList).orElse(methodsWithName);
  }

  private Optional<JavadocMethod> findExactMatch(Collection<JavadocMethod> methods,
      Collection<String> parameters) {
    return methods.stream()
        .filter(javadocMethod -> hasMatchingParameters(parameters, javadocMethod))
        .findAny();
  }

  /**
   * Returns all methods with the given name and parameters.
   *
   * @param javadocClass The class to get it for
   * @param name The name of the method. Can contain parameters in the `(paramClass)` notation
   * @return The first {@link JavadocMethod} if found
   */
  public List<JavadocMethod> getMethodsWithName(JavadocClass javadocClass, String name) {
    return getMethodsWithNameAndParam(javadocClass, name.replaceAll("\\(.+\\)", ""));
  }

  /**
   * Returns all methods for the class
   *
   * @param javadocClass The class to get the methods for
   * @return All methods for the class
   */
  public List<JavadocMethod> getAllMethods(JavadocClass javadocClass) {
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
  private List<String> getParameters(String methodName) {
    if (!methodName.contains("(")) {
      return Collections.emptyList();
    }
    String parameters = methodName.substring(methodName.indexOf("(") + 1);
    parameters = parameters.replace(")", "");
    return Arrays.stream(parameters.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  /**
   * Checks if the parameters match
   *
   * @param userEntered The parameters the user entered
   * @param method The method to check
   * @return True if the parameters match
   */
  private boolean hasMatchingParameters(Collection<String> userEntered, JavadocMethod method) {
    List<Pair<String, String>> parameters = method.getParameters();
    if (parameters.size() != userEntered.size()) {
      return false;
    }
    for (String s : userEntered) {
      if (!ListUtil.contains(parameters, s, Pair::getKey)) {
        return false;
      }
    }
    return true;
  }
}
