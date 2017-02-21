package li.vin.netv2.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import li.vin.netv2.request.RequestPkgHooks;
import li.vin.netv2.service.Collisions;
import li.vin.netv2.service.Devices;
import li.vin.netv2.service.Diagnostics;
import li.vin.netv2.service.Distances;
import li.vin.netv2.service.Dummies;
import li.vin.netv2.service.Events;
import li.vin.netv2.service.Generic;
import li.vin.netv2.service.Locations;
import li.vin.netv2.service.Messages;
import li.vin.netv2.service.Notifications;
import li.vin.netv2.service.ReportCards;
import li.vin.netv2.service.Rules;
import li.vin.netv2.service.Snapshots;
import li.vin.netv2.service.Subscriptions;
import li.vin.netv2.service.Trips;
import li.vin.netv2.service.Users;
import li.vin.netv2.service.Vehicles;
import li.vin.netv2.util.Lazy;
import li.vin.netv2.util.LazyOrSet;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static java.lang.String.format;
import static li.vin.netv2.internal.InternalPkgHooks.strictGson;
import static retrofit2.adapter.rxjava.RxJavaCallAdapterFactory.createWithScheduler;

public class CachedHttpClients {

  CachedHttpClients() {
  }

  // lazy init singleton inst

  static final LazyOrSet<CachedHttpClients> inst = //
      LazyOrSet.create(new Func0<CachedHttpClients>() {
        @Override
        public CachedHttpClients call() {
          return new CachedHttpClients();
        }
      });

  // inst providers

  public static void provideInst(RequestPkgHooks hooks) {
    hooks.cachedHttpClientsHook = inst.get();
  }

  // default impl

  public ClientCacheKey createKey( //
      @NonNull Level logLevel, @NonNull String accessToken, @Nullable String env, //
      long readTimeoutAmount, @NonNull TimeUnit readTimeoutUnit, //
      long writeTimeoutAmount, @NonNull TimeUnit writeTimeoutUnit, //
      long connectTimeoutAmount, @NonNull TimeUnit connectTimeoutUnit //
  ) {
    return new ClientCacheKey( //
        logLevel, accessToken, env, //
        readTimeoutAmount, readTimeoutUnit, //
        writeTimeoutAmount, writeTimeoutUnit, //
        connectTimeoutAmount, connectTimeoutUnit //
    );
  }

  @NonNull
  public ClientAndServices clientForKey( //
      @NonNull final ClientCacheKey key, @Nullable OkHttpClient.Builder clientBuilder) {

    synchronized (clients) {

      ClientAndServices client = clients.get(key);
      if (client != null) return client;

      if (clientBuilder == null) {
        clientBuilder = new OkHttpClient.Builder();
      } else {
        // No mutating a param's internal state
        clientBuilder = clientBuilder.build().newBuilder();
      }

      Interceptor authInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
          if ("NONE".equals(key.accessToken)) return chain.proceed(chain.request());
          return chain.proceed( //
              chain.request() //
                  .newBuilder() //
                  .header("Authorization", format("Bearer %s", key.accessToken)) //
                  .build() //
          );
        }
      };

      Interceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
          Log.d("VinliNet2", message);
        }
      }).setLevel(key.logLevel);

      OkHttpClient okHttpClient = clientBuilder //
          .addInterceptor(authInterceptor)
          .addInterceptor(logInterceptor)
          .readTimeout(key.readTimeoutAmount, key.readTimeoutUnit)
          .writeTimeout(key.writeTimeoutAmount, key.writeTimeoutUnit)
          .connectTimeout(key.connectTimeoutAmount, key.connectTimeoutUnit)
          .build();

      clients.put(key, client = new ClientAndServices(okHttpClient, key.env));

      return client;
    }
  }

  private final HashMap<ClientCacheKey, ClientAndServices> clients = new HashMap<>();

  // HTTP client + all services

  public static class ClientAndServices {

    @NonNull final OkHttpClient client;
    @Nullable final String env;

    final Lazy<Retrofit> platformAdapter;
    final Lazy<Retrofit> safetyAdapter;
    final Lazy<Retrofit> eventsAdapter;
    final Lazy<Retrofit> telemAdapter;
    final Lazy<Retrofit> tripsAdapter;
    final Lazy<Retrofit> authAdapter;
    final Lazy<Retrofit> behavioralAdapter;
    final Lazy<Retrofit> rulesAdapter;
    final Lazy<Retrofit> dummiesAdapter;

    public final Lazy<Devices> devices;
    public final Lazy<Vehicles> vehicles;
    public final Lazy<Collisions> collisions;
    public final Lazy<Diagnostics> diagnostics;
    public final Lazy<Distances> distances;
    public final Lazy<Events> events;
    public final Lazy<Locations> locations;
    public final Lazy<Messages> messages;
    public final Lazy<Snapshots> snapshots;
    public final Lazy<Notifications> notifications;
    public final Lazy<Subscriptions> subscriptions;
    public final Lazy<Trips> trips;
    public final Lazy<Users> users;
    public final Lazy<ReportCards> reportCards;
    public final Lazy<Rules> rules;
    public final Lazy<Dummies> dummies;

    public final Lazy<Generic> genericAuth;
    public final Lazy<Generic> genericTos;
    public final Lazy<Generic> genericMyVinli;

    ClientAndServices(@NonNull OkHttpClient client, @Nullable String env) {
      this.client = client;
      this.env = env;

      this.platformAdapter = lazyAdapter(Endpoint.PLATFORM);
      this.safetyAdapter = lazyAdapter(Endpoint.SAFETY);
      this.eventsAdapter = lazyAdapter(Endpoint.EVENTS);
      this.telemAdapter = lazyAdapter(Endpoint.TELEMETRY);
      this.tripsAdapter = lazyAdapter(Endpoint.TRIPS);
      this.authAdapter = lazyAdapter(Endpoint.AUTH);
      this.behavioralAdapter = lazyAdapter(Endpoint.BEHAVIORAL);
      this.rulesAdapter = lazyAdapter(Endpoint.RULES);
      this.dummiesAdapter = lazyAdapter(Endpoint.DUMMY);

      this.devices = lazyService(this.platformAdapter, Devices.class);
      this.vehicles = lazyService(this.platformAdapter, Vehicles.class);
      this.collisions = lazyService(this.safetyAdapter, Collisions.class);
      this.diagnostics = lazyService(Endpoint.DIAGNOSTICS, Diagnostics.class);
      this.distances = lazyService(Endpoint.DISTANCE, Distances.class);
      this.events = lazyService(this.eventsAdapter, Events.class);
      this.locations = lazyService(this.telemAdapter, Locations.class);
      this.messages = lazyService(this.telemAdapter, Messages.class);
      this.snapshots = lazyService(this.telemAdapter, Snapshots.class);
      this.notifications = lazyService(this.eventsAdapter, Notifications.class);
      this.subscriptions = lazyService(this.eventsAdapter, Subscriptions.class);
      this.trips = lazyService(this.tripsAdapter, Trips.class);
      this.users = lazyService(Endpoint.AUTH, Users.class);
      this.reportCards = lazyService(this.behavioralAdapter, ReportCards.class);
      this.rules = lazyService(this.rulesAdapter, Rules.class);
      this.dummies = lazyService(this.dummiesAdapter, Dummies.class);

      this.genericAuth = lazyService(Endpoint.AUTH, Generic.class);
      this.genericTos = lazyService(Endpoint.TOS, Generic.class);
      this.genericMyVinli = lazyService(Endpoint.MY_VINLI, Generic.class);
    }

    Lazy<Retrofit> lazyAdapter(final Endpoint endpoint) {
      return Lazy.create(new Func0<Retrofit>() {
        @Override
        public Retrofit call() {
          return new Retrofit.Builder() //
              .baseUrl(endpoint.getUrl(env))
              .client(client)
              .addConverterFactory(GsonConverterFactory.create(strictGson.get().gson()))
              .addCallAdapterFactory(createWithScheduler(Schedulers.io()))
              .build();
        }
      });
    }

    <T> Lazy<T> lazyService(final Endpoint endpoint, final Class<T> serviceClass) {
      return Lazy.create(new Func0<T>() {
        @Override
        public T call() {
          return new Retrofit.Builder() //
              .baseUrl(endpoint.getUrl(env))
              .client(client)
              .addConverterFactory(GsonConverterFactory.create(strictGson.get().gson()))
              .addCallAdapterFactory(createWithScheduler(Schedulers.io()))
              .build()
              .create(serviceClass);
        }
      });
    }

    <T> Lazy<T> lazyService(final Lazy<Retrofit> adapter, final Class<T> serviceClass) {
      return Lazy.create(new Func0<T>() {
        @Override
        public T call() {
          return adapter.get().create(serviceClass);
        }
      });
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ClientAndServices)) return false;
      ClientAndServices that = (ClientAndServices) o;
      if (!client.equals(that.client)) return false;
      return env != null
          ? env.equals(that.env)
          : that.env == null;
    }

    @Override
    public int hashCode() {
      int result = client.hashCode();
      result = 31 * result + (env != null
          ? env.hashCode()
          : 0);
      return result;
    }
  }

  // cache key data class

  public static class ClientCacheKey {

    @NonNull final Level logLevel;
    @NonNull final String accessToken;
    @Nullable final String env;
    final long readTimeoutAmount;
    @NonNull final TimeUnit readTimeoutUnit;
    final long writeTimeoutAmount;
    @NonNull final TimeUnit writeTimeoutUnit;
    final long connectTimeoutAmount;
    @NonNull final TimeUnit connectTimeoutUnit;

    ClientCacheKey( //
        @NonNull Level logLevel, @NonNull String accessToken, @Nullable String env, //
        long readTimeoutAmount, @NonNull TimeUnit readTimeoutUnit, //
        long writeTimeoutAmount, @NonNull TimeUnit writeTimeoutUnit, //
        long connectTimeoutAmount, @NonNull TimeUnit connectTimeoutUnit //
    ) {
      this.logLevel = logLevel;
      this.accessToken = accessToken;
      this.env = env;
      this.readTimeoutAmount = readTimeoutAmount;
      this.readTimeoutUnit = readTimeoutUnit;
      this.writeTimeoutAmount = writeTimeoutAmount;
      this.writeTimeoutUnit = writeTimeoutUnit;
      this.connectTimeoutAmount = connectTimeoutAmount;
      this.connectTimeoutUnit = connectTimeoutUnit;
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ClientCacheKey)) return false;
      ClientCacheKey that = (ClientCacheKey) o;
      if (readTimeoutAmount != that.readTimeoutAmount) return false;
      if (writeTimeoutAmount != that.writeTimeoutAmount) return false;
      if (connectTimeoutAmount != that.connectTimeoutAmount) return false;
      if (logLevel != that.logLevel) return false;
      if (!accessToken.equals(that.accessToken)) return false;
      if (env != null
          ? !env.equals(that.env)
          : that.env != null) {
        return false;
      }
      if (readTimeoutUnit != that.readTimeoutUnit) return false;
      if (writeTimeoutUnit != that.writeTimeoutUnit) return false;
      return connectTimeoutUnit == that.connectTimeoutUnit;
    }

    @Override
    public int hashCode() {
      int result = logLevel.hashCode();
      result = 31 * result + accessToken.hashCode();
      result = 31 * result + (env != null
          ? env.hashCode()
          : 0);
      result = 31 * result + (int) (readTimeoutAmount ^ (readTimeoutAmount >>> 32));
      result = 31 * result + readTimeoutUnit.hashCode();
      result = 31 * result + (int) (writeTimeoutAmount ^ (writeTimeoutAmount >>> 32));
      result = 31 * result + writeTimeoutUnit.hashCode();
      result = 31 * result + (int) (connectTimeoutAmount ^ (connectTimeoutAmount >>> 32));
      result = 31 * result + connectTimeoutUnit.hashCode();
      return result;
    }
  }
}
