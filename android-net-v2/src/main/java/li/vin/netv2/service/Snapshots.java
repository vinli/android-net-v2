package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Snapshot;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Snapshots {
  @GET("devices/{deviceId}/snapshots")
  Observable<Snapshot.TimeSeries> snapshots(@NonNull @Path("deviceId") String deviceId,
      @NonNull @Query("fields") String fields, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET("vehicles/{vehicleId}/snapshots")
  Observable<Snapshot.TimeSeries> vehicleSnapshots(@NonNull @Path("vehicleId") String vehicleId,
      @NonNull @Query("fields") String fields, @Nullable @Query("since") Long since,
      @Nullable @Query("until") Long until, @Nullable @Query("limit") Integer limit,
      @Nullable @Query("sortDir") String sortDir);

  @GET
  Observable<Snapshot.TimeSeries> snapshotsForUrl(@NonNull @Url String url);
}
