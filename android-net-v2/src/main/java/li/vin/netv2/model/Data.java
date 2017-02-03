package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.ModelPkgHooks.maps;

/**
 * Helper for looking up values in a dictionary of optionals.
 * <br/><br/>
 * NOTE: This is just a transient helper class and is NOT designed to be serialized. If
 * serialization is needed, instead serialize the model that produced this data. This does not
 * implement {@link Object#equals(Object)}, {@link Object#hashCode()}, {@link Object#toString()},
 * and is therefore not safe to use as a data class.
 */
public class Data {

  @NonNull
  static Data create(@Nullable Map raw) {
    return new Data(raw);
  }

  @Nullable final Map raw;

  Data(@Nullable Map raw) {
    this.raw = raw;
  }

  @Nullable
  public <T> List<T> getList(@NonNull String key) {
    return maps.get().getListNullable(raw, key);
  }

  @Nullable
  public String getString(@NonNull String key) {
    return maps.get().getStrNullable(raw, key);
  }

  @Nullable
  public Integer getInt(@NonNull String key) {
    return maps.get().getIntNullable(raw, key);
  }

  @Nullable
  public Double getDouble(@NonNull String key) {
    return maps.get().getDblNullable(raw, key);
  }

  @Nullable
  public Boolean getBool(@NonNull String key) {
    return maps.get().getBoolNullable(raw, key);
  }

  @Nullable
  double[] getLatLon(@NonNull String key) {
    return maps.get().getLatLonNullable(raw, key);
  }
}
