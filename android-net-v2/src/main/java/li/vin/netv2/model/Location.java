package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.contract.StrictModelId;

import static li.vin.netv2.model.BaseModels.BaseModel;
import static li.vin.netv2.model.BaseModels.BaseModelTimeSeries;
import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLatLon;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;
import static li.vin.netv2.model.misc.StrictValidations.ReqStr;

public class Location extends BaseModel implements StrictModelId {

  Location() {
  }

  @ReqLatLon({ //
      "coordinates" //
  }) Map geometry;

  @ReqStr({ //
      "id" //
  }) //
  @ReqLink({ //
      "links.self" //
  }) //
  @ReqIsoDate({ //
      "timestamp" //
  }) Map properties;

  @NonNull
  @Override
  public String id() {
    return maps.get().getStr(properties, "id");
  }

  @NonNull
  public double[] latLon() {
    return maps.get().getLatLon(geometry, "coordinates");
  }

  @NonNull
  public String timestamp() {
    return maps.get().getStr(properties, "timestamp");
  }

  @NonNull
  public Link<Message.Wrapper> selfLink() {
    return Link.create(maps.get().getStr(properties, "links.self"));
  }

  public static class TimeSeries extends BaseModelTimeSeries<Location> {

    TimeSeries() {
    }

    TimeSeriesInner locations;

    @Override
    List<Location> rawTimeSeriesContent() {
      return locations.features;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  static class TimeSeriesInner extends BaseModel {

    TimeSeriesInner() {
    }

    List<Location> features;

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeriesInner>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Location>>() {
    }.getType();
  }
}
