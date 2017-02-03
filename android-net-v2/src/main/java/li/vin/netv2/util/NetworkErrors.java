package li.vin.netv2.util;

import android.support.annotation.NonNull;
import java.net.SocketTimeoutException;
import java.util.Locale;
import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Func1;

public final class NetworkErrors {

  private NetworkErrors() {
  }

  public static boolean isHttpException(Throwable t) {
    return t instanceof HttpException;
  }

  public static Func1<Throwable, Boolean> isHttpException() {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return isHttpException(t);
      }
    };
  }

  public static boolean isSocketTimeout(Throwable t) {
    return t instanceof SocketTimeoutException;
  }

  public static Func1<Throwable, Boolean> isSocketTimeout() {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return isSocketTimeout(t);
      }
    };
  }

  public static boolean isCode(Throwable t, int code) {
    return t instanceof HttpException && ((HttpException) t).code() == code;
  }

  public static Func1<Throwable, Boolean> isCode(final int code) {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return isCode(t, code);
      }
    };
  }

  public static boolean is4xx(Throwable t) {
    return t instanceof HttpException && ((HttpException) t).code() / 100 == 4;
  }

  public static Func1<Throwable, Boolean> is4xx() {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return is4xx(t);
      }
    };
  }

  public static boolean is5xx(Throwable t) {
    return t instanceof HttpException && ((HttpException) t).code() / 100 == 5;
  }

  public static Func1<Throwable, Boolean> is5xx() {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return is5xx(t);
      }
    };
  }

  public static boolean errorBodyContains(Throwable t, @NonNull String s) {
    return errorBodyContains(t, s, false);
  }

  public static Func1<Throwable, Boolean> errorBodyContains(@NonNull final String s) {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return errorBodyContains(t, s);
      }
    };
  }

  public static boolean errorBodyContains(Throwable t, @NonNull String s, //
      boolean caseInsensitive) {
    if (!(t instanceof HttpException)) return false;
    HttpException e = (HttpException) t;
    try {
      if (caseInsensitive) {
        return e.response()
            .errorBody()
            .string()
            .toLowerCase(Locale.US)
            .contains(s.toLowerCase(Locale.US));
      }
      return e.response() //
          .errorBody() //
          .string() //
          .contains(s);
    } catch (Exception any) {
      return false;
    }
  }

  public static Func1<Throwable, Boolean> errorBodyContains(@NonNull final String s, //
      final boolean caseInsensitive) {
    return new Func1<Throwable, Boolean>() {
      @Override
      public Boolean call(Throwable t) {
        return errorBodyContains(t, s, caseInsensitive);
      }
    };
  }
}
