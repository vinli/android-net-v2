package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.misc.StrictValidations.OptDouble;
import li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;

import static li.vin.netv2.model.ModelPkgHooks.maps;

public class Snapshot extends BaseModels.BaseModelId {
  Snapshot() {

  }

  @ReqIsoDate String timestamp;

  @ReqLink({
      "self"
  }) Map links;

  @OptDouble({
      "vehicleSpeed", //
      "rpm"
  }) Map data;

  @NonNull
  public String timestamp() {
    return timestamp;
  }

  @NonNull
  Data data() {
    return Data.create(data);
  }

  @Nullable
  Double vehicleSpeed() {
    return maps.get().getDblNullable(data, "vehicleSpeed");
  }

  @Nullable
  Double rpm() {
    return maps.get().getDblNullable(data, "rpm");
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Snapshot> {

    TimeSeries() {

    }

    List<Snapshot> snapshots;

    @Override
    List<Snapshot> rawTimeSeriesContent() {
      return snapshots;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Snapshot> {

    Wrapper() {

    }

    Snapshot snapshot;

    @NonNull
    @Override
    public Snapshot extract() {
      return null;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Snapshot>>() {
    }.getType();
  }
}
