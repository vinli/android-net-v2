package li.vin.netv2.service;

import android.support.annotation.NonNull;
import li.vin.netv2.model.Distance;
import li.vin.netv2.model.Odometer;
import li.vin.netv2.model.OdometerSeed;
import li.vin.netv2.model.OdometerTrigger;
import li.vin.netv2.model.OdometerTriggerSeed;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
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

  @POST("vehicles/{vehicleId}/odometers")
  Observable<Odometer.Wrapper> createOdometerReport(
      @Path("vehicleId") String vehicleId,
      @Body OdometerSeed.Wrapper odometerSeed);

  @GET("vehicles/{vehicleId}/odometers")
  Observable<Odometer.TimeSeries> odometerReports(
      @Path("vehicleId") String vehicleId,
      @Query("since") Long since,
      @Query("until") Long until,
      @Query("limit") Integer limit,
      @Query("sortDir") String sortDir);

  @GET("odometers/{odometerId}")
  Observable<Odometer.Wrapper> odometerReport(
      @Path("odometerId") String odometerId);

  @DELETE("odometers/{odometerId}")
  Observable<Void> deleteOdometerReport(
      @Path("odometerId") String odometerId);

  @GET Observable<Odometer.Wrapper> odometerReportForUrl(@NonNull @Url String url);

  @GET Observable<Odometer.TimeSeries> odometerReportsForUrl(@NonNull @Url String url);

  @POST("vehicles/{vehicleId}/odometer_triggers")
  Observable<OdometerTrigger.Wrapper> createOdometerTrigger(
      @Path("vehicleId") String vehicleId,
      @Body OdometerTriggerSeed.Wrapper odometerTriggerSeed);

  @GET("odometer_triggers/{odometerTriggerId}")
  Observable<OdometerTrigger.Wrapper> odometerTrigger(
      @Path("odometerTriggerId") String odometerTriggerId);

  @DELETE("odometer_triggers/{odometerTriggerId}")
  Observable<Void> deleteOdometerTrigger(
      @Path("odometerTriggerId") String odometerTriggerId);

  @GET("vehicles/{vehicleId}/odometer_triggers")
  Observable<OdometerTrigger.TimeSeries> odometerTriggers(
      @Path("vehicleId") String vehicleId,
      @Query("since") Long since,
      @Query("until") Long until,
      @Query("limit") Integer limit,
      @Query("sortDir") String sortDir);

  @GET Observable<OdometerTrigger.TimeSeries> odometerTriggersForUrl(@NonNull @Url String url);

  @GET Observable<OdometerTrigger.Wrapper> odometerTriggerForUrl(@NonNull @Url String url);
}
