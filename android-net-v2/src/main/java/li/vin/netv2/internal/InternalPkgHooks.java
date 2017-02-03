package li.vin.netv2.internal;

import li.vin.netv2.model.misc.StrictGson;
import li.vin.netv2.util.Lazy;
import rx.functions.Func0;

public final class InternalPkgHooks {

  private InternalPkgHooks() {
  }

  public StrictGson strictGsonHook;

  static final Lazy<StrictGson> strictGson = //
      Lazy.create(new Func0<StrictGson>() {
        @Override
        public StrictGson call() {
          InternalPkgHooks hooks = new InternalPkgHooks();
          StrictGson.provideInst(hooks);
          return hooks.strictGsonHook;
        }
      });
}
