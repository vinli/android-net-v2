package li.vin.netv2.service;

import android.support.annotation.NonNull;
import li.vin.netv2.model.User;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface Users {

  @GET("users/_current")
  Observable<User.Wrapper> currentUser();

  @GET
  Observable<User.Wrapper> userForUrl( //
      @NonNull @Url String url //
  );
}
