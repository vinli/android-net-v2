package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.BaseModels.BaseModelId;
import li.vin.netv2.model.BaseModels.BaseModelPage;
import li.vin.netv2.model.BaseModels.BaseModelWrapper;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Device extends BaseModelId {

  Device() {
  }

  @AllowNull String name;
  @AllowNull String icon;
  @AllowNull Boolean virtual;
  String chipId;
  @ReqIsoDate String createdAt;

  @ReqLink({ //
      "self", //
      "events", //
      "latestVehicle", //
      "rules", //
      "subscriptions", //
      "trips", //
      "vehicles" //
  }) Map links;

  @Nullable
  public String name() {
    return name;
  }

  @Nullable
  public String icon() {
    return icon;
  }

  public boolean virtual() {
    return virtual != null && virtual;
  }

  @NonNull
  public String chipId() {
    return chipId;
  }

  @NonNull
  public String createdAt() {
    return createdAt;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Event.TimeSeries> eventsLink() {
    return Link.create(maps.get().getStr(links, "events"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> latestVehicleLink() {
    return Link.create(maps.get().getStr(links, "latestVehicle"));
  }

  @NonNull
  public Link<Rule.Page> rulesLink() {
    return Link.create(maps.get().getStr(links, "rules"));
  }

  // TODO - convert this to a Link class
  @NonNull
  public String subscriptionsLink() {
    return maps.get().getStr(links, "subscriptions");
  }

  @NonNull
  public Link<Trip.TimeSeries> tripsLink() {
    return Link.create(maps.get().getStr(links, "trips"));
  }

  @NonNull
  public Link<Vehicle.Page> vehiclesLink() {
    return Link.create(maps.get().getStr(links, "vehicles"));
  }

  public static class Page extends BaseModelPage<Device> {

    Page() {
    }

    List<Device> devices;

    @Override
    List<Device> rawPageContent() {
      return devices;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Page>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModelWrapper<Device> {

    Wrapper() {
    }

    Device device;

    @NonNull
    @Override
    public Device extract() {
      return device;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Device>>() {
    }.getType();
  }
}
