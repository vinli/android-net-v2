package li.vin.netv2.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import li.vin.netv2.BuildConfig;
import li.vin.netv2.model.misc.ModelMiscPkgHooks;
import li.vin.netv2.util.LazyOrSet;
import rx.functions.Func0;

import static java.util.Collections.addAll;

public class CachedReflectiveFields {

  CachedReflectiveFields() {
  }

  // lazy init singleton inst

  static final LazyOrSet<CachedReflectiveFields> inst = //
      LazyOrSet.create(new Func0<CachedReflectiveFields>() {
        @Override
        public CachedReflectiveFields call() {
          return new CachedReflectiveFields();
        }
      });

  // inst providers

  public static void provideInst(ModelMiscPkgHooks hooks) {
    hooks.cachedReflectiveFieldsHook = inst.get();
  }

  // default impl

  public List<Field> allFields(Class<?> cls) {
    // We do some LRU caching of lookup results since we (ab)use reflection so we can maintain
    // simple Java models without code-gen dependencies and still avoid boilerplate.
    List<Field> fields = allFieldsCache.get(cls);
    if (fields == null) allFieldsCache.put(cls, fields = allFields(cls, null));
    return fields;
  }

  private List<Field> allFields(Class<?> cls, List<Field> fields) {
    if (fields == null) fields = new ArrayList<>();
    if (cls == null || cls == java.lang.Object.class) return fields;
    addAll(fields, cls.getDeclaredFields());
    allFields(cls.getSuperclass(), fields);
    for (Field f : fields) f.setAccessible(true);
    return fields;
  }

  private final android.support.v4.util.LruCache<Class, List<Field>> allFieldsCache =
      new android.support.v4.util.LruCache<>(BuildConfig.FIELDS_CACHE_SIZE);
}
