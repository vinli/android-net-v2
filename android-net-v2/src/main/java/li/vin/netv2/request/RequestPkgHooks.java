package li.vin.netv2.request;

import li.vin.netv2.internal.CachedHttpClients;
import li.vin.netv2.model.DtcDiagnosis;
import li.vin.netv2.model.OdometerSeed;
import li.vin.netv2.model.OdometerTriggerSeed;
import li.vin.netv2.model.RuleSeed;
import li.vin.netv2.model.misc.IsoDateFormat;
import li.vin.netv2.util.Lazy;
import rx.functions.Func0;

public final class RequestPkgHooks {

  private RequestPkgHooks() {
  }

  public CachedHttpClients cachedHttpClientsHook;

  static final Lazy<CachedHttpClients> cachedHttpClients =
      Lazy.create(new Func0<CachedHttpClients>() {
        @Override
        public CachedHttpClients call() {
          RequestPkgHooks hooks = new RequestPkgHooks();
          CachedHttpClients.provideInst(hooks);
          return hooks.cachedHttpClientsHook;
        }
      });

  public IsoDateFormat isoDateFormatHook;

  static final Lazy<IsoDateFormat> isoDateFormat = //
      Lazy.create(new Func0<IsoDateFormat>() {
        @Override
        public IsoDateFormat call() {
          RequestPkgHooks hooks = new RequestPkgHooks();
          IsoDateFormat.provideInst(hooks);
          return hooks.isoDateFormatHook;
        }
      });

  public DtcDiagnosis.Wrapper dtcDiagWrapperHook;

  static DtcDiagnosis.Wrapper dtcDiagWrapper(DtcDiagnosis dtcDiagnosis) {
    RequestPkgHooks hooks = new RequestPkgHooks();
    DtcDiagnosis.Wrapper.provideWrapper(hooks, dtcDiagnosis);
    return hooks.dtcDiagWrapperHook;
  }

  public RuleSeed.Wrapper ruleSeedWrapperHook;

  static RuleSeed.Wrapper ruleSeedWrapper(RuleSeed ruleSeed) {
    RequestPkgHooks hooks = new RequestPkgHooks();
    RuleSeed.Wrapper.provideWrapper(hooks, ruleSeed);
    return hooks.ruleSeedWrapperHook;
  }

  public OdometerSeed.Wrapper odometerSeedWrapperHook;

  static OdometerSeed.Wrapper odometerSeedWrapper(OdometerSeed odometerSeed) {
    RequestPkgHooks hooks = new RequestPkgHooks();
    OdometerSeed.Wrapper.provideWrapper(hooks, odometerSeed);
    return hooks.odometerSeedWrapperHook;
  }

  public OdometerTriggerSeed.Wrapper odometerTriggerSeedWrapperHook;

  static OdometerTriggerSeed.Wrapper odometerTriggerSeedWrapper(OdometerTriggerSeed odometerTriggerSeed) {
    RequestPkgHooks hooks = new RequestPkgHooks();
    OdometerTriggerSeed.Wrapper.provideWrapper(hooks, odometerTriggerSeed);
    return hooks.odometerTriggerSeedWrapperHook;
  }
}
