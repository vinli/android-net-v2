package li.vin.netv2.model.contract;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import li.vin.netv2.util.ParcelableModel;

/**
 * Base contract for all Vinli model classes. Must provide, at minimum, data class functionality:
 * {@link Object#equals(Object)}, {@link Object#hashCode()}, and {@link Object#toString()}. In
 * addition, any class implementing this should be out-of-the-box {@link Gson} compatible. No
 * custom type adapters or funny business required for basic serialization and deserialization.
 * <br/><br/>
 * Also note: {@link #toParcelable()} contractualizes that {@link StrictModel} instances be
 * convertible to a {@link Parcelable}, even if the {@link StrictModel} itself is not necessarily
 * {@link Parcelable}. See {@link ParcelableModel#toModel(Parcelable, Class)} for conversion from
 * {@link Parcelable} back into a specific type of {@link StrictModel}.
 */
public interface StrictModel {

  /** @see Object#equals(Object) */
  boolean equals(Object obj);

  /** @see Object#hashCode() */
  int hashCode();

  /** @see Object#toString() */
  String toString();

  /** @see ParcelableModel */
  @NonNull
  ParcelableModel toParcelable();
}
