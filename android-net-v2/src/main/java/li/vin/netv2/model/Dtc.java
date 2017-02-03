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
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Dtc extends BaseModels.BaseModelId {

  Dtc() {
  }

  public enum State {

    ACTIVE("active"),
    INACTIVE("inactive");

    @NonNull private final String str;

    State(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static State fromString(@NonNull String str) {
      if ("active".equals(str)) return ACTIVE;
      if ("inactive".equals(str)) return INACTIVE;
      throw new IllegalArgumentException("must be active or inactive.");
    }
  }

  String vehicleId;
  String deviceId;
  String number;
  String description;
  @ReqIsoDate String start;
  @AllowNull @OptIsoDate String stop;

  @ReqLink({ //
      "code", //
      "vehicle", //
      "device", //
  }) Map links;

  @NonNull
  public String vehicleId() {
    return vehicleId;
  }

  @NonNull
  public String deviceId() {
    return deviceId;
  }

  @NonNull
  public String number() {
    return number;
  }

  @NonNull
  public String description() {
    return description;
  }

  @NonNull
  public String start() {
    return start;
  }

  @Nullable
  public String stop() {
    return stop;
  }

  @NonNull
  public Link<DtcDiagnosis.Wrapper> diagnosisLink() {
    return Link.create(maps.get().getStr(links, "code"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  @NonNull
  public Link<Device.Wrapper> deviceLink() {
    return Link.create(maps.get().getStr(links, "device"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Dtc> {

    TimeSeries() {
    }

    List<Dtc> codes;

    @Override
    List<Dtc> rawTimeSeriesContent() {
      return codes;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Dtc>>() {
    }.getType();
  }
}
