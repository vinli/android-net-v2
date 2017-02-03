package li.vin.netv2.util;

import android.support.annotation.NonNull;
import li.vin.netv2.request.VinliRequest;

/**
 * Contractualizes deep copy behavior, primarily for the safely-reusable {@link
 * VinliRequest.Builder}.
 */
public interface DeepCopyable<T extends DeepCopyable<T>> {
  // note self-bounded generic

  @NonNull
  T copy();
}
