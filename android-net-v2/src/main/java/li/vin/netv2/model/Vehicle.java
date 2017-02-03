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

public class Vehicle extends BaseModels.BaseModelId {

  Vehicle() {
  }

  /*
  const vehicleSchema = Joi.object({

      id: Joi.string().guid().required(),

      createdAt: Joi.date().iso().required(),

      vin: Joi.string().required().allow(null),
      make: Joi.string().required().allow(null),
      model: Joi.string().required().allow(null),
      year: Joi.string().required().allow(null),
      trim: Joi.string().required().allow(null),

      lastStartup: Joi.date().iso().optional(),

      data: Joi.object({

        engine: Joi.object().unknown().required().allow(null),
        engineDisplacement: Joi.number().required().allow(null),

        transmission: Joi.object({

          // id: Joi.string().required(),
          // name: Joi.string().required(),
          // equipmentType: Joi.string().required(),
          // availability: Joi.string().required(),
          // automaticType: Joi.string().required(),
          // transmissionType: Joi.string().required(),
          // numberOfSpeeds: Joi.number().integer().required()

        }).unknown().required().allow(null),

        manufacturer: Joi.string().required().allow(null),

        categories: Joi.object({

          // market: Joi.string().required(),
          // EPAClass: Joi.string().required(),
          // vehicleSize: Joi.string().required(),
          // primaryBodyType: Joi.string().required(),
          // vehicleStyle: Joi.string().required(),
          // vehicleType: Joi.string().required(),
          // manufacturerCabType: Joi.string().required()

        }).unknown().required().allow(null),

        epaMpg: Joi.object({

          highway: Joi.number().required(),
          city: Joi.number().required()

        }).required().allow(null),

        drive: Joi.string().required().allow(null),

        numDoors: Joi.number().integer().required().allow(null)

      }).required().allow(null),

      links: Joi.object({

        self: Joi.string().uri().required(),
        trips: Joi.string().uri().required(),
        codes: Joi.string().uri().required(),
        collisions: Joi.string().uri().required()

      }).required()
  });
   */

  @ReqIsoDate String createdAt;

  @AllowNull String vin;
  @AllowNull String make;
  @AllowNull String model;
  @AllowNull String year;
  @AllowNull String trim;

  @AllowNull @OptIsoDate String lastStartup;

  @AllowNull Map data;

  @ReqLink({ //
      "self", //
      "trips", //
      "codes", //
      "collisions" //
  }) Map links;

  @NonNull
  public String createdAt() {
    return createdAt;
  }

  @Nullable
  public String vin() {
    return vin;
  }

  @Nullable
  public String make() {
    return make;
  }

  @Nullable
  public String model() {
    return model;
  }

  @Nullable
  public String year() {
    return year;
  }

  @Nullable
  public String trim() {
    return trim;
  }

  @Nullable
  public String lastStartup() {
    return lastStartup;
  }

  @NonNull
  public Data data() {
    return Data.create(data);
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Trip.TimeSeries> tripsLink() {
    return Link.create(maps.get().getStr(links, "trips"));
  }

  @NonNull
  public Link<Dtc.TimeSeries> codesLink() {
    return Link.create(maps.get().getStr(links, "codes"));
  }

  @NonNull
  public Link<Collision.TimeSeries> collisionsLink() {
    return Link.create(maps.get().getStr(links, "collisions"));
  }

  public static class Page extends BaseModels.BaseModelPage<Vehicle> {

    Page() {
    }

    List<Vehicle> vehicles;

    @Override
    List<Vehicle> rawPageContent() {
      return vehicles;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Page>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Vehicle> {

    Wrapper() {
    }

    Vehicle vehicle;

    @NonNull
    @Override
    public Vehicle extract() {
      return vehicle;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Vehicle>>() {
    }.getType();
  }
}
