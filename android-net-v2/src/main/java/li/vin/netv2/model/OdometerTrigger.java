package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class OdometerTrigger extends BaseModels.BaseModelId {

  OdometerTrigger() {
  }

  public enum TriggerType {
    SPECIFIC("specific"), //
    FROM_NOW("from_now"), //
    MILESTONE("milestone"), //
    UNKNOWN("unknown");

    @NonNull private final String str;

    TriggerType(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static TriggerType fromString(@NonNull String str) {
      if ("specific".equals(str)) return SPECIFIC;
      if ("from_now".equals(str)) return FROM_NOW;
      if ("milestone".equals(str)) return MILESTONE;
      return UNKNOWN;
    }
  }

  String vehicleId;
  Double threshold;
  Double events;
  String type;
  String unit;

  @ReqLink({ //
      "self", //
      "vehicle" //
  }) Map links;

  @NonNull
  public String vehicleId() {
    return vehicleId;
  }

  @NonNull
  public Double threshhold() {
    return threshold;
  }

  @NonNull
  public Double events() {
    return events;
  }

  @NonNull
  public TriggerType type() {
    return TriggerType.fromString(type);
  }

  @NonNull
  public Distance.Unit unit() {
    return Distance.Unit.fromString(unit);
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<OdometerTrigger> {

    TimeSeries() {
    }

    List<OdometerTrigger> odometerTriggers;

    @Override
    List<OdometerTrigger> rawTimeSeriesContent() {
      return odometerTriggers;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<OdometerTrigger> {

    Wrapper() {
    }

    OdometerTrigger odometerTrigger;

    @NonNull
    @Override
    public OdometerTrigger extract() {
      return odometerTrigger;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<OdometerTrigger>>() {
    }.getType();
  }
}


