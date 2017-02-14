package li.vin.netv2.model.misc;

import com.google.gson.JsonParseException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.util.LazyOrSet;
import okhttp3.HttpUrl;
import rx.functions.Func0;

import static java.lang.String.format;
import static li.vin.netv2.model.misc.ModelMiscPkgHooks.cachedReflectiveFields;
import static li.vin.netv2.model.misc.ModelMiscPkgHooks.maps;

public class StrictValidations {

  StrictValidations() {
  }

  // lazy init singleton inst

  static final LazyOrSet<StrictValidations> inst = //
      LazyOrSet.create(new Func0<StrictValidations>() {
        @Override
        public StrictValidations call() {
          return new StrictValidations();
        }
      });

  // default impl

  // TODO - break ubermethod this into smaller pieces
  public void checkStrict(Object val) {
    if (val == null || !(val instanceof StrictModel)) return;

    try {
      for (Field f : cachedReflectiveFields.get().allFields(val.getClass())) {

        Object o = f.get(val);

        if (!f.isAnnotationPresent(AllowNull.class) && o == null) {
          throw new JsonParseException(format("null found for not-nullable field %s", f.getName()));
        }

        // ------- VALIDATE ISO DATES ------- //

        ReqIsoDate reqDt = f.getAnnotation(ReqIsoDate.class);
        if (reqDt != null) {
          if (o instanceof String) {
            if (reqDt.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            isoDateFormat().get().parse((String) o).getTime();
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (reqDt.value().length == 0) throw new IllegalArgumentException("vals required.");
            for (String k : reqDt.value()) {
              isoDateFormat().get().parse(maps.get().getStr(ltm, k)).getTime();
            }
          } else {
            throw new JsonParseException(
                format("non-date found for ISO date field %s", f.getName()));
          }
        }

        OptIsoDate optDt = f.getAnnotation(OptIsoDate.class);
        if (optDt != null) {
          if (o instanceof String) {
            if (optDt.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            isoDateFormat().get().parse((String) o).getTime();
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (optDt.value().length == 0) throw new IllegalArgumentException("vals required.");
            for (String k : optDt.value()) {
              Object v = maps.get().getObjNullable(ltm, k);
              if (v instanceof String) {
                isoDateFormat().get().parse((String) v).getTime();
              } else if (v != null) {
                throw new JsonParseException(
                    format("non-date found for ISO date field %s", f.getName()));
              }
            }
          } else if (o != null) {
            throw new JsonParseException(
                format("non-date found for ISO date field %s", f.getName()));
          }
        }

        // ------- VALIDATE LAT LON ------- //

        ReqLatLon reqLtLn = f.getAnnotation(ReqLatLon.class);
        if (reqLtLn != null) {
          if (o instanceof Object[]) {
            if (reqLtLn.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            maps.get().validateLatLon((Object[]) o, f.getName());
          } else if (o instanceof Collection) {
            if (reqLtLn.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            maps.get().validateLatLon(((Collection) o).toArray(), f.getName());
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (reqLtLn.value().length == 0) {
              maps.get().validateLatLon( //
                  new Object[] { ltm.get("lon"), ltm.get("lat") }, f.getName());
            } else {
              for (String k : reqLtLn.value()) {
                maps.get().getLatLon(ltm, k);
              }
            }
          } else {
            throw new JsonParseException(
                format("non-lat/lon found for lat/lon field %s", f.getName()));
          }
        }

        OptLatLon optLtLn = f.getAnnotation(OptLatLon.class);
        if (optLtLn != null) {
          if (o instanceof Object[]) {
            if (optLtLn.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            maps.get().validateLatLon((Object[]) o, f.getName());
          } else if (o instanceof Collection) {
            if (optLtLn.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            maps.get().validateLatLon(((Collection) o).toArray(), f.getName());
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (optLtLn.value().length == 0) {
              maps.get().validateLatLon( //
                  new Object[] { ltm.get("lon"), ltm.get("lat") }, f.getName());
            } else {
              for (String k : optLtLn.value()) {
                Object v = maps.get().getObjNullable(ltm, k);
                if (v != null && !(v instanceof Collection) && !(v instanceof Object[])) {
                  throw new JsonParseException(
                      format("non-lat/lon found for lat/lon field %s", f.getName()));
                }
              }
            }
          } else if (o != null) {
            throw new JsonParseException(
                format("non-lat/lon found for lat/lon field %s", f.getName()));
          }
        }

        // -------- VALIDATE LINKS -------- //

        ReqLink reqLnk = f.getAnnotation(ReqLink.class);
        if (reqLnk != null) {
          if (o instanceof String) {
            if (reqLnk.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            if (HttpUrl.parse((String) o) == null) {
              throw new JsonParseException(format("non-link found for link field %s", f.getName()));
            }
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (reqLnk.value().length == 0) throw new IllegalArgumentException("vals required.");
            for (String k : reqLnk.value()) {
              if (HttpUrl.parse(maps.get().getStr(ltm, k)) == null) {
                throw new JsonParseException(
                    format("non-link found for link field %s", f.getName()));
              }
            }
          } else {
            throw new JsonParseException(format("non-link found for link field %s", f.getName()));
          }
        }

        OptLink optLnk = f.getAnnotation(OptLink.class);
        if (optLnk != null) {
          if (o instanceof String) {
            if (optLnk.value().length != 0) throw new IllegalArgumentException("no vals allowed.");
            if (HttpUrl.parse((String) o) == null) {
              throw new JsonParseException(format("non-link found for link field %s", f.getName()));
            }
          } else if (o instanceof Map) {
            Map ltm = (Map) o;
            if (optLnk.value().length == 0) throw new IllegalArgumentException("vals required.");
            for (String k : optLnk.value()) {
              Object v = maps.get().getObjNullable(ltm, k);
              if (v != null && (!(v instanceof String) || HttpUrl.parse((String) v) == null)) {
                throw new JsonParseException(
                    format("non-link found for link field %s", f.getName()));
              }
            }
          } else if (o != null) {
            throw new JsonParseException(format("non-link found for link field %s", f.getName()));
          }
        }

        // -------- VALIDATE STRINGS -------- //

        ReqStr reqStr = f.getAnnotation(ReqStr.class);
        if (reqStr != null) {
          if (!(o instanceof Map)) {
            throw new JsonParseException(
                format("reqd fields not found: %s", Arrays.toString(reqStr.value())));
          }
          Map ltm = (Map) o;
          if (reqStr.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : reqStr.value()) {
            maps.get().getStr(ltm, k);
          }
        }

        OptStr optStr = f.getAnnotation(OptStr.class);
        if (optStr != null && o instanceof Map) {
          Map ltm = (Map) o;
          if (optStr.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : optStr.value()) {
            Object v = maps.get().getObjNullable(ltm, k);
            if (v != null && !(v instanceof String)) {
              throw new JsonParseException(format("non-str found for str field %s", f.getName()));
            }
          }
        }

        // ---------- VALIDATE INTS --------- //

        ReqInt reqInt = f.getAnnotation(ReqInt.class);
        if (reqInt != null) {
          if (!(o instanceof Map)) {
            throw new JsonParseException(
                format("reqd fields not found: %s", Arrays.toString(reqInt.value())));
          }
          Map ltm = (Map) o;
          if (reqInt.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : reqInt.value()) {
            maps.get().getInt(ltm, k);
          }
        }

        OptInt optInt = f.getAnnotation(OptInt.class);
        if (optInt != null && o instanceof Map) {
          Map ltm = (Map) o;
          if (optInt.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : optInt.value()) {
            Object v = maps.get().getObjNullable(ltm, k);
            if (v != null && !(v instanceof Number)) {
              throw new JsonParseException(format("non-int found for int field %s", f.getName()));
            }
          }
        }

        // -------- VALIDATE DOUBLES -------- //

        ReqDouble reqDbl = f.getAnnotation(ReqDouble.class);
        if (reqDbl != null) {
          if (!(o instanceof Map)) {
            throw new JsonParseException(
                format("reqd fields not found: %s", Arrays.toString(reqDbl.value())));
          }
          Map ltm = (Map) o;
          if (reqDbl.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : reqDbl.value()) {
            maps.get().getDbl(ltm, k);
          }
        }

        OptDouble optDbl = f.getAnnotation(OptDouble.class);
        if (optDbl != null && o instanceof Map) {
          Map ltm = (Map) o;
          if (optDbl.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : optDbl.value()) {
            Object v = maps.get().getObjNullable(ltm, k);
            if (v != null && !(v instanceof Number)) {
              throw new JsonParseException(
                  format("non-double found for double field %s", f.getName()));
            }
          }
        }

        // -------- VALIDATE BOOLS -------- //

        ReqBool reqBl = f.getAnnotation(ReqBool.class);
        if (reqBl != null) {
          if (!(o instanceof Map)) {
            throw new JsonParseException(
                format("reqd fields not found: %s", Arrays.toString(reqBl.value())));
          }
          Map ltm = (Map) o;
          if (reqBl.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : reqBl.value()) {
            maps.get().getBool(ltm, k);
          }
        }

        OptBool optBl = f.getAnnotation(OptBool.class);
        if (optBl != null && o instanceof Map) {
          Map ltm = (Map) o;
          if (optBl.value().length == 0) throw new IllegalArgumentException("vals required.");
          for (String k : optBl.value()) {
            Object v = maps.get().getObjNullable(ltm, k);
            if (v != null && !(v instanceof Boolean)) {
              throw new JsonParseException(format("non-bool found for bool field %s", f.getName()));
            }
          }
        }

        // ---------------------------------- //

        checkStrict(o);
      }
    } catch (Exception e) {
      throw new JsonParseException(e);
    }
  }

  private IsoDateFormat isoDateFormat() {
    return IsoDateFormat.inst.get();
  }

  // runtime validation enums

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface AllowNull {
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqIsoDate {
    String[] value() default {};
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptIsoDate {
    String[] value() default {};
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqLatLon {
    String[] value() default {};
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptLatLon {
    String[] value() default {};
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqLink {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptLink {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqStr {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptStr {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqInt {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptInt {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqDouble {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptDouble {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqLong {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptLong {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface ReqBool {
    String[] value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface OptBool {
    String[] value();
  }
}
