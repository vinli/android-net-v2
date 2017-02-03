package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Rule;
import li.vin.netv2.model.RuleSeed;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Rules {

  @GET("devices/{deviceId}/rules")
  Observable<Rule.Page> rulesForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("offset") Integer offset //
  );

  @GET("vehicles/{vehicleId}/rules")
  Observable<Rule.Page> rulesForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("offset") Integer offset //
  );

  @GET("rules/{ruleId}")
  Observable<Rule.Wrapper> rule( //
      @NonNull @Path("ruleId") String ruleId //
  );

  @POST("devices/{deviceId}/rules")
  Observable<Rule.Wrapper> createRuleForDevice( //
      @NonNull @Path("deviceId") String deviceId, //
      @NonNull @Body RuleSeed.Wrapper ruleSeed //
  );

  @POST("vehicles/{vehicleId}/rules")
  Observable<Rule.Wrapper> createRuleForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @NonNull @Body RuleSeed.Wrapper ruleSeed //
  );

  @DELETE("rules/{ruleId}")
  Observable<Void> deleteRule( //
      @NonNull @Path("ruleId") String ruleId //
  );

  @GET
  Observable<Rule.Page> rulesForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Rule.Wrapper> ruleForUrl( //
      @NonNull @Url String url //
  );
}
