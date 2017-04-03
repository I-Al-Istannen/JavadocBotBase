# JavadocBotBase

This is a library for reading javadocs, previously a JDA bot.


### Reading the javadocs

To read the javadocs you'll need a JavadocManager

```java
JavadocManager manager = new JavadocManager();
```

(That's something you'll want to cache instead of making a new one each time)

After doing that you'll want to index the manager, to allow it to actually find the javadocs for you.

```java
manager.index();
```

Now that the manager has indexed all of the classes/methods/packages (it shouldn't take long), you can actually look for what you need. You'll want to use JavadocManager#getClass for classes, JavadocManager#getClassesEndingIn and JavadocManager#getMethod and finally JavadocManager#getPackage for packages.

To get a class you can do something like this:
```java
String className = "String";
Collection<JavadocClass> classes = manager.getClass(className);
if (classes.isEmpty()) {
// No classes found!
// I'd return here, or make the next part
// an else statement
}
JavadocClass javadocClass = classes.iterator().next();
String javadocClassName = javadocClass.getNameWithModifiers();
String javadocClassUrl = javadocClass.getUrl();
String classHierarchy = JavadocUtil.trimToSize(javadocClass.getExtendsImplements(), 1024);String classDescription = JavadocUtil.trimToSize(javadocClass.getDescription(), 1024);
```

Methods can be done some of the same way, but are done (instead of grabbing the classes directly you do the follow, the following code is used with the input `String#contains`):

```java
String[] split = input.split("#");
Collection<JavadocClass> methodClasses = manager.getClassEndingIn(split[0]);
if (methodClasses.isEmpty()) {
// Error, classes not found
// I'd return here, or make the next part
// an else statement
}
JavadocClass methodClass = methodClasses.iterator().next();
Optional<JavadocMethod> methodOptional = GlobalConstants.manager.getMethod(methodClass, split[1]);
if (!methodOptional.isPresent()) {
// Error, method not found
// I'd return here, or make the next part
// an else statement
}
JavadocMethod method = methodOptional.get();
// Then the relevant method code for grabbing URLs and such
```

Packages are done almost the exact same as classes, and are formatted like: `java.util`

Happy Javadocing!
