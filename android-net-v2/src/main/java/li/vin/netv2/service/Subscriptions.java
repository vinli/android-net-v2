package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Subscription;
import li.vin.netv2.model.SubscriptionSeed;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Subscriptions {
  @GET("devices/{deviceId}/subscriptions")
  Observable<Subscription.Page> subscriptions(
      @NonNull @Path("deviceId") String deviceId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);


  @GET("vehicles/{vehicleId}/subscriptions")
  Observable<Subscription.Page> vehicleSubscriptions(
      @NonNull @Path("vehicleId") String vehicleId,
      @Nullable @Query("limit") Integer limit,
      @Nullable @Query("offset") Integer offset);


  @POST("vehicles/{vehicleId}/subscriptions")
  Observable<Subscription.Wrapper> vehicleCreate(
      @NonNull @Path("vehicleId") String vehicleId,
      @NonNull @Body SubscriptionSeed.Wrapper subscriptionSeed);

  @GET("subscriptions/{subscriptionId}")
  Observable<Subscription.Wrapper> subscription(
      @NonNull @Path("subscriptionId") String subscriptionId);

  @POST("devices/{deviceId}/subscriptions")
  Observable<Subscription.Wrapper> create(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Body SubscriptionSeed.Wrapper subscriptionSeed);

  @PUT("devices/{deviceId}/subscriptions/{subscriptionId}")
  Observable<Subscription.Wrapper> edit(
      @NonNull @Path("deviceId") String deviceId,
      @NonNull @Path("subscriptionId") String subscriptionId,
      @NonNull @Body SubscriptionSeed.Wrapper subscriptionSeed);

  @DELETE("subscriptions/{subscriptionId}")
  Observable<Void> delete(@NonNull @Path("subscriptionId") String subscriptionId);

  @GET Observable<Subscription.Page> subscriptionsForUrl(@NonNull @Url String url);

  @GET Observable<Subscription.Wrapper> subscriptionForUrl(@NonNull @Url String url);

}
