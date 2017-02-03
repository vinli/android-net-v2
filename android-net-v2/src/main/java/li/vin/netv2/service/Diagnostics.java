package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.BatteryStatus;
import li.vin.netv2.model.Dtc;
import li.vin.netv2.model.DtcDiagnosis;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Diagnostics {

  @GET("vehicles/{vehicleId}/codes")
  Observable<Dtc.TimeSeries> dtcs( //
      @NonNull @Path("vehicleId") String vehicleId, //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until, //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir, //
      @Nullable @Query("state") String state //
  );

  @GET("codes")
  Observable<DtcDiagnosis.Page> diagnoseByNumber( //
      @NonNull @Query("number") String number //
  );

  @GET("codes/{id}")
  Observable<DtcDiagnosis.Wrapper> diagnoseById( //
      @NonNull @Path("id") String id //
  );

  @GET("vehicles/{vehicleId}/battery_statuses/_current")
  Observable<BatteryStatus.Wrapper> currentBatteryStatus( //
      @NonNull @Path("vehicleId") String vehicleId //
  );

  @GET
  Observable<Dtc.TimeSeries> dtcsForUrl( //
      @NonNull @Url String url //
  );

  // FIXME maybe? Uncomment if we need this. Don't think we ever will.
  //@GET
  //Observable<DtcDiagnosis.Page> diagnosisByNumberForUrl( //
  //    @NonNull @Url String url //
  //);

  @GET
  Observable<DtcDiagnosis.Wrapper> diagnosisByIdForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<BatteryStatus.Wrapper> batteryStatusForUrl( //
      @NonNull @Url String url //
  );
}
