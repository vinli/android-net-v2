package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Message;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Messages {

  @GET("devices/{deviceId}/messages")
  Observable<Message.TimeSeries> messagesForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/messages")
  Observable<Message.TimeSeries> messagesForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("messages/{messageId}")
  Observable<Message.Wrapper> message( //
      @NonNull @Path("messageId") String messageId //
  );

  @GET
  Observable<Message.TimeSeries> messagesForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Message.Wrapper> messageForUrl( //
      @NonNull @Url String url //
  );
}
