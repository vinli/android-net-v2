package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.OverallReportCard;
import li.vin.netv2.model.ReportCard;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface ReportCards {

  @GET("devices/{deviceId}/report_cards")
  Observable<ReportCard.TimeSeries> reportCardsForDevice(@NonNull @Path("deviceId") String deviceId,
      //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until,  //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("vehicles/{vehicleId}/report_cards")
  Observable<ReportCard.TimeSeries> reportCardsForVehicle( //
      @NonNull @Path("vehicleId") String vehicleId,  //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until,  //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("sortDir") String sortDir //
  );

  @GET("devices/{deviceId}/report_cards/overall")
  Observable<OverallReportCard> overallReportCardForDevice( //
      @NonNull @Path("deviceId") String deviceId,  //
      @Nullable @Query("since") Long since, //
      @Nullable @Query("until") Long until  //
  );

  @GET("report_cards/{reportCardId}")
  Observable<ReportCard.Wrapper> reportCard( //
      @NonNull @Path("reportCardId") String reportCardId //
  );

  @GET("trips/{tripId}/report_cards/_current")
  Observable<ReportCard.Wrapper> reportCardForTrip( //
      @NonNull @Path("tripId") String tripId //
  );

  @GET
  Observable<ReportCard.TimeSeries> reportCardsForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<ReportCard.Wrapper> reportCardForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<OverallReportCard> overallReportCardForUrl( //
      @NonNull @Url String url //
  );
}
