package li.vin.netv2.model;

import android.support.annotation.NonNull;
import li.vin.netv2.request.Linker;
import li.vin.netv2.model.contract.StrictModel;

import static java.lang.String.format;

/**
 * For use with {@link Linker}. Facilitates typesafe linking. Is an immutable data class;
 * implements {@link #equals(Object)}, {@link #hashCode()}, {@link #toString()}.
 */
@SuppressWarnings("unused") // type param isn't used internally, but keeps links typesafe.
public final class Link<T extends StrictModel> {

  /** Links will never be null - we instead use this value to indicate that no link exists. */
  public static final String NO_LINK = format("%s.NO_LINK", Link.class.getName());

  static <T extends StrictModel> Link<T> create(@NonNull String raw) {
    return new Link<>(raw);
  }

  @NonNull private final String raw;

  private Link(@NonNull String raw) {
    this.raw = raw;
  }

  /**
   * The raw value of this link. In most cases, rather than using this directly, prefer to use
   * this instance with a {@link Linker}.
   */
  @NonNull
  public String raw() {
    return raw;
  }

  /**
   * Helper to check if this is a valid link, or {@link #NO_LINK}. Unless using this link's raw
   * value manually, it is safe to use without checking this. {@link Linker} areadly checks this
   * internally.
   */
  public boolean isNoLink() {
    return NO_LINK.equals(raw);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Link)) return false;
    Link<?> link = (Link<?>) o;
    return raw.equals(link.raw);
  }

  @Override
  public int hashCode() {
    return raw.hashCode();
  }

  @Override
  public String toString() {
    return raw;
  }
}
