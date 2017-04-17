package me.ialistannen.javadocbot.util;

import java.util.function.Function;

/**
 * Some random List utility functions
 */
public class ListUtil {

  /**
   * Checks if an {@link Iterable} contains an element, after a mapping function was applied to all
   * elements of it.
   *
   * @param iterable The {@link Iterable} to check inside
   * @param element The element you search
   * @param mapper The translating function to turn an element of the {@link Iterable} to the class
   * of the passed {@code element}
   * @param <T> The type of the {@link Iterable}
   * @param <R> The type of the element whose presence you want to check
   * @return True if the {@code element} is inside the {@link Iterable} after the mapper was
   * applied.
   */
  public static <T, R> boolean contains(Iterable<T> iterable, R element, Function<T, R> mapper) {
    for (T t : iterable) {
      if (mapper.apply(t).equals(element)) {
        return true;
      }
    }
    return false;
  }
}
