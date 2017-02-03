package li.vin.netv2.internal;

import okhttp3.OkHttpClient;

public final class InternalTestUtil {

  private InternalTestUtil() {
  }

  public static OkHttpClient client(CachedHttpClients.ClientAndServices c) {
    return c.client;
  }
}
