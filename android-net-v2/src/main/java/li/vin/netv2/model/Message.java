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
import static li.vin.netv2.model.misc.StrictValidations.OptDouble;
import static li.vin.netv2.model.misc.StrictValidations.OptLatLon;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Message extends BaseModelId {

  Message() {
  }

  @ReqIsoDate String timestamp;

  @OptDouble({ //
      "accel.maxX", //
      "accel.maxY", //
      "accel.maxZ", //
      "accel.minX", //
      "accel.minY", //
      "accel.minZ" //
  }) //
  @OptLatLon({ //
      "location.coordinates"
  }) Map data;

  @ReqLink({ //
      "self" //
  }) Map links;

  @NonNull
  public String timestamp() {
    return timestamp;
  }

  @NonNull
  public Data data() {
    return Data.create(data);
  }

  @Nullable
  public Double accelMaxX() {
    return maps.get().getDblNullable(data, "accel.maxX");
  }

  @Nullable
  public Double accelMaxY() {
    return maps.get().getDblNullable(data, "accel.maxY");
  }

  @Nullable
  public Double accelMaxZ() {
    return maps.get().getDblNullable(data, "accel.maxZ");
  }

  @Nullable
  public Double accelMinX() {
    return maps.get().getDblNullable(data, "accel.minX");
  }

  @Nullable
  public Double accelMinY() {
    return maps.get().getDblNullable(data, "accel.minY");
  }

  @Nullable
  public Double accelMinZ() {
    return maps.get().getDblNullable(data, "accel.minZ");
  }

  @Nullable
  public double[] locationLatLon() {
    return maps.get().getLatLonNullable(data, "location.coordinates");
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  public static class TimeSeries extends BaseModelTimeSeries<Message> {

    TimeSeries() {
    }

    List<Message> messages;

    @Override
    List<Message> rawTimeSeriesContent() {
      return messages;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<TimeSeries>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModelWrapper<Message> {

    Wrapper() {
    }

    Message message;

    @NonNull
    @Override
    public Message extract() {
      return message;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Message>>() {
    }.getType();
  }
}
