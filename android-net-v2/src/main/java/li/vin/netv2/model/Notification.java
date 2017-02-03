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

public class Notification extends BaseModels.BaseModelId {

  Notification() {
  }

  String eventId;
  String eventType;
  @ReqIsoDate String eventTimestamp;
  String subscriptionId;
  @AllowNull Integer responseCode;
  @AllowNull String response;
  String url;
  String payload;
  String state;
  @AllowNull @OptIsoDate String notifiedAt;
  @AllowNull @OptIsoDate String respondedAt;
  @ReqIsoDate String createdAt;
  @AllowNull String appId;

  @ReqLink({ //
      "self", //
      "event", //
      "subscription" //
  }) Map links;

  @NonNull
  public String eventId() {
    return eventId;
  }

  @NonNull
  public String eventType() {
    return eventType;
  }

  @NonNull
  public String eventTimestamp() {
    return eventTimestamp;
  }

  @NonNull
  public String subscriptionId() {
    return subscriptionId;
  }

  @Nullable
  public Integer responseCode() {
    return responseCode;
  }

  @Nullable
  public String response() {
    return response;
  }

  @NonNull
  public String url() {
    return url;
  }

  @NonNull
  public String payload() {
    return payload;
  }

  @NonNull
  public String state() {
    return state;
  }

  @Nullable
  public String notifiedAt() {
    return notifiedAt;
  }

  @Nullable
  public String respondedAt() {
    return respondedAt;
  }

  @NonNull
  public String createdAt() {
    return createdAt;
  }

  @Nullable
  public String appId() {
    return appId;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Event.Wrapper> eventLink() {
    return Link.create(maps.get().getStr(links, "event"));
  }

  // TODO - convert this to a Link class
  @NonNull
  public String subscriptionLink() {
    return maps.get().getStr(links, "subscription");
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Notification> {

    TimeSeries() {
    }

    List<Notification> notifications;

    @Override
    List<Notification> rawTimeSeriesContent() {
      return notifications;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Notification> {

    Wrapper() {
    }

    Notification notification;

    @NonNull
    @Override
    public Notification extract() {
      return notification;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Notification>>() {
    }.getType();
  }
}
