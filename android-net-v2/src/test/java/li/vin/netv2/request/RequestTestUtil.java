package li.vin.netv2.request;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static li.vin.netv2.TestUtil.generateUnsafeBuilder;
import static li.vin.netv2.internal.InternalTestUtil.client;

public final class RequestTestUtil {

  private RequestTestUtil() {
  }

  public static VinliRequest.Builder baseBuilder() {
    return baseBuilder(null);
  }

  public static VinliRequest.Builder baseBuilder(String env) {
    return VinliRequest.builder()
        .logLevel(HttpLoggingInterceptor.Level.HEADERS)
        .clientBuilder(generateUnsafeBuilder())
        .env(env);
  }

  public static OkHttpClient clientFromBuilder(VinliRequest.Builder b) {
    return client(b.validateAndGetClient());
  }

  public static String accessTokenFromBuilder(VinliRequest.Builder b) {
    return b.accessToken;
  }
}
