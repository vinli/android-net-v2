package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.BaseModels.BaseModel;
import static li.vin.netv2.model.BaseModels.BaseModelWrapper;
import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.OptIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Distance extends BaseModel {

  Distance() {
  }

  public enum Unit {

    METERS("m"),
    KILOMETERS("km"),
    MILES("mi"),
    UNKNOWN("unknown");

    @NonNull private final String str;

    Unit(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static Unit fromString(@NonNull String str) {
      if ("m".equals(str)) return METERS;
      if ("km".equals(str)) return KILOMETERS;
      if ("mi".equals(str)) return MILES;
      return UNKNOWN;
    }
  }

  String vehicleId;
  double value;
  double confidenceMin;
  double confidenceMax;
  String unit;
  @AllowNull @OptIsoDate String lastOdometerDate;

  @ReqLink({ //
      "self", //
      "vehicle" //
  }) Map links;

  @NonNull
  public String vehicleId() {
    return vehicleId;
  }

  public double value() {
    return value;
  }

  public double confidenceMin() {
    return confidenceMin;
  }

  public double confidenceMax() {
    return confidenceMax;
  }

  @NonNull
  public Unit unit() {
    return Unit.fromString(unit);
  }

  @Nullable
  public String lastOdometerDate() {
    return lastOdometerDate;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class Wrapper extends BaseModelWrapper<Distance> {

    Wrapper() {
    }

    Distance distance;

    @NonNull
    @Override
    public Distance extract() {
      return distance;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Distance>>() {
    }.getType();
  }
}
