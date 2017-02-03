package li.vin.netv2.model.misc;

import android.support.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Locale;
import li.vin.netv2.Vinli;
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

  // default impl

  @NonNull
  public SimpleDateFormat get() {
    return dtFmtThrLocal.get();
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
