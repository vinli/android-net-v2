package li.vin.netv2.model;

import android.support.annotation.NonNull;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.request.RequestPkgHooks;

/**
 * Created by JoshBeridon on 2/9/17.
 */

public class OdometerTriggerSeed extends BaseModels.BaseModel implements ModelSeed {

  @NonNull
  public static OdometerTriggerSeed create() {
    return new OdometerTriggerSeed(null, null, null);
  }

  OdometerTriggerSeed(final OdometerTrigger.TriggerType type, final Double threshold,
      final Distance.Unit unit) {
    this.type = type;
    this.threshold = threshold;
    this.unit = unit;
  }

  final OdometerTrigger.TriggerType type;
  final Double threshold;
  final Distance.Unit unit;

  public OdometerTriggerSeed type(@NonNull OdometerTrigger.TriggerType type) {
    return new OdometerTriggerSeed(type, threshold, unit);
  }

  public OdometerTriggerSeed threshold(@NonNull Double threshold) {
    return new OdometerTriggerSeed(type, threshold, unit);
  }

  public OdometerTriggerSeed unit(@NonNull Distance.Unit unit) {
    return new OdometerTriggerSeed(type, threshold, unit);
  }

  @Override
  public void validate() {
    if (type == null) throw new IllegalArgumentException("type required");
    if (unit == null) throw new IllegalArgumentException("unit required");
    if (threshold == null) throw new IllegalArgumentException("threshold required");
  }

  public static class Wrapper {
    Wrapper() {

    }

    OdometerTriggerSeed odometerTriggerSeed;

    public static void provideWrapper(RequestPkgHooks hooks, OdometerTriggerSeed odometerTriggerSeed) {
      hooks.odometerTriggerSeedWrapperHook = new Wrapper();
      hooks.odometerTriggerSeedWrapperHook.odometerTriggerSeed = odometerTriggerSeed;
    }
  }
}
