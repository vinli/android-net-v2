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
import static li.vin.netv2.model.misc.StrictValidations.OptLatLon;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Trip extends BaseModels.BaseModelId {

  Trip() {
  }

  String deviceId;
  String vehicleId;
  String status;
  @AllowNull String preview;
  @ReqIsoDate String start;
  @AllowNull @OptIsoDate String stop;
  @AllowNull @OptLatLon({ "coordinates" }) Map startPoint;
  @AllowNull @OptLatLon({ "coordinates" }) Map stopPoint;

  @AllowNull Stats stats;

  @ReqLink({ //
      "self", //
      "device", //
      "vehicle", //
      "locations", //
      "messages", //
      "events" //
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
  public String status() {
    return status;
  }

  @Nullable
  public String preview() {
    return preview;
  }

  @NonNull
  public String start() {
    return start;
  }

  @Nullable
  public String stop() {
    return stop;
  }

  @Nullable
  public double[] startLatLon() {
    return maps.get().getLatLonNullable(startPoint, "coordinates");
  }

  @Nullable
  public double[] stopLatLon() {
    return maps.get().getLatLonNullable(stopPoint, "coordinates");
  }

  @NonNull
  public Stats stats() {
    return stats == null
        ? new Stats()
        : stats;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Device.Wrapper> deviceLink() {
    return Link.create(maps.get().getStr(links, "device"));
  }

  @NonNull
  public Link<Vehicle.Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "vehicle"));
  }

  @NonNull
  public Link<Location.TimeSeries> locationsLink() {
    return Link.create(maps.get().getStr(links, "locations"));
  }

  @NonNull
  public Link<Message.TimeSeries> messagesLink() {
    return Link.create(maps.get().getStr(links, "messages"));
  }

  @NonNull
  public Link<Event.TimeSeries> eventsLink() {
    return Link.create(maps.get().getStr(links, "events"));
  }

  public static class Stats extends BaseModels.BaseModel {

    Stats() {
    }

    @AllowNull Double averageLoad;
    @AllowNull Double averageMovingSpeed;
    @AllowNull Double averageSpeed;
    @AllowNull Double distance;
    @AllowNull Double distanceByGPS;
    @AllowNull Double distanceByVSS;
    @AllowNull Double fuelConsumed;
    @AllowNull Double fuelEconomy;
    @AllowNull Double maxSpeed;
    @AllowNull Double stdDevMovingSpeed;

    @AllowNull Integer duration;
    @AllowNull Integer hardAccelCount;
    @AllowNull Integer hardBrakeCount;
    @AllowNull Integer locationCount;
    @AllowNull Integer messageCount;
    @AllowNull Integer stopCount;

    @AllowNull Boolean comprehensiveLocations;
    @AllowNull Boolean substantial;

    @Nullable
    Double averageLoad() {
      return averageLoad;
    }

    @Nullable
    Double averageMovingSpeed() {
      return averageMovingSpeed;
    }

    @Nullable
    Double averageSpeed() {
      return averageSpeed;
    }

    @Nullable
    Double distance() {
      return distance;
    }

    @Nullable
    Double distanceByGPS() {
      return distanceByGPS;
    }

    @Nullable
    Double distanceByVSS() {
      return distanceByVSS;
    }

    @Nullable
    Double fuelConsumed() {
      return fuelConsumed;
    }

    @Nullable
    Double fuelEconomy() {
      return fuelEconomy;
    }

    @Nullable
    Double maxSpeed() {
      return maxSpeed;
    }

    @Nullable
    Double stdDevMovingSpeed() {
      return stdDevMovingSpeed;
    }

    @Nullable
    Integer duration() {
      return duration;
    }

    @Nullable
    Integer hardAccelCount() {
      return hardAccelCount;
    }

    @Nullable
    Integer hardBrakeCount() {
      return hardBrakeCount;
    }

    @Nullable
    Integer locationCount() {
      return locationCount;
    }

    @Nullable
    Integer messageCount() {
      return messageCount;
    }

    @Nullable
    Integer stopCount() {
      return stopCount;
    }

    @Nullable
    Boolean comprehensiveLocations() {
      return comprehensiveLocations;
    }

    @Nullable
    Boolean substantial() {
      return substantial;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Stats>>() {
      }.getType();
    }
  }

  public static class TimeSeries extends BaseModels.BaseModelTimeSeries<Trip> {

    TimeSeries() {
    }

    List<Trip> trips;

    @Override
    List<Trip> rawTimeSeriesContent() {
      return trips;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Trip> {

    Wrapper() {
    }

    Trip trip;

    @NonNull
    @Override
    public Trip extract() {
      return trip;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Trip>>() {
    }.getType();
  }
}
