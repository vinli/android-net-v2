package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.BaseModels.BaseModelId;
import static li.vin.netv2.model.BaseModels.BaseModelTimeSeries;
import static li.vin.netv2.model.BaseModels.BaseModelWrapper;
import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.OptStr;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Event extends BaseModelId {

  Event() {
  }

  String deviceId;
  @AllowNull String vehicleId;
  @ReqIsoDate String timestamp;
  String eventType;

  @AllowNull //
  @OptStr({ //
      "id", //
      "type" //
  }) Map object;

  @AllowNull Map meta;

  @ReqLink({ //
      "self", //
      "notifications" //
  }) Map links;

  @NonNull
  public String deviceId() {
    return deviceId;
  }

  @Nullable
  public String vehicleId() {
    return vehicleId;
  }

  @NonNull
  public String timestamp() {
    return timestamp;
  }

  @NonNull
  public String type() {
    return eventType;
  }

  @Nullable
  public String objectId() {
    return maps.get().getStrNullable(object, "id");
  }

  @Nullable
  public String objectType() {
    return maps.get().getStrNullable(object, "type");
  }

  @NonNull
  public Data meta() {
    return Data.create(meta);
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Notification.TimeSeries> notificationsLink() {
    return Link.create(maps.get().getStr(links, "notifications"));
  }

  public static class TimeSeries extends BaseModelTimeSeries<Event> {

    TimeSeries() {
    }

    List<Event> events;

    @Override
    List<Event> rawTimeSeriesContent() {
      return events;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModelWrapper<Event> {

    Wrapper() {
    }

    Event event;

    @NonNull
    @Override
    public Event extract() {
      return event;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Event>>() {
    }.getType();
  }
}
