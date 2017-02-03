package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Vehicle;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Vehicles {

  @GET("devices/{deviceId}/vehicles")
  Observable<Vehicle.Page> vehiclesForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("offset") Integer offset //
  );

  @GET("devices/{deviceId}/vehicles/_latest")
  Observable<Vehicle.Wrapper> latestVehicleForDevice( //
      @NonNull @Path("deviceId") String deviceId //
  );

  @GET("vehicles/{vehicleId}")
  Observable<Vehicle.Wrapper> vehicle( //
      @NonNull @Path("vehicleId") String vehicleId //
  );

  @GET
  Observable<Vehicle.Page> vehiclesForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Vehicle.Wrapper> vehicleForUrl( //
      @NonNull @Url String url //
  );
}
