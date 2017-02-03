package li.vin.netv2.service;

import android.support.annotation.NonNull;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

public interface Generic {

  @POST
  Observable<ResponseBody> genericPost( //
      @NonNull @Url String url, //
      @NonNull @Body RequestBody body //
  );

  @GET
  Observable<ResponseBody> genericGet( //
      @NonNull @Url String url //
  );

  @GET
  @Headers({ "Accept: text/html" })
  Observable<ResponseBody> genericGetHtml( //
      @NonNull @Url String url //
  );
}
