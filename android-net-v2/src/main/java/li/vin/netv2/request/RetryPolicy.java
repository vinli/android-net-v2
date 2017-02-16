package li.vin.netv2.request;

import android.support.annotation.NonNull;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static li.vin.netv2.util.NetworkErrors.is5xx;
import static li.vin.netv2.util.NetworkErrors.isConnectionException;
import static li.vin.netv2.util.NetworkErrors.isSocketTimeout;

/**
 * A collection of common retry policies. Only applicable to HTTP 5XX errors. It is not
 * recommended to automatically retry other types of errors.
 *
 * @see #flat(int, TimeUnit, boolean)
 * @see #linear(int, TimeUnit, boolean)
 * @see #exponential(int, TimeUnit, boolean)
 */
public abstract class RetryPolicy {

  private RetryPolicy() {
  }

  /**
   * Retry with a flat backoff. Optionally add jitter (jitter strongly recommended).
   *
   * @see #linear(int, TimeUnit, boolean)
   * @see #exponential(int, TimeUnit, boolean)
   */
  @NonNull
  public static RetryPolicy flat(int amount, @NonNull TimeUnit unit, final boolean jitter) {
    final long amountMs = unit.toMillis(amount);
    final Random rand = new Random();
    return new RetryPolicy() {
      @Override
      public long delay(@NonNull Throwable err, int count) {
        if (is5xx(err) || isSocketTimeout(err) || isConnectionException(err)) {
          //noinspection UnnecessaryLocalVariable
          long delay = amountMs;
          if (jitter) return (long) (rand.nextFloat() * delay);
          return delay;
        }
        return -1L;
      }
    };
  }

  /** Convenience for {@link #flat(int, TimeUnit, boolean)} with jitter. */
  @NonNull
  public static RetryPolicy flat(int amount, @NonNull TimeUnit unit) {
    return flat(amount, unit, true);
  }

  /**
   * Retry with a linear backoff. Optionally add jitter (jitter strongly recommended).
   *
   * @see #flat(int, TimeUnit, boolean)
   * @see #exponential(int, TimeUnit, boolean)
   */
  @NonNull
  public static RetryPolicy linear(int amount, @NonNull TimeUnit unit, final boolean jitter) {
    final long amountMs = unit.toMillis(amount);
    final Random rand = new Random();
    return new RetryPolicy() {
      @Override
      public long delay(@NonNull Throwable err, int count) {
        if (is5xx(err) || isSocketTimeout(err) || isConnectionException(err)) {
          long delay = amountMs * count;
          if (jitter) return (long) (rand.nextFloat() * delay);
          return delay;
        }
        return -1L;
      }
    };
  }

  /** Convenience for {@link #linear(int, TimeUnit, boolean)} with jitter. */
  @NonNull
  public static RetryPolicy linear(int amount, @NonNull TimeUnit unit) {
    return linear(amount, unit, true);
  }

  /**
   * Retry with an exponential backoff. Optionally add jitter (jitter strongly recommended).
   *
   * @see #flat(int, TimeUnit, boolean)
   * @see #linear(int, TimeUnit, boolean)
   */
  @NonNull
  public static RetryPolicy exponential(int amount, @NonNull TimeUnit unit, final boolean jitter) {
    final long amountMs = unit.toMillis(amount);
    final Random rand = new Random();
    return new RetryPolicy() {
      @Override
      public long delay(@NonNull Throwable err, int count) {
        if (is5xx(err) || isSocketTimeout(err) || isConnectionException(err)) {
          long delay = amountMs;
          for (int i = 1; i < count; i++) delay *= 2;
          if (jitter) return (long) (rand.nextFloat() * delay);
          return delay;
        }
        return -1L;
      }
    };
  }

  /** Convenience for {@link #exponential(int, TimeUnit, boolean)} with jitter. */
  @NonNull
  public static RetryPolicy exponential(int amount, @NonNull TimeUnit unit) {
    return exponential(amount, unit, true);
  }

  /**
   * How long to delay in ms for the given err and retry count. Count starts at 1. Returning a
   * negative value will abort the retries and propagate the latest error.
   */
  public abstract long delay(@NonNull Throwable err, int count);
}
