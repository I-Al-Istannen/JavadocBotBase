# JavadocBotBase

This is a library for reading javadocs, previously a JDA bot.


### Reading the javadocs

To read the javadocs you'll need a JavadocManager

```java
JavadocManager manager = new JavadocManager();
```

(That's something you'll want to cache instead of making a new one each time)

After doing that you'll want to index the manager, to allow it to find the JavaDocs for you.

```java
manager.index();
```

Now that the manager has indexed all of the classes and packages (it shouldn't take long),
you can actually look for what you need.
  
There are different ways to actually get your information now.
* `JavadocManager#getClassesExact(String name)`  
   Returns all classes with that exact name. If you have `test.Hello` and `main.Hello`,
   a query for `Hello` will return both.
* `JavadocMannager#getClassesEndingIn(String name)`  
  Returns all classes ending in that name.
   It respects packages, so a query for `t.Hello` would only return `test.Hello`.
* `JavadocManager#getPackage(String name)`  
  Returns a package found by its name. Needs to be the fully qualified name.
  `util` would *not* return `java.util`.
* `JavadocManager#getMethod(JavadocClass class, String name)`  
  Returns a method with the given name found in the given class.

#### Some examples

##### Getting a class:
```java
String className = "String";
Collection<JavadocClass> classes = manager.getClass(className);
if (classes.isEmpty()) {
// No classes found!
}
JavadocClass javadocClass = classes.iterator().next();
javadocClass.getName(); // 'String'
javadocClass.getNameWithModifiers(); // 'public final class String'
javadocClass.getUrl(); // 'https://docs.oracle.com/javase/8/docs/api/java/lang/String.html'
javadocClass.getExtendsImplements(); // 'extends Object implements Serializable` (Omitted some and link markdown)
javadocClass.getDescription(); // 'The `String` class represents character strings.' And on and on
javadocClass.getDeclaration(); // getNameWithModifiers() + getExtendsImplemements()
```

##### Getting a method

```java
Collection<JavadocClass> methodClasses = manager.getClassEndingIn("String");
if (methodClasses.isEmpty()) {
// Error, class not found
}
JavadocClass methodClass = methodClasses.iterator().next();
Optional<JavadocMethod> methodOptional = GlobalConstants.manager.getMethod(methodClass, "toLowerCase");
if (!methodOptional.isPresent()) {
// Error, method not found
}
JavadocMethod method = methodOptional.get();
method.getName(); // 'toLowerCase'
method.getDescription(); // 'Converts all of the characters in this `String` to lower case...'
method.getDeclaration(); // 'public [String](link)  toLowerCase()' (Omitted link)
method.getReturnValue(); // '[String](link)' (Omitted link)
method.getShortDescription(); // 'Converts all of the characters in this `String` to lower case using the rules of the default locale.' 
method.getContainingClass(); // 'Well, the passed String class'
method.getDeclarationWithoutExceptions(); // Same as getDeclaration here. Just replaces everything following 'throws'
method.getExceptions(); // ''
method.getParameters(); // '{}'
```

##### Packages
Packages are basically done like classes.

```java
Optional<Package> packageOptional = manager.getPackage("java.util");
// handling for absent package
Package javadocPackage = packageOptional.get();
javadocPackage.getName(); // 'java.util'
javadocPackage.getDescription(); // 'Contains the collections framework,...'
javadocPackage.getUrl(); // 'https://docs.oracle.com/javase/8/docs/api/java/util/package-summary.html'
javadocPackage.getClasses(); // '[Long list]'
```

### Maven
It is currently not in the maven central repo or JCenter.  
You can obtain it using [Jitpack.io](https://jitpack.io/#I-Al-Istannen/JavadocBotBase) though.

<br>
Happy Javadocing!
