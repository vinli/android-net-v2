package li.vin.netv2.util;

import android.support.annotation.NonNull;
import rx.functions.Func0;

/**
 * Helper for inst that should be lazily initialized. This class is thread safe using correct
 * (volatile backing field) double checked locking.
 *
 * @see #create(Func0)
 * @see #get()
 */
public class Lazy<T> {

  protected Lazy(@NonNull Func0<T> init) {
    this.init = init;
  }

  /** Create an instance with the given initializer func. */
  public static <T> Lazy<T> create(@NonNull Func0<T> init) {
    return new Lazy<>(init);
  }

  @NonNull private final Func0<T> init;
  protected volatile T obj;

  /** Get obj. Lazy init if needed. */
  @NonNull
  public final T get() {
    T result = obj;
    if (result == null) {
      synchronized (this) {
        result = obj;
        if (result == null) {
          obj = result = init.call();
          if (result == null || obj == null) {
            throw new IllegalStateException("null init not allowed.");
          }
        }
      }
    }
    return result;
  }
}
