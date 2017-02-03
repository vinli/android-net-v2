package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.OptIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.OptLatLon;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Collision extends BaseModels.BaseModelId {

  Collision() {
  }

  String deviceId;
  String vehicleId;
  @ReqIsoDate String timestamp;

  @AllowNull //
  @OptIsoDate({ //
      "timestamp" //
  }) //
  @OptLatLon({ //
      "coordinate"
  }) Map location;

  @ReqLink({ //
      "self", //
      "device", //
      "vehicle" //
  }) Map links;

  @NonNull
  public String deviceId() {
    return deviceId;
  }

  @NonNull
  public String vehicleId() {
    return vehicleId;
  }

  @NonNull
  public String timestamp() {
    return timestamp;
  }

  @Nullable
  public String locationTimestamp() {
    return maps.get().getStrNullable(location, "timestamp");
  }

  @Nullable
  public double[] locationLatLon() {
    return maps.get().getLatLonNullable(location, "coordinate");
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Device.Wrapper> deviceLink() {
    return Link.create(maps.get().getStr(links, "device"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Collision> {

    TimeSeries() {
    }

    List<Collision> collisions;

    @Override
    List<Collision> rawTimeSeriesContent() {
      return collisions;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Collision> {

    Wrapper() {
    }

    Collision collision;

    @NonNull
    @Override
    public Collision extract() {
      return collision;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Collision>>() {
    }.getType();
  }
}
