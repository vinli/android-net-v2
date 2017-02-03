package li.vin.netv2.util;

import android.support.annotation.NonNull;
import rx.functions.Func0;

/**
 * Helper for inst that can be lazily initialized OR be set externally, but not both, exactly once.
 * Useful for singletons that might need to be overridden in some circumstances. This class is
 * thread safe using correct (volatile backing field) double checked locking.
 *
 * @see #create(Func0)
 * @see #get()
 * @see #set(Object)
 */
public final class LazyOrSet<T> extends Lazy<T> {

  private LazyOrSet(@NonNull Func0<T> init) {
    super(init);
  }

  /** Create an instance with the given initializer func. */
  public static <T> LazyOrSet<T> create(@NonNull Func0<T> init) {
    return new LazyOrSet<>(init);
  }

  /**
   * Set obj externally. If already initialized - either externally or by lazy init - this will
   * throw an unchecked exception. It is the caller's responsibility to ensure that if this is
   * called, it is called only once and before {@link #get()} is called.
   *
   * @see #get()
   */
  public final void set(@NonNull T externalObj) {
    //noinspection ConstantConditions
    if (externalObj == null) {
      throw new IllegalArgumentException("null set not allowed.");
    }
    T checkExisting = obj;
    if (checkExisting == null) {
      synchronized (this) {
        checkExisting = obj;
        if (checkExisting == null) {
          obj = externalObj;
          return;
        }
      }
    }
    throw new IllegalStateException("obj already exists.");
  }
}
