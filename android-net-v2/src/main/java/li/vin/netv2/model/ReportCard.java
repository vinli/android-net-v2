package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class ReportCard extends BaseModels.BaseModelId {

  ReportCard() {
  }

  public enum Grade {

    A("A"), B("B"), C("C"), D("D"), F("F"), I("I"),
    UNKNOWN("unknown");

    @NonNull private final String str;

    Grade(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static Grade fromString(@NonNull String str) {
      if ("A".equals(str)) return A;
      if ("B".equals(str)) return B;
      if ("C".equals(str)) return C;
      if ("D".equals(str)) return D;
      if ("F".equals(str)) return F;
      if ("I".equals(str)) return I;
      return UNKNOWN;
    }
  }

  String deviceId;
  String vehicleId;
  String tripId;
  String grade;

  @ReqLink({ //
      "self", //
      "trip", //
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
  public String tripId() {
    return tripId;
  }

  @NonNull
  public Grade grade() {
    return Grade.fromString(grade);
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Trip.Wrapper> tripLink() {
    return Link.create(maps.get().getStr(links, "trip"));
  }

  @NonNull
  public Link<Device.Wrapper> device() {
    return Link.create(maps.get().getStr(links, "device"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicle() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<ReportCard> {

    TimeSeries() {
    }

    List<ReportCard> reportCards;

    @Override
    List<ReportCard> rawTimeSeriesContent() {
      return reportCards;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<ReportCard> {

    Wrapper() {
    }

    ReportCard reportCard;

    @NonNull
    @Override
    public ReportCard extract() {
      return reportCard;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<ReportCard>>() {
    }.getType();
  }
}
