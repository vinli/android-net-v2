package li.vin.netv2.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.ModelPkgHooks;
import li.vin.netv2.model.misc.ModelMiscPkgHooks;
import li.vin.netv2.util.LazyOrSet;
import rx.functions.Func0;

import static java.lang.String.format;

public class Maps {

  Maps() {
  }

  // lazy init singleton inst

  static final LazyOrSet<Maps> inst = //
      LazyOrSet.create(new Func0<Maps>() {
        @Override
        public Maps call() {
          return new Maps();
        }
      });

  // inst providers

  public static void provideInst(ModelPkgHooks hooks) {
    hooks.mapsHook = inst.get();
  }

  public static void provideInst(ModelMiscPkgHooks hooks) {
    hooks.mapsHook = inst.get();
  }

  // default impl

  @NonNull
  public Object getObj(@Nullable Map ltm, @NonNull String key) {
    if (ltm == null) throw new RuntimeException(format("%s not an obj.", key));
    String[] split = key.split("\\.");
    if (split.length == 0) throw new RuntimeException(format("%s not an obj.", key));
    if (split.length == 1) {
      Object o = ltm.get(split[0]);
      if (o == null) throw new RuntimeException(format("%s not an obj.", key));
      return o;
    }
    Object nxt = ltm.get(split[0]);
    if (!(nxt instanceof Map)) throw new RuntimeException(format("%s not an obj.", key));
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < split.length; i++) {
      if (sb.length() != 0) sb.append('.');
      sb.append(split[i]);
    }
    return getObj((Map) nxt, sb.toString());
  }

  @Nullable
  public Object getObjNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getObj(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public <T> List<T> getList(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    if (o instanceof Collection) return new ArrayList<>((Collection<? extends T>) o);
    if (o instanceof Object[]) {
      Object[] oa = (Object[]) o;
      ArrayList<T> l = new ArrayList<>(oa.length);
      for (Object oo : oa) l.add((T) oo);
      return l;
    }
    throw new RuntimeException(format("%s not an array or collection.", key));
  }

  @Nullable
  public <T> List<T> getListNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getList(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  @NonNull
  public String getStr(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    if (o instanceof String) return (String) o;
    throw new RuntimeException(format("%s not a string.", key));
  }

  @Nullable
  public String getStrNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getStr(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  @NonNull
  public Integer getInt(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    if (o instanceof Number) return ((Number) o).intValue();
    throw new RuntimeException(format("%s not an int.", key));
  }

  @Nullable
  public Integer getIntNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getInt(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  @NonNull
  public Double getDbl(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    if (o instanceof Number) return ((Number) o).doubleValue();
    throw new RuntimeException(format("%s not a double.", key));
  }

  @Nullable
  public Double getDblNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getDbl(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  @NonNull
  public Boolean getBool(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    if (o instanceof Boolean) return (Boolean) o;
    throw new RuntimeException(format("%s not a bool.", key));
  }

  @Nullable
  public Boolean getBoolNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getBool(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }

  public double[] validateLatLon(Object[] oa, String key) {
    if (oa.length != 2) throw new RuntimeException(format("%s not a tuple of doubles.", key));
    double lat = ((Number) oa[1]).doubleValue();
    double lon = ((Number) oa[0]).doubleValue();
    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
      throw new RuntimeException(format("%s doubles outside of lat / lon bounds.", key));
    }
    return new double[] { lat, lon };
  }

  @NonNull
  public double[] getLatLon(@Nullable Map ltm, @NonNull String key) {
    Object o = getObj(ltm, key);
    Object[] oa;
    if (o instanceof Object[]) {
      oa = (Object[]) o;
    } else if (o instanceof Collection) {
      oa = ((Collection) o).toArray();
    } else if (o instanceof Map) {
      Map oltm = (Map) o;
      oa = new Object[] { oltm.get("lon"), oltm.get("lat") };
    } else {
      throw new RuntimeException(format("%s not a tuple of doubles.", key));
    }
    return validateLatLon(oa, key);
  }

  @Nullable
  public double[] getLatLonNullable(@Nullable Map ltm, @NonNull String key) {
    try {
      return getLatLon(ltm, key);
    } catch (Exception e) {
      return null;
    }
  }
}
