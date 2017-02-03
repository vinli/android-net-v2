package li.vin.netv2.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import li.vin.netv2.auth.SignInView;
import li.vin.netv2.model.Collision;
import li.vin.netv2.model.Device;
import li.vin.netv2.request.VinliRequest;
import li.vin.netv2.util.VinliRx;
import li.vin.netv2.util.VinliRx.RxCache;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.observers.SafeSubscriber;
import rx.subjects.BehaviorSubject;

import static android.widget.Toast.LENGTH_LONG;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static li.vin.netv2.Vinli.strictGson;
import static li.vin.netv2.request.RetryPolicy.exponential;
import static li.vin.netv2.util.NetworkErrors.is4xx;
import static li.vin.netv2.util.NetworkErrors.is5xx;
import static li.vin.netv2.util.VinliRx.AgeSince.SINCE_ACCESSED;
import static li.vin.netv2.util.VinliRx.AgeSince.SINCE_CREATED;
import static li.vin.netv2.util.VinliRx.cachify;
import static li.vin.netv2.util.VinliRx.cachifyOnError;
import static li.vin.netv2.util.VinliRx.gsonDiskCacheReader;
import static li.vin.netv2.util.VinliRx.gsonDiskCacheWriter;
import static li.vin.netv2.util.VinliRx.or;
import static li.vin.netv2.util.VinliRx.pushInto;
import static li.vin.netv2.util.VinliRx.serializableDiskCacheReader;
import static li.vin.netv2.util.VinliRx.serializableDiskCacheWriter;
import static li.vin.netv2.util.VinliRx.shareify;
import static li.vin.netv2.util.VinliRx.simpleCacheFileNamer;
import static rx.android.schedulers.AndroidSchedulers.mainThread;

public class SampleActivity extends AppCompatActivity {

  RxCache signInCache;
  RxCache diskCache;
  RxCache memCache;

  private static final BehaviorSubject<VinliRequest.Builder> builderSubject = BehaviorSubject //
      .create((VinliRequest.Builder) null);

  public static final Observable<VinliRequest.Builder> builderObservable = builderSubject //
      .first(VinliRx.<VinliRequest.Builder>nonNull());

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    signInCache = VinliRx.diskCache( //
        Integer.MAX_VALUE, new File(getFilesDir(), "SIGN_IN"), //
        serializableDiskCacheWriter(), //
        serializableDiskCacheReader(), //
        simpleCacheFileNamer() //
    );

    diskCache = VinliRx.diskCache( //
        128, new File(getCacheDir(), "DISK_CACHE"), //
        gsonDiskCacheWriter(strictGson()), //
        gsonDiskCacheReader(strictGson()), //
        simpleCacheFileNamer() //
    );

    memCache = VinliRx.memCache(32, null);

    final VinliRequest.Builder requestBuilder = VinliRequest.builder()
        .logLevel(Level.BODY)
        .readTimeout(30, SECONDS)
        .writeTimeout(30, SECONDS)
        .connectTimeout(30, SECONDS)
        .overallTimeout(60, SECONDS)
        .retryPolicy(exponential(2, SECONDS));

    Observable.just(0)
        .observeOn(mainThread())
        .flatMap(new Func1<Integer, Observable<String>>() {
          @Override
          public Observable<String> call(Integer i) {
            return new SignInView(SampleActivity.this) //
                .clientId("BLAH")
                .redirectUri("BLAH")
                .setAsContentView(SampleActivity.this)
                .load()
                .observe();
          }
        })
        .compose(cachifySignIn())
        .map(requestBuilder.withAccessToken())
        .doOnNext(pushInto(builderSubject))
        .flatMap(collisionsToday())
        .repeat(128)
        .skip(127)
        .first()
        .delay(1500, MILLISECONDS)
        .observeOn(mainThread())
        .subscribe(new SafeSubscriber<>(new Subscriber<Collision>() {

          @Override
          public void onCompleted() {
          }

          @Override
          public void onError(Throwable e) {

            if (e instanceof NoSuchElementException) {
              finishWithToast("No collisions today.");
            } else {
              finishWithToast("Error: " + e.getMessage());
            }
          }

          @Override
          public void onNext(Collision collision) {

            finishWithToast("There was a collision today at " + collision.timestamp());
          }
        }));
  }

  @Override
  protected void onPause() {
    if (isFinishing()) builderSubject.onNext(null);
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    builderSubject.onNext(null);
    super.onDestroy();
  }

  Func1<VinliRequest.Builder, Observable<Collision>> collisionsToday() {

    return new Func1<VinliRequest.Builder, Observable<Collision>>() {
      @Override
      public Observable<Collision> call(final VinliRequest.Builder b) {

        final String cacheKey = b.getCacheKey();

        List<Observable<Collision>> all = new ArrayList<>();
        for (int i = 0; i < 100; i++) {

          all.add(b.copy()
              .getDevices()
              .build()
              .observeAllFlattened()
              .flatMap(new Func1<Device, Observable<Collision>>() {
                @Override
                public Observable<Collision> call(Device device) {

                  return b.copy()
                      .getCollisions()
                      .forDevice(device)
                      .since(currentTimeMillis() - DAYS.toMillis(1))
                      .build()
                      .observeAllFlattened();
                }
              })
              .doOnSubscribe(new Action0() {
                @Override
                public void call() {
                  Log.e("TESTEE", "source resubscribed....");
                }
              })
              .compose(cachifyCollisionsOnError(cacheKey)) //
              .compose(shareifyCollisions(cacheKey)) //
          );
        }
        return Observable.merge(all);
      }
    };
  }

  private Observable.Transformer<String, String> cachifySignIn() {
    return cachify("cacheSignInAccesssToken", new TypeToken<List<String>>() {
        }.getType(), //
        7, DAYS, SINCE_ACCESSED, memCache, signInCache);
  }

  //private Observable.Transformer<Device, Device> cachifyDevices(String id) {
  //  return cachify("getDevicesFlattened:" + id, Device.listType(), //
  //      60, SECONDS, SINCE_CREATED, memCache, diskCache);
  //}
  //
  //private Observable.Transformer<User, User> cachifyUser(String accessToken) {
  //  return cachify("getUserExtracted:" + accessToken, User.listType(), //
  //      60, SECONDS, SINCE_CREATED, memCache, diskCache);
  //}
  //
  //private Observable.Transformer<Device.Page, Device.Page> cachifyDevicePages(String id) {
  //  return cachify("getDevicePagesFlattened:" + id, Device.Page.listType(), //
  //      60, SECONDS, SINCE_CREATED, memCache, diskCache);
  //}
  //
  //private Observable.Transformer< //
  //    Collision.TimeSeries, Collision.TimeSeries> cachifyCollisionTimeSeries(String id) {
  //  return cachify("getCollisionTimeSeriesFlattened:" + id, Collision.TimeSeries.listType(), //
  //      60, SECONDS, SINCE_CREATED, memCache, diskCache);
  //}
  //
  //private Observable.Transformer<Collision, Collision> seedCollisionsCache(String id) {
  //  return seedCachify(id, Collision.listType());
  //}

  private Observable.Transformer<Collision, Collision> cachifyCollisionsOnError(String id) {
    return cachifyOnError("getCollisionsExtracted:" + id, Collision.listType(), //
        60, SECONDS, SINCE_CREATED, or(is4xx(), is5xx()), memCache, diskCache);
  }

  private Observable.Transformer<Collision, Collision> shareifyCollisions(String id) {
    return shareify("shareCollisions:" + id, 2, SECONDS);
  }

  void finishWithToast(String msg) {

    Toast t = Toast.makeText(this, msg, LENGTH_LONG);
    t.setGravity(Gravity.CENTER, 0, 0);
    t.show();

    finish();
  }
}
