package li.vin.netv2.model.misc;

import com.google.gson.Gson;

public final class ModelMiscTestUtil {

  private ModelMiscTestUtil() {
  }

  public static void checkStrict(Object val) {
    StrictValidations.inst.get().checkStrict(val);
  }

  public static Gson gson() {
    return StrictGson.inst.get().gson();
  }
}
