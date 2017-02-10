package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;

import static li.vin.netv2.model.ModelPkgHooks.maps;

public class Snapshot extends BaseModels.BaseModelId {
  Snapshot() {

  }

  @ReqIsoDate String timestamp;
  Map data;
  @ReqLink({
      "self"
  }) Map links;


  @NonNull
  public String timestamp() {
    return timestamp;
  }

  @NonNull
  public Data data() {
    return Data.create(data);
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
