package li.vin.netv2.model;

import li.vin.netv2.internal.AutoMethodImpls;
import li.vin.netv2.internal.Maps;
import li.vin.netv2.util.Lazy;
import rx.functions.Func0;

public final class ModelPkgHooks {

  private ModelPkgHooks() {
  }

  public Maps mapsHook;

  static final Lazy<Maps> maps = //
      Lazy.create(new Func0<Maps>() {
        @Override
        public Maps call() {
          ModelPkgHooks hooks = new ModelPkgHooks();
          Maps.provideInst(hooks);
          return hooks.mapsHook;
        }
      });

  public AutoMethodImpls autoMethodImplsHook;

  static final Lazy<AutoMethodImpls> autoMethodImpls = //
      Lazy.create(new Func0<AutoMethodImpls>() {
        @Override
        public AutoMethodImpls call() {
          ModelPkgHooks hooks = new ModelPkgHooks();
          AutoMethodImpls.provideInst(hooks);
          return hooks.autoMethodImplsHook;
        }
      });
}
