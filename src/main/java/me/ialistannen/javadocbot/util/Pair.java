package me.ialistannen.javadocbot.util;

/**
 * A small pair implementation
 *
 * @author jwachter
 */
public class Pair<K, V> {

  private K key;
  private V value;

  /**
   * @param key The key
   * @param value The value
   */
  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * @return The key
   */
  public K getKey() {
    return key;
  }

  /**
   * @return The value
   */
  public V getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "Pair{" +
        "key=" + key +
        ", value=" + value +
        '}';
  }
}
