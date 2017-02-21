package li.vin.netv2.internal;

import android.support.annotation.Nullable;
import java.util.Locale;
import okhttp3.HttpUrl;

import static java.lang.String.format;

enum Endpoint {

  AUTH("auth"),
  DIAGNOSTICS("diagnostic"),
  EVENTS("events"),
  PLATFORM("platform"),
  RULES("rules"),
  TELEMETRY("telemetry"),
  TRIPS("trips"),
  SAFETY("safety"),
  BEHAVIORAL("behavioral"),
  DISTANCE("distance"),
  DUMMY("dummies"),
  TOS("tos"),
  MY_VINLI("my-vinli");

  final String subDomain;
  final HttpUrl url;

  Endpoint(String subDomain) {
    this.subDomain = subDomain;
    this.url = new HttpUrl.Builder().scheme("https")
        .host(format(Locale.US, "%s.vin.li", subDomain))
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment("")
        .build();
  }

  String getUrl(@Nullable String env) {
    if (env == null) {
      env = "";
    } else if (!env.startsWith("-")) {
      env = format(Locale.US, "-%s", env);
    }
    return url.newBuilder()
        .host(format(Locale.US, "%s%s.vin.li", subDomain, env.toLowerCase(Locale.US)))
        .toString();
  }

}
