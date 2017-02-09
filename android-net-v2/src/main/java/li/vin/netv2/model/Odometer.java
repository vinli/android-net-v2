package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.misc.StrictValidations.AllowNull;
import li.vin.netv2.model.misc.StrictValidations.OptIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;

import static li.vin.netv2.model.ModelPkgHooks.maps;

/**
 * Created by JoshBeridon on 2/3/17.
 */

public class Odometer extends BaseModels.BaseModelId {

  Odometer() {

  }


  Double reading;
  String vehicleId;
  Distance.Unit unit;
  @ReqIsoDate String timestamp;

  @AllowNull //
  @OptIsoDate({ //
      "date" //
  }) //

  @ReqLink({
      "self", //
      "vehcle"
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
    return unit;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Odometer> {
    TimeSeries() {

    }

    List<Odometer> odometers;

    @Override
    List<Odometer> rawTimeSeriesContent() {
      return null;
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
    return new TypeToken<List<Wrapper>>() {
    }.getType();
  }
}
