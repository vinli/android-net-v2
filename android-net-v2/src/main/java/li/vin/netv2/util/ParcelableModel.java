package li.vin.netv2.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.Vinli;
import li.vin.netv2.model.contract.StrictModel;

/**
 * Utility for converting {@link StrictModel} to and from {@link Parcelable} without the usual
 * boilerplate or overhead of maintaining code-gen dependencies.
 *
 * @see #fromModel(StrictModel)
 * @see #toModel(Parcelable, Class)
 * @see StrictModel
 */
public final class ParcelableModel implements Parcelable {

  /**
   * Wrap the given model in a {@link ParcelableModel}, allowing any Gson-compatible model to be
   * treated as parcelable without the usual boilerplate or overhead of maintaining code-gen
   * dependencies. The given model must be non-null - it is up to the caller to handle the "null
   * model" case; unchecked exceptions will be thrown upon trying to write or unparcel a null
   * model.
   *
   * @see #toModel(Parcelable, Class)
   */
  @NonNull
  public static ParcelableModel fromModel(@NonNull StrictModel model) {
    return new ParcelableModel(model);
  }

  /**
   * Convert a Parcelable back into a model. This method assumes the given Parcelable is an
   * instance of {@link ParcelableModel}, and that it contains a non-null model of the given
   * modelClass. It is the caller's responsibility to ensure these conditions are met - if they
   * are not, an unchecked exception will be thrown.
   *
   * @see #fromModel(StrictModel)
   */
  @NonNull
  @SuppressWarnings({ "unchecked", "ConstantConditions" })
  public static <T extends StrictModel> T toModel( //
      @NonNull Parcelable p, @NonNull Class<T> modelClass) {
    ParcelableModel pm = (ParcelableModel) p;
    if (pm.model == null) pm.model = Vinli.strictGson().fromJson(pm.json, modelClass);
    if (pm.model == null) throw new NullPointerException();
    return (T) pm.model;
  }

  @Nullable private volatile Object model;
  @Nullable private volatile String json;

  private ParcelableModel(@NonNull Object model) {
    this.model = model;
  }

  private ParcelableModel(@NonNull String json) {
    this.json = json;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    // We convert the model to JSON lazily, only when we need to write, to ensure that we don't
    // undermine the main benefit of Parcelable over other forms of serialization - its last
    // resort serialization.
    if (json == null) json = Vinli.strictGson().toJson(model);
    if (json == null) throw new NullPointerException("tried null JSON to parcel.");
    //noinspection ConstantConditions
    if (json.isEmpty()) throw new IllegalArgumentException("tried empty JSON to parcel.");
    out.writeString(json);
  }

  public static final Creator<ParcelableModel> CREATOR = new Creator<ParcelableModel>() {

    @Override
    public ParcelableModel createFromParcel(Parcel in) {
      return new ParcelableModel(in.readString());
    }

    @Override
    public ParcelableModel[] newArray(int size) {
      return new ParcelableModel[size];
    }
  };
}
