package me.ialistannen.javadocbot.util;

import java.util.NoSuchElementException;

/**
 * @author jwachter
 */
public class ArrayUtil {

  /**
   * Returns the last entry in an arrayreturn null;
   *
   * @param array The array to get the last element for
   * @param <T> The type of the array
   * @return The last element
   * @throws NoSuchElementException if the array is empty
   */
  public static <T> T getLast(T[] array) {
    if (array.length < 1) {
      throw new IllegalArgumentException("Array is empty!");
    }
    return array[array.length - 1];
  }
}
