package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Notification;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Notifications {

  @GET("subscriptions/{subscriptionId}/notifications")
  Observable<Notification.TimeSeries> notificationsForSubscription( //
      @NonNull @Path("subscriptionId") String subscriptionId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("events/{eventId}/notifications")
  Observable<Notification.TimeSeries> notificationsForEvent( //
      @NonNull @Path("eventId") String eventId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("notifications/{notificationId}")
  Observable<Notification.Wrapper> notification( //
      @NonNull @Path("notificationId") String notificationId //
  );

  @GET
  Observable<Notification.TimeSeries> notificationsForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Notification.Wrapper> notificationForUrl( //
      @NonNull @Url String url //
  );
}
