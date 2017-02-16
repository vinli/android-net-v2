package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Dummy;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Dummies {
  @GET("dummies")
  Observable<Dummy.Page> dummies(@Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);

  @POST("dummies/{dummyId}/runs")
  Observable<Dummy.Run.Wrapper> create(@NonNull @Path("dummyId") String dummyId,
      @NonNull @Body Dummy.RunSeed.Wrapper runSeed);

  @GET("dummies/{dummyId}/runs/_current")
  Observable<Dummy.Run.Wrapper> currentRun(@NonNull @Path("dummyId") String dummyId);

  @GET("dummies/{dummyId}")
  Observable<Dummy.Wrapper> trip(@NonNull @Path("dummyId") String dummyId);

  @DELETE("dummies/{dummyId}/runs/_current")
  Observable<Void> deleteRun(@NonNull @Path("dummyId") String dummyId);

  @GET
  Observable<Dummy.Page> dummiesForUrl(@NonNull @Url String url);

  @GET
  Observable<Dummy.Run.Wrapper> runForUrl(@NonNull @Url String url);
}
