package li.vin.netv2.model;

import android.support.annotation.NonNull;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.request.RequestPkgHooks;

public class SubscriptionSeed extends BaseModels.BaseModel implements ModelSeed {

  public SubscriptionSeed(final String eventType, final String appData, final String url, final
     ObjectRefSeed object) {
    this.eventType = eventType;
    this.appData = appData;
    this.url = url;
    this.object = object;
  }

  @NonNull
  public static SubscriptionSeed create() {
    return new SubscriptionSeed(null, null, null, null);
  }

  final String eventType;
  final String appData;
  final String url;
  final ObjectRefSeed object;

  public SubscriptionSeed eventType(String eventType) {
    return new SubscriptionSeed(eventType, appData, url, object);
  }

  public SubscriptionSeed url(String url) {
    return new SubscriptionSeed(eventType, appData, url, object);
  }

  public SubscriptionSeed appData(String appData) {
    return new SubscriptionSeed(eventType, appData, url, object);
  }

  public SubscriptionSeed object(ObjectRefSeed object) {
    return new SubscriptionSeed(eventType, appData, url, object);
  }

  @Override
  public void validate() {
    if (eventType == null) throw new IllegalArgumentException("eventType required");
    if (url == null) throw new IllegalArgumentException("url required");
  }

  public static class ObjectRefSeed extends BaseModels.BaseModel implements ModelSeed {

    public static ObjectRefSeed create() {
      return new ObjectRefSeed(null, null);
    }

    final String id;
    final String type;

    ObjectRefSeed(final String id, final String type) {
      this.id = id;
      this.type = type;
    }

    @NonNull
    public ObjectRefSeed id(String id) {
      return new ObjectRefSeed(id, type);
    }

    @NonNull
    public ObjectRefSeed type(String type) {
      return new ObjectRefSeed(id, type);
    }

    @Override
    public void validate() {
      if (id == null) throw new IllegalArgumentException("id required");
      if (type == null) throw new IllegalArgumentException("type required");
    }
  }

  public static class Wrapper {
    Wrapper() {

    }

    SubscriptionSeed subscription;

    public static void provideWrapper(RequestPkgHooks hooks, SubscriptionSeed subscriptionSeed) {
      hooks.subscriptionSeedWrapperHook = new Wrapper();
      hooks.subscriptionSeedWrapperHook.subscription = subscriptionSeed;
    }
  }
}
