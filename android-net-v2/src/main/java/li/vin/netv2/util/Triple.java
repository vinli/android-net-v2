package li.vin.netv2.util;

/** @see Pair */
public class Triple<T, U, V> {

  public final T first;
  public final U second;
  public final V third;

  public Triple(T first, U second, V third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  @Override
  public String toString() {
    return "Triple{" +
        "first=" + first +
        ", second=" + second +
        ", third=" + third +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Triple)) return false;
    Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
    return first != null
        ? first.equals(triple.first)
        : triple.first == null && (second != null
            ? second.equals(triple.second)
            : triple.second == null && (third != null
                ? third.equals(triple.third)
                : triple.third == null));
  }

  @Override
  public int hashCode() {
    int result = first != null
        ? first.hashCode()
        : 0;
    result = 31 * result + (second != null
        ? second.hashCode()
        : 0);
    result = 31 * result + (third != null
        ? third.hashCode()
        : 0);
    return result;
  }
}
