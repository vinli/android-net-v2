package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.model.misc.StrictValidations.AllowNull;
import li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;

import static li.vin.netv2.model.ModelPkgHooks.maps;

public class Subscription extends BaseModels.BaseModelId {

  Subscription() {

  }

  @AllowNull String vehicleId;
  @AllowNull String deviceId;
  String eventType;
  @AllowNull String url;
  @AllowNull String appData;
  @ReqIsoDate String createdAt;
  @ReqIsoDate String updatedAt;
  @AllowNull ObjectRef object;
  @ReqLink({
      "self", //
      "notifications" //
  }) Map links;

  @Nullable
  public String vehicleId() {
    return vehicleId;
  }

  @Nullable
  public String deviceId() {
    return deviceId;
  }

  @Nullable
  public String eventType() {
    return eventType;
  }

  @Nullable
  public String url() {
    return url;
  }

  @Nullable
  public String appData() {
    return appData;
  }

  @NonNull
  public String createdAt() {
    return createdAt;
  }

  @NonNull
  public String updatedAt() {
    return updatedAt;
  }

  @Nullable
  public ObjectRef objectRef() {
    return object;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Wrapper> vehicleLink() {
    return Link.create(maps.get().getStr(links, "notifications"));
  }

  public static class ObjectRef extends BaseModels.BaseModelId {

    ObjectRef() {
    }

    String type;

    public String type() {
      return type;
    }

    public static Type listType() {
      return new TypeToken<List<User.Settings>>() {
      }.getType();
    }
  }



  public static class Wrapper extends BaseModels.BaseModelWrapper<Subscription> {

    Wrapper() {

    }

    Subscription subscription;

    @NonNull
    @Override
    public Subscription extract() {
      return subscription;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<User.Settings>>() {
      }.getType();
    }
  }

  public static class Page extends BaseModels.BaseModelPage<Subscription> {

    Page() {
    }

    List<Subscription> subscriptions;

    @Override
    List<Subscription> rawPageContent() {
      return subscriptions;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Subscription.Page>>() {
      }.getType();
    }
  }
}
