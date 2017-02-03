package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Event;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Events {

  @GET("devices/{deviceId}/events")
  Observable<Event.TimeSeries> eventsForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("type") String type, //
      @Nullable @Query("objectId") String objectId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/events")
  Observable<Event.TimeSeries> eventsForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("type") String type, //
      @Nullable @Query("objectId") String objectId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("events/{eventId}")
  Observable<Event.Wrapper> event( //
      @NonNull @Path("eventId") String eventId //
  );

  @GET
  Observable<Event.TimeSeries> eventsForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Event.Wrapper> eventForUrl( //
      @NonNull @Url String url //
  );
}
