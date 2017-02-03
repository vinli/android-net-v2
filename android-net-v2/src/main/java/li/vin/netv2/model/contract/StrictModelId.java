package li.vin.netv2.model.contract;

import android.support.annotation.NonNull;

/** {@link StrictModel} that has a unique {@link #id()}. */
public interface StrictModelId extends StrictModel {

  /** globally unique, non-null identifier. */
  @NonNull
  String id();
}
