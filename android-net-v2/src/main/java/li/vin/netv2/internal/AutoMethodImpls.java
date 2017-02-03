package li.vin.netv2.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.Field;
import li.vin.netv2.model.ModelPkgHooks;
import li.vin.netv2.util.LazyOrSet;
import rx.functions.Func0;

import static java.lang.reflect.Modifier.isTransient;
import static li.vin.netv2.internal.InternalPkgHooks.strictGson;

public class AutoMethodImpls {

  AutoMethodImpls() {
  }

  // lazy init singleton inst

  static final LazyOrSet<AutoMethodImpls> inst = //
      LazyOrSet.create(new Func0<AutoMethodImpls>() {
        @Override
        public AutoMethodImpls call() {
          return new AutoMethodImpls();
        }
      });

  // inst providers

  public static void provideInst(ModelPkgHooks hooks) {
    hooks.autoMethodImplsHook = inst.get();
  }

  // default impl

  public boolean autoEquals(@NonNull Object tthis, @Nullable Object other) {
    try {
      if (tthis == other) return true;
      if (other == null || other.getClass() != tthis.getClass()) {
        return false;
      }
      for (Field f : CachedReflectiveFields.inst.get().allFields(tthis.getClass())) {
        if (isTransient(f.getModifiers())) continue;
        Object fVal = f.get(tthis);
        Object oVal = f.get(other);
        if (fVal != null
            ? !fVal.equals(oVal)
            : oVal != null) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public int autoHashCode(@NonNull Object tthis) {
    try {
      int result = 0;
      for (Field f : CachedReflectiveFields.inst.get().allFields(tthis.getClass())) {
        if (isTransient(f.getModifiers())) continue;
        Object fVal = f.get(tthis);
        result = 31 * result + (fVal != null
            ? fVal.hashCode()
            : 0);
      }
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String autoToString(@NonNull Object tthis) {
    return strictGson.get().gson().toJson(tthis);
  }
}
