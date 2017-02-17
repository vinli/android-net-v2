package li.vin.netv2.model;

import android.support.annotation.NonNull;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.request.RequestPkgHooks;

public class OdometerSeed extends BaseModels.BaseModel implements ModelSeed {

  @NonNull
  public static OdometerSeed create() {
    return new OdometerSeed(null, null, null);
  }

  OdometerSeed(final Double reading, final String timestamp, final String unit) {
    this.reading = reading;
    this.timestamp = timestamp;
    this.unit = unit;
  }

  final Double reading;
  final String timestamp;
  final String unit;

  public OdometerSeed reading(@NonNull Double reading) {
    return new OdometerSeed(reading, timestamp, unit);
  }

  public OdometerSeed timestamp(@NonNull String timestamp) {
    return new OdometerSeed(reading, timestamp, unit);
  }

  public OdometerSeed unit(@NonNull String unit) {
    return new OdometerSeed(reading, timestamp, unit);
  }

  @Override
  public void validate() {
    if (reading == null) throw new IllegalArgumentException("reading required");
    if (unit == null) throw new IllegalArgumentException("unit required");
  }

  public static class Wrapper {

    Wrapper() {
    }

    OdometerSeed odometer;

    public static void provideWrapper(RequestPkgHooks hooks, OdometerSeed odometerSeed) {
      hooks.odometerSeedWrapperHook = new Wrapper();
      hooks.odometerSeedWrapperHook.odometer = odometerSeed;
    }
  }
}
