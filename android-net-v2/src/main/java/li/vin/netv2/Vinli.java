package li.vin.netv2;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import li.vin.netv2.model.misc.IsoDateFormat;
import li.vin.netv2.model.misc.StrictGson;
import li.vin.netv2.request.VinliRequest;
import li.vin.netv2.util.Lazy;
import rx.functions.Func0;

/**
 * Common Vinli functionality is statically accessible here. See {@link VinliRequest#builder()} for
 * the main entry point to building network requests.
 */
public final class Vinli {

  private Vinli() {
  }

  /**
   * Gson instance used internally for all Vinli models. Strictly validates all model fields, so
   * parsing will fail rather than generate a model that violates its validation contracts, such as
   * nullability. Vinli models can be parsed with any out-of-the-box Gson instance - but if a model
   * is not deserialized with this particular instance, validations will not be strictly enforced,
   * and a model that defies its validation contracts may be produced.
   */
  @NonNull
  public static Gson strictGson() {
    return MainPkgHooks.strictGson.get().gson();
  }

  /**
   * Produces a {@link SimpleDateFormat} that parses Vinli model dates, which are just ISO dates in
   * UTC. Won't work properly for ISO dates stored with a timezone other than UTC.
   */
  @NonNull
  public static SimpleDateFormat isoUtcDateFormat() {
    // Clone since it's not safe to give out the inst, DateFormat has internal mutable state :(
    return (SimpleDateFormat) MainPkgHooks.isoDateFormat.get().get().clone();
  }

  public static final class MainPkgHooks {

    private MainPkgHooks() {
    }

    public StrictGson strictGsonHook;

    private static final Lazy<StrictGson> strictGson = //
        Lazy.create(new Func0<StrictGson>() {
          @Override
          public StrictGson call() {
            MainPkgHooks hooks = new MainPkgHooks();
            StrictGson.provideInst(hooks);
            return hooks.strictGsonHook;
          }
        });

    public IsoDateFormat isoDateFormatHook;

    private static final Lazy<IsoDateFormat> isoDateFormat = //
        Lazy.create(new Func0<IsoDateFormat>() {
          @Override
          public IsoDateFormat call() {
            MainPkgHooks hooks = new MainPkgHooks();
            IsoDateFormat.provideInst(hooks);
            return hooks.isoDateFormatHook;
          }
        });
  }
}
