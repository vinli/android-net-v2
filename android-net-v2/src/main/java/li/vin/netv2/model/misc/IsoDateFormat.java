package li.vin.netv2.model.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import li.vin.netv2.Vinli;
import li.vin.netv2.request.RequestPkgHooks;
import li.vin.netv2.util.LazyOrSet;
import rx.functions.Func0;

import static java.util.TimeZone.getTimeZone;

public class IsoDateFormat {

  IsoDateFormat() {
  }

  // lazy init singleton inst

  static final LazyOrSet<IsoDateFormat> inst = //
      LazyOrSet.create(new Func0<IsoDateFormat>() {
        @Override
        public IsoDateFormat call() {
          return new IsoDateFormat();
        }
      });

  // inst providers

  public static void provideInst(Vinli.MainPkgHooks hooks) {
    hooks.isoDateFormatHook = inst.get();
  }

  public static void provideInst(RequestPkgHooks hooks) {
    hooks.isoDateFormatHook = inst.get();
  }

  // default impl

  @NonNull
  public SimpleDateFormat get() {
    return dtFmtThrLocal.get();
  }

  @Nullable
  public Long time(@Nullable String s) {
    if (s == null) return null;
    try {
      return get().parse(s).getTime();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private final ThreadLocal<SimpleDateFormat> dtFmtThrLocal = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
      sdf.setTimeZone(getTimeZone("UTC"));
      return sdf;
    }
  };
}
