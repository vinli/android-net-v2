package li.vin.netv2.util;

/**
 * There are many {@link Pair} implementations, and this is one (or two, depending on your point of
 * view!)
 */
public class Pair<T, U> {

  public final T first;
  public final U second;

  public Pair(T first, U second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public String toString() {
    return "Pair{" +
        "first=" + first +
        ", second=" + second +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pair)) return false;
    Pair<?, ?> pair = (Pair<?, ?>) o;
    return first != null
        ? first.equals(pair.first)
        : pair.first == null && (second != null
            ? second.equals(pair.second)
            : pair.second == null);
  }

  @Override
  public int hashCode() {
    int result = first != null
        ? first.hashCode()
        : 0;
    result = 31 * result + (second != null
        ? second.hashCode()
        : 0);
    return result;
  }
}
