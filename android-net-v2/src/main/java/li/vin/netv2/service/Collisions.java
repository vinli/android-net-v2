package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Collision;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Collisions {

  @GET("devices/{deviceId}/collisions")
  Observable<Collision.TimeSeries> collisionsForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/collisions")
  Observable<Collision.TimeSeries> collisionsForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("collisions/{collisionId}")
  Observable<Collision.Wrapper> collision( //
      @NonNull @Path("collisionId") String collisionId //
  );

  @GET
  Observable<Collision.TimeSeries> collisionsForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Collision.Wrapper> collisionForUrl( //
      @NonNull @Url String url //
  );
}
