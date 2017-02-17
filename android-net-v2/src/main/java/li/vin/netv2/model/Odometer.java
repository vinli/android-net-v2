package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;

import static li.vin.netv2.model.ModelPkgHooks.maps;

public class Odometer extends BaseModels.BaseModelId {

  Odometer() {
  }

  Double reading;
  String vehicleId;
  String unit;
  @ReqIsoDate String date;

  @ReqLink({
      "self", //
      "vehicle"
  }) Map links;

  @NonNull
  public String vehicleId() {
    return vehicleId;
  }

  @NonNull
  public Double reading() {
    return reading;
  }

  @NonNull
  public Distance.Unit unit() {
    return Distance.Unit.fromString(unit);
  }

  @NonNull
  public String date() {
    return date;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Odometer> {

    TimeSeries() {
    }

    List<Odometer> odometers;

    @Override
    List<Odometer> rawTimeSeriesContent() {
      return odometers;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Odometer> {

    Wrapper() {
    }

    Odometer odometer;

    @NonNull
    @Override
    public Odometer extract() {
      return odometer;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Odometer>>() {
    }.getType();
  }
}
