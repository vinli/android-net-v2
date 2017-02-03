package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Location;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Locations {

  @GET("devices/{deviceId}/locations")
  Observable<Location.TimeSeries> locationsForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/locations")
  Observable<Location.TimeSeries> locationsForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET
  Observable<Location.TimeSeries> locationsForUrl( //
      @NonNull @Url String url //
  );
}
