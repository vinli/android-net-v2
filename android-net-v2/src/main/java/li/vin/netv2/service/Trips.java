package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Trip;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Trips {

  @GET("devices/{deviceId}/trips")
  Observable<Trip.TimeSeries> tripsForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/trips")
  Observable<Trip.TimeSeries> tripsForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("trips/{tripId}")
  Observable<Trip.Wrapper> trip( //
      @NonNull @Path("tripId") String tripId //
  );

  @GET
  Observable<Trip.TimeSeries> tripsForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Trip.Wrapper> tripForUrl( //
      @NonNull @Url String url //
  );
}
