package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import li.vin.netv2.error.NoResourceExistsException;

import static li.vin.netv2.model.BaseModels.BaseModel;
import static li.vin.netv2.model.BaseModels.BaseModelWrapper;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;

public class BatteryStatus extends BaseModel {

  BatteryStatus() {
  }

  public static class NoBatteryStatusException extends NoResourceExistsException {

    public NoBatteryStatusException() {
      super("No battery status available for the given resource.");
    }
  }

  public enum Color {

    GREEN("green"),
    YELLOW("yellow"),
    RED("red"),
    UNKNOWN("unknown");

    @NonNull private final String str;

    Color(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static Color fromString(@NonNull String str) {
      if ("green".equals(str)) return GREEN;
      if ("yellow".equals(str)) return YELLOW;
      if ("red".equals(str)) return RED;
      return UNKNOWN;
    }
  }

  String status;
  @ReqIsoDate String timestamp;

  @NonNull
  public Color status() {
    return Color.fromString(status);
  }

  @NonNull
  public String timestamp() {
    return timestamp;
  }

  public static class Wrapper extends BaseModelWrapper<BatteryStatus> {

    Wrapper() {
    }

    @AllowNull BatteryStatus batteryStatus;

    @NonNull
    @Override
    public BatteryStatus extract() {
      if (batteryStatus == null) throw new NoBatteryStatusException();
      return batteryStatus;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<BatteryStatus>>() {
    }.getType();
  }
}
