package li.vin.netv2.request;

import android.support.annotation.NonNull;

/**
 * Specifies a resource type for which a request applies.
 */
public enum ForId {

  DEVICE("deviceId"),
  VEHICLE("vehicleId"),
  EVENT("eventId"),
  SUBSCRIPTION("subscriptionId"),
  TRIP("tripId");

  @NonNull private final String str;

  ForId(@NonNull String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return str;
  }

  @NonNull
  public static ForId fromString(@NonNull String str) {
    if ("deviceId".equals(str)) return DEVICE;
    if ("vehicleId".equals(str)) return VEHICLE;
    if ("eventId".equals(str)) return EVENT;
    if ("subscriptionId".equals(str)) return SUBSCRIPTION;
    if ("tripId".equals(str)) return TRIP;
    throw new IllegalArgumentException(
        "must be deviceId, vehicleId, eventId, subscriptionId, tripId.");
  }
}
