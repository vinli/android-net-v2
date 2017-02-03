package li.vin.netv2.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import li.vin.netv2.model.Device;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface Devices {

  @GET("devices")
  Observable<Device.Page> devices( //
      @Nullable @Query("limit") Integer limit, //
      @Nullable @Query("offset") Integer offset //
  );

  @GET("devices/{deviceId}")
  Observable<Device.Wrapper> device( //
      @NonNull @Path("deviceId") String deviceId //
  );

  @GET
  Observable<Device.Page> devicesForUrl( //
      @NonNull @Url String url //
  );

  @GET
  Observable<Device.Wrapper> deviceForUrl( //
      @NonNull @Url String url //
  );
}
