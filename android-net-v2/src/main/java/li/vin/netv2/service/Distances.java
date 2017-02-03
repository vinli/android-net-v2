package li.vin.netv2.service;

import android.support.annotation.NonNull;
import li.vin.netv2.model.Distance;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

public interface Distances {

  @GET("vehicles/{vehicleId}/distances/_latest")
  Observable<Distance.Wrapper> latestDistance( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @NonNull @Header("x-vinli-unit") String unit //
  );

  @GET
  Observable<Distance.Wrapper> distanceForUrl( //
      @NonNull @Url String url, //
      @NonNull @Header("x-vinli-unit") String unit //
  );

  //@POST("vehicles/{vehicleId}/odometers")
  //Observable<Wrapped<Odometer>> createOdometerReport(
  //    @Path("vehicleId") String vehicleId,
  //    @Body Odometer.ModelSeed odometerSeed);
  //
  //@GET("vehicles/{vehicleId}/odometers")
  //Observable<TimeSeries<Odometer>> odometerReports(
  //    @Path("vehicleId") String vehicleId,
  //    @Query("since") Long since,
  //    @Query("until") Long until,
  //    @Query("limit") Integer limit,
  //    @Query("sortDir") String sortDir);
  //
  //@GET("odometers/{odometerId}")
  //Observable<Wrapped<Odometer>> odometerReport(
  //    @Path("odometerId") String odometerId);
  //
  //@DELETE("odometers/{odometerId}")
  //Observable<Void> deleteOdometerReport(
  //    @Path("odometerId") String odometerId);
  //
  //@GET Observable<TimeSeries<Odometer>> odometerReportsForUrl(@NonNull @Url String url);
  //
  //@POST("vehicles/{vehicleId}/odometer_triggers")
  //Observable<Wrapped<OdometerTrigger>> createOdometerTrigger(
  //    @Path("vehicleId") String vehicleId,
  //    @Body OdometerTrigger.ModelSeed odometerTrigger);
  //
  //@GET("odometer_triggers/{odometerTriggerId}")
  //Observable<Wrapped<OdometerTrigger>> odometerTrigger(
  //    @Path("odometerTriggerId") String odometerTriggerId);
  //
  //@DELETE("odometer_triggers/{odometerTriggerId}")
  //Observable<Void> deleteOdometerTrigger(
  //    @Path("odometerTriggerId") String odometerTriggerId);
  //
  //@GET("vehicles/{vehicleId}/odometer_triggers")
  //Observable<TimeSeries<OdometerTrigger>> odometerTriggers(
  //    @Path("vehicleId") String vehicleId,
  //    @Query("since") Long since,
  //    @Query("until") Long until,
  //    @Query("limit") Integer limit,
  //    @Query("sortDir") String sortDir);
  //
  //@GET Observable<TimeSeries<OdometerTrigger>> odometerTriggersForUrl(@NonNull @Url String url);
}
