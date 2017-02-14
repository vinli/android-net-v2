package li.vin.netv2.model;

import android.support.annotation.NonNull;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.request.RequestPkgHooks;

public class SubscriptionSeed extends BaseModels.BaseModel implements ModelSeed {

  public SubscriptionSeed(final String eventType, final String appData, final String url) {
    this.eventType = eventType;
    this.appData = appData;
    this.url = url;
  }

  @NonNull
  public static SubscriptionSeed create() {
    return new SubscriptionSeed(null, null, null);
  }

  final String eventType;
  final String appData;
  final String url;

  public SubscriptionSeed eventType(String eventType) {
    return new SubscriptionSeed(eventType, appData, url);
  }

  public SubscriptionSeed url(String url) {
    return new SubscriptionSeed(eventType, appData, url);
  }

  public SubscriptionSeed appData(String appData) {
    return new SubscriptionSeed(eventType, appData, url);
  }

  @Override
  public void validate() {
    if (eventType == null) throw new IllegalArgumentException("eventType required");
    if (url == null) throw new IllegalArgumentException("url required");
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
