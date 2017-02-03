package li.vin.netv2.request;

import android.support.annotation.NonNull;
import li.vin.netv2.model.Device;
import li.vin.netv2.model.Event;
import li.vin.netv2.model.Trip;
import li.vin.netv2.model.Vehicle;
import li.vin.netv2.util.DeepCopyable;

import static java.lang.String.format;

/**
 * Defines a resource type ({@link ForId}) and id for which a request applies.
 */
public final class ForIdBuilder<B extends DeepCopyable<B>> {

  @NonNull final B builder;

  ForIdBuilder(@NonNull B builder) {
    this.builder = builder.copy();
  }

  /** Specify a {@link ForId} type and id required by the given request. */
  public B forId(@NonNull ForId forId, @NonNull String forIdTarget) {
    if (builder instanceof TimeSeriesBuilder) {
      TimeSeriesBuilder tsb = (TimeSeriesBuilder) builder;
      //noinspection unchecked
      tsb.forIdVals = new ForIdVals(forId, forIdTarget, tsb.allowedIds);
    } else if (builder instanceof PageBuilder) {
      PageBuilder pb = (PageBuilder) builder;
      //noinspection unchecked
      pb.forIdVals = new ForIdVals(forId, forIdTarget, pb.allowedIds);
    } else if (builder instanceof WrapperBuilder) {
      WrapperBuilder wb = (WrapperBuilder) builder;
      //noinspection unchecked
      wb.forIdVals = new ForIdVals(forId, forIdTarget, wb.allowedIds);
    } else {
      throw new IllegalStateException(
          format("Unknown builder %s", builder.getClass().getSimpleName()));
    }
    return builder;
  }

  /** Convenience to choose {@link ForId#DEVICE} with a given {@link Device#id()}. */
  public B forDevice(@NonNull Device device) {
    return forId(ForId.DEVICE, device.id());
  }

  /** Convenience to choose {@link ForId#VEHICLE} with a given {@link Vehicle#id()}. */
  public B forVehicle(@NonNull Vehicle vehicle) {
    return forId(ForId.VEHICLE, vehicle.id());
  }

  /** Convenience to choose {@link ForId#EVENT} with a given {@link Event#id()}. */
  public B forEvent(@NonNull Event event) {
    return forId(ForId.EVENT, event.id());
  }

  // TODO
  ///** Convenience to choose {@link ForId#SUBSCRIPTION} with a given {@link Subscription#id()}. */
  //public B forSubscription(@NonNull Subscription subscription) {
  //  return forId(ForId.SUBSCRIPTION, subscription.id());
  //}

  /** Convenience to choose {@link ForId#TRIP} with a given {@link Trip#id()}. */
  public B forTrip(@NonNull Trip trip) {
    return forId(ForId.TRIP, trip.id());
  }
}
