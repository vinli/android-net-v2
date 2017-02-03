package li.vin.netv2.model.misc;

import li.vin.netv2.internal.CachedReflectiveFields;
import li.vin.netv2.internal.Maps;
import li.vin.netv2.util.Lazy;
import rx.functions.Func0;

public final class ModelMiscPkgHooks {

  private ModelMiscPkgHooks() {
  }

  public Maps mapsHook;

  static final Lazy<Maps> maps = //
      Lazy.create(new Func0<Maps>() {
        @Override
        public Maps call() {
          ModelMiscPkgHooks hooks = new ModelMiscPkgHooks();
          Maps.provideInst(hooks);
          return hooks.mapsHook;
        }
      });

  public CachedReflectiveFields cachedReflectiveFieldsHook;

  static final Lazy<CachedReflectiveFields> cachedReflectiveFields = //
      Lazy.create(new Func0<CachedReflectiveFields>() {
        @Override
        public CachedReflectiveFields call() {
          ModelMiscPkgHooks hooks = new ModelMiscPkgHooks();
          CachedReflectiveFields.provideInst(hooks);
          return hooks.cachedReflectiveFieldsHook;
        }
      });
}
