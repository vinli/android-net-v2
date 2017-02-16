package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import li.vin.netv2.error.NoResourceExistsException;
import li.vin.netv2.model.BatteryStatus;
import li.vin.netv2.model.Collision;
import li.vin.netv2.model.Device;
import li.vin.netv2.model.Distance;
import li.vin.netv2.model.Distance.Unit;
import li.vin.netv2.model.Dtc;
import li.vin.netv2.model.Dtc.State;
import li.vin.netv2.model.DtcDiagnosis;
import li.vin.netv2.model.Dummy;
import li.vin.netv2.model.Event;
import li.vin.netv2.model.Link;
import li.vin.netv2.model.Location;
import li.vin.netv2.model.Message;
import li.vin.netv2.model.Notification;
import li.vin.netv2.model.Odometer;
import li.vin.netv2.model.OdometerSeed;
import li.vin.netv2.model.OdometerTrigger;
import li.vin.netv2.model.OdometerTriggerSeed;
import li.vin.netv2.model.OverallReportCard;
import li.vin.netv2.model.ReportCard;
import li.vin.netv2.model.Rule;
import li.vin.netv2.model.RuleSeed;
import li.vin.netv2.model.Snapshot;
import li.vin.netv2.model.Subscription;
import li.vin.netv2.model.SubscriptionSeed;
import li.vin.netv2.model.Trip;
import li.vin.netv2.model.User;
import li.vin.netv2.model.Vehicle;
import li.vin.netv2.model.misc.IsoDateFormat;
import li.vin.netv2.util.DeepCopyable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import rx.functions.Func1;

import static android.text.TextUtils.getTrimmedLength;
import static android.text.TextUtils.isEmpty;
import static java.util.concurrent.TimeUnit.SECONDS;
import static li.vin.netv2.internal.CachedHttpClients.ClientAndServices;
import static li.vin.netv2.internal.CachedHttpClients.ClientCacheKey;
import static li.vin.netv2.request.RequestPkgHooks.cachedHttpClients;
import static li.vin.netv2.request.RequestPkgHooks.isoDateFormat;

/**
 * The entry point for all interaction with the Vinli Net SDK - create a {@link Builder} with
 * {@link #builder()} to get started.
 *
 * @see #builder()
 */
public final class VinliRequest {

  private VinliRequest() {
  }

  /**
   * Begin building a request to the Vinli platform - the entry point for all interaction with the
   * Vinli Net SDK. Creates a {@link Builder}.
   */
  @NonNull
  public static Builder builder() {
    return new Builder();
  }

  // -----------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------
  // Phase 1 builder
  // -----------------------------------------------------------------------------------------------
  // -----------------------------------------------------------------------------------------------

  // TODO - volatile fields and yet not threadsafe; validate is called while fields are mutating???

  /**
   * The entry point for all interaction with the Vinli Net SDK. Note that this is {@link
   * DeepCopyable}, which is the only safe mechanism for reuse due to its internal mutable state.
   * Either create a fresh {@link Builder} for each request, or {@link #copy()} before each reuse.
   * Similarly, this is not thread safe - don't share an instance between threads, give each thread
   * its own copy instead.
   *
   * @see #builder()
   */
  public static final class Builder implements DeepCopyable<Builder> {

    private Builder() {
    }

    @NonNull Level logLevel = Level.NONE;
    @Nullable String accessToken;
    boolean missingResourcesAsNull;
    @Nullable RetryPolicy retryPolicy;
    long readTimeoutAmount = 60;
    @NonNull TimeUnit readTimeoutUnit = SECONDS;
    long writeTimeoutAmount = 60;
    @NonNull TimeUnit writeTimeoutUnit = SECONDS;
    long connectTimeoutAmount = 60;
    @NonNull TimeUnit connectTimeoutUnit = SECONDS;
    long overallTimeoutAmount = 120;
    @NonNull TimeUnit overallTimeoutUnit = SECONDS;
    @Nullable String env;
    @Nullable OkHttpClient.Builder clientBuilder;

    // getters

    @NonNull
    public Level getLogLevel() {
      return logLevel;
    }

    @Nullable
    public String getAccessToken() {
      return accessToken;
    }

    public boolean getMissingResourcesAsNull() {
      return missingResourcesAsNull;
    }

    public long getReadTimeoutAmount() {
      return readTimeoutAmount;
    }

    @NonNull
    public TimeUnit getReadTimeoutUnit() {
      return readTimeoutUnit;
    }

    public long getWriteTimeoutAmount() {
      return writeTimeoutAmount;
    }

    @NonNull
    public TimeUnit getWriteTimeoutUnit() {
      return writeTimeoutUnit;
    }

    public long getConnectTimeoutAmount() {
      return connectTimeoutAmount;
    }

    @NonNull
    public TimeUnit getConnectTimeoutUnit() {
      return connectTimeoutUnit;
    }

    public long getOverallTimeoutAmount() {
      return overallTimeoutAmount;
    }

    @NonNull
    public TimeUnit getOverallTimeoutUnit() {
      return overallTimeoutUnit;
    }

    @Nullable
    public RetryPolicy getRetryPolicy() {
      return retryPolicy;
    }

    /**
     * Generate a string that's safe to use as a part of a cache key for requests generated by this
     * builder. A valid access token must be set first, or this will throw an unchecked exception.
     */
    @NonNull
    public String getCacheKey() {
      try {
        //noinspection ConstantConditions
        byte[] hash = MessageDigest.getInstance("MD5").digest(accessToken.getBytes("UTF-8"));
        String md5 = new BigInteger(1, hash).toString(16);
        if (md5 == null || isEmpty(md5)) throw new NullPointerException();
        return md5;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @NonNull
    @Override
    public Builder copy() {
      Builder b = new Builder();
      b.logLevel = logLevel;
      b.accessToken = accessToken;
      b.missingResourcesAsNull = missingResourcesAsNull;
      b.retryPolicy = retryPolicy;
      b.readTimeoutAmount = readTimeoutAmount;
      b.readTimeoutUnit = readTimeoutUnit;
      b.writeTimeoutAmount = writeTimeoutAmount;
      b.writeTimeoutUnit = writeTimeoutUnit;
      b.connectTimeoutAmount = connectTimeoutAmount;
      b.connectTimeoutUnit = connectTimeoutUnit;
      b.overallTimeoutAmount = overallTimeoutAmount;
      b.overallTimeoutUnit = overallTimeoutUnit;
      b.env = env;
      b.clientBuilder = clientBuilder != null
          ? clientBuilder.build().newBuilder()
          : null;
      return b;
    }

    /**
     * Set the log level for all requests produced by this builder. {@link Level#NONE} by default.
     */
    @NonNull
    public Builder logLevel(@NonNull Level logLevel) {
      this.logLevel = logLevel;
      return this;
    }

    /**
     * Set the access token used to authenticate all requests produced by this builder. This is
     * required. If not provided, attempting to proceed past the phase one build step will throw an
     * unchecked exception.
     */
    @NonNull
    public Builder accessToken(@NonNull String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    /**
     * The default behavior for a missing resource is to throw a subclass of {@link
     * NoResourceExistsException} related to the model class found missing. If this is set to true,
     * instead, null values will be passed to observers in the case of a missing resource.
     *
     * @see BatteryStatus.NoBatteryStatusException
     * @see DtcDiagnosis.NoDtcDiagnosisException
     */
    @NonNull
    public Builder missingResourcesAsNull(boolean missingResourcesAsNull) {
      this.missingResourcesAsNull = missingResourcesAsNull;
      return this;
    }

    /**
     * Add a global {@link RetryPolicy} to all requests produced by this builder. This policy will
     * only apply HTTP 5XX errors.
     */
    @NonNull
    public Builder retryPolicy(@NonNull RetryPolicy retryPolicy) {
      this.retryPolicy = retryPolicy;
      return this;
    }

    /**
     * Set a read timeout at the HTTP client level. Defaults to 60 seconds.
     */
    @NonNull
    public Builder readTimeout(long amount, @NonNull TimeUnit unit) {
      this.readTimeoutAmount = amount;
      this.readTimeoutUnit = unit;
      return this;
    }

    /**
     * Set a write timeout at the HTTP client level. Defaults to 60 seconds.
     */
    @NonNull
    public Builder writeTimeout(long amount, @NonNull TimeUnit unit) {
      this.writeTimeoutAmount = amount;
      this.writeTimeoutUnit = unit;
      return this;
    }

    /**
     * Set a connect timeout at the HTTP client level. Defaults to 60 seconds.
     */
    @NonNull
    public Builder connectTimeout(long amount, @NonNull TimeUnit unit) {
      this.connectTimeoutAmount = amount;
      this.connectTimeoutUnit = unit;
      return this;
    }

    /**
     * Set an overall timeout per-request, propagating a {@link TimeoutException}. Defaults to 120
     * seconds.
     * <br/><br/>
     * Note that this supercedes any sort of {@link RetryPolicy}; the timeout will be generated
     * regardless.
     */
    @NonNull
    public Builder overallTimeout(long amount, @NonNull TimeUnit unit) {
      this.overallTimeoutAmount = amount;
      this.overallTimeoutUnit = unit;
      return this;
    }

    @NonNull
    public Func1<String, Builder> withAccessToken() {
      final Builder b = copy();
      return new Func1<String, Builder>() {
        @Override
        public Builder call(String s) {
          return b.accessToken(s);
        }
      };
    }

    @NonNull
    Builder env(@NonNull String env) {
      this.env = env;
      return this;
    }

    @NonNull
    Builder clientBuilder(@NonNull OkHttpClient.Builder clientBuilder) {
      this.clientBuilder = clientBuilder;
      return this;
    }

    @NonNull
    ClientAndServices validateAndGetClient() {
      if (accessToken == null || getTrimmedLength(accessToken) == 0) {
        throw new IllegalArgumentException("accessToken must be non-null, nonempty.");
      }

      //noinspection ConstantConditions
      ClientCacheKey key = cachedHttpClients.get().createKey( //
          logLevel, accessToken, env, //
          readTimeoutAmount, readTimeoutUnit, //
          writeTimeoutAmount, writeTimeoutUnit, //
          connectTimeoutAmount, connectTimeoutUnit //
      );

      return cachedHttpClients.get().clientForKey(key, clientBuilder);
    }

    /** Create a {@link Linker}, used to follow {@link Link} instances produced by models. */
    @NonNull
    public Linker followLink() {
      return new Linker(this);
    }

    /** Get {@link Device.Page} for the given {@link #accessToken(String)}. */
    @NonNull
    public PageBuilder<Device, Device.Page> getDevices() {
      return RequestFactories.inst.get() //
          .devicesPageBuilder(this, validateAndGetClient());
    }

    /** Get a single {@link Device} by id. */
    @NonNull
    public WrapperBuilder<Device, Device.Wrapper> getDevice(@NonNull String id) {
      return RequestFactories.inst.get().deviceWrapperBuilder(this, validateAndGetClient()).id(id);
    }

    /** Get {@link Vehicle.Page} for a device. Requires {@link ForId#DEVICE}. */
    @NonNull
    public ForIdBuilder< //
        PageBuilder<Vehicle, Vehicle.Page>> getVehicles() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .vehiclesPageBuilder(this, validateAndGetClient()));
    }

    /** Get the latest single {@link Vehicle} for a device. Requires {@link ForId#DEVICE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Vehicle, Vehicle.Wrapper>> getLatestVehicle() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .latestVehicleWrapperBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Vehicle} by id. */
    @NonNull
    public WrapperBuilder<Vehicle, Vehicle.Wrapper> getVehicle(@NonNull String id) {
      return RequestFactories.inst.get().vehicleWrapperBuilder(this, validateAndGetClient()).id(id);
    }

    /**
     * Get {@link Collision.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Collision, Collision.TimeSeries>> getCollisions() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .collisionsTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Collision} by id. */
    @NonNull
    public WrapperBuilder<Collision, Collision.Wrapper> getCollision(@NonNull String id) {
      return RequestFactories.inst.get()
          .collisionWrapperBuilder(this, validateAndGetClient())
          .id(id);
    }

    /**
     * Get current {@link BatteryStatus} for a vehicle. Requires {@link ForId#VEHICLE}.
     * <br/><br/>
     * Note a {@link BatteryStatus.NoBatteryStatusException} may be produced if not enough data is
     * available to determine battery status, unless opting for nulls through {@link
     * #missingResourcesAsNull(boolean)}.
     */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<BatteryStatus, BatteryStatus.Wrapper>> getCurrentBatteryStatus() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .batteryStatusWrapperBuilder(this, validateAndGetClient()));
    }

    /** Get the latest {@link Distance} for a vehicle. Requires {@link ForId#VEHICLE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Distance, Distance.Wrapper>> getLatestDistance(@NonNull Unit unit) {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().distanceWrapperBuilder(this, validateAndGetClient(), unit));
    }

    /**
     * Get {@link Odometer.TimeSeries} for a vehicle. Requires {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Odometer, Odometer.TimeSeries>> getOdometers() {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().odometersTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Odometer} by id. */
    @NonNull
    public WrapperBuilder<Odometer, Odometer.Wrapper> getOdometer(@NonNull String id) {
      return RequestFactories.inst.get().odometerWrapperBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /** Create a new {@link Odometer}. Require or {@link ForId#VEHICLE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Odometer, Odometer.Wrapper>> createOdometer(
        @NonNull OdometerSeed odometerSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .odometerCreateWrapperBuilder(this, validateAndGetClient(), odometerSeed));
    }

    /** Delete a {@link Odometer} by id. */
    @NonNull
    public ItemBuilder<Void> deleteOdometer(@NonNull String id) {
      return RequestFactories.inst.get().odometerDeleteItemBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /**
     * Get {@link OdometerTrigger.TimeSeries} for a vehicle. Requires {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<OdometerTrigger, OdometerTrigger.TimeSeries>> getOdometerTriggers() {
      return new ForIdBuilder<>(RequestFactories.inst.get()
          .odometerTriggersTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link OdometerTrigger} by id. */
    @NonNull
    public WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper> getOdometerTrigger(
        @NonNull String id) {
      return RequestFactories.inst.get()
          .odometerTriggerWrapperBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /** Create a new {@link OdometerTrigger}. Require or {@link ForId#VEHICLE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper>> createOdometerTrigger(
        @NonNull OdometerTriggerSeed odometerTriggerSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get().odometerTriggerCreateWrapperBuilder( //
          this, validateAndGetClient(), odometerTriggerSeed));
    }

    /** Delete a {@link Odometer} by id. */
    @NonNull
    public ItemBuilder<Void> deleteOdometerTrigger(@NonNull String id) {
      return RequestFactories.inst.get()
          .odometerTriggerDeleteItemBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /**
     * Get {@link Dtc.TimeSeries} for a vehicle. Requires {@link ForId#VEHICLE}. Same as {@link
     * #getDtcsWithState(State)} with a null param.
     *
     * @see #getDtcsWithState(State)
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Dtc, Dtc.TimeSeries>> getDtcs() {
      return getDtcsWithState(null);
    }

    /**
     * Get {@link Dtc.TimeSeries} for a vehicle. Requires {@link ForId#VEHICLE}. <i>state</i> param
     * allows filtering by {@link State#ACTIVE} or {@link State#INACTIVE}, or null for all.
     *
     * @see #getDtcs()
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Dtc, Dtc.TimeSeries>> getDtcsWithState(@Nullable State state) {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().dtcsTimeSeriesBuilder(this, validateAndGetClient(), state));
    }

    /** Get a single {@link DtcDiagnosis} by number (code). */
    @NonNull
    public WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> diagnoseDtc(@NonNull String number) {
      return RequestFactories.inst.get()
          .diagDtcByNumberBuilder(this, validateAndGetClient())
          .id(number);
    }

    /** Get a single {@link DtcDiagnosis} by id. */
    @NonNull
    public WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> getDtcDiagnosis(@NonNull String id) {
      return RequestFactories.inst.get() //
          .diagDtcByIdBuilder(this, validateAndGetClient()).id(id);
    }

    /**
     * Get {@link Event.TimeSeries} for a vehicle or device. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}. Same as {@link #getEventsOfType(String)} with a null param or {@link
     * #getEventsOfTypeForObjectId(String, String)} with all null params.
     *
     * @see #getEventsOfType(String)
     * @see #getEventsOfTypeForObjectId(String, String)
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Event, Event.TimeSeries>> getEvents() {
      return getEventsOfTypeForObjectId(null, null);
    }

    /**
     * Get {@link Event.TimeSeries} for a vehicle or device. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}. Same as {@link #getEventsOfTypeForObjectId(String, String)} with a
     * null <i>objectId</i> param. <i>type</i> param allows filtering by events of a certain type.
     * <br/><br/>
     * <a href="http://docs.vin.li/en/latest/web/event-services/overview.html#event-types">See the
     * online docs</a> for a complete list of supported event types.
     *
     * @see #getEvents()
     * @see #getEventsOfTypeForObjectId(String, String)
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Event, Event.TimeSeries>> getEventsOfType(@Nullable String type) {
      return getEventsOfTypeForObjectId(type, null);
    }

    /**
     * Get {@link Event.TimeSeries} for a vehicle or device. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}. <i>type</i> param allows filtering by events of a certain type.
     * <i>objectId</i> param allows getting only events associated with the given object (depends
     * on
     * the event - but typically a vehicle, trip, or rule).
     * <br/><br/>
     * <a href="http://docs.vin.li/en/latest/web/event-services/overview.html#event-types">See the
     * online docs</a> for a complete list of supported event types.
     *
     * @see #getEvents()
     * @see #getEventsOfType(String)
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Event, Event.TimeSeries>> getEventsOfTypeForObjectId( //
        @Nullable String type, @Nullable String objectId) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .eventsTimeSeriesBuilder(this, validateAndGetClient(), type, objectId));
    }

    /** Get a single {@link Event} by id. */
    @NonNull
    public WrapperBuilder<Event, Event.Wrapper> getEvent(@NonNull String id) {
      return RequestFactories.inst.get().eventWrapperBuilder(this, validateAndGetClient()).id(id);
    }

    /**
     * Get {@link Location.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Location, Location.TimeSeries>> getLocations() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .locationsTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Location} by id (result is a {@link Message}). */
    @NonNull
    public WrapperBuilder<Message, Message.Wrapper> getLocation(@NonNull String id) {
      return getMessage(id);
    }

    /**
     * Get {@link Message.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Message, Message.TimeSeries>> getMessages() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .messagesTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Message} by id. */
    @NonNull
    public WrapperBuilder<Message, Message.Wrapper> getMessage(@NonNull String id) {
      return RequestFactories.inst.get().messageWrapperBuilder(this, validateAndGetClient()).id(id);
    }

    /**
     * Get {@link Snapshot.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Snapshot, Snapshot.TimeSeries>> getSnapshots(@NonNull String fields) {
      return new ForIdBuilder<>(RequestFactories.inst.get()
          .snapshotsTimeSeriesBuilder(this, validateAndGetClient(), fields)); //
    }

    /**
     * Get {@link Notification.TimeSeries} for an event or subscription. Requires {@link
     * ForId#EVENT} or {@link ForId#SUBSCRIPTION}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Notification, Notification.TimeSeries>> getNotifications() {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().notificationsTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Notification} by id. */
    @NonNull
    public WrapperBuilder<Notification, Notification.Wrapper> getNotification(@NonNull String id) {
      return RequestFactories.inst.get()
          .notificationWrapperBuilder(this, validateAndGetClient())
          .id(id);
    }

    /**
     * Get {@link Subscription.Page} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        PageBuilder<Subscription, Subscription.Page>> getSubscriptions() {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().subscriptionPageBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Subscription} by id. */
    @NonNull
    public ForIdBuilder<WrapperBuilder<Subscription, Subscription.Wrapper>> getSubscription() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .subscriptionWrapperBuilder(this, validateAndGetClient()));
    }

    /** Create a new {@link Subscription}. Requires {@link ForId#DEVICE} or {@link ForId#VEHICLE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Subscription, Subscription.Wrapper>> createSubscription(
        @NonNull SubscriptionSeed subscriptionSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .subcriptionCreateWrapperBuilder(this, validateAndGetClient(), subscriptionSeed));
    }

    /** Delete a {@link Subscription} by id. */
    @NonNull
    public ItemBuilder<Void> deleteSubscription(@NonNull String id) {
      return RequestFactories.inst.get() //
          .subscriptionDeleteItemBuilder(this, validateAndGetClient()).id(id);
    }

    /**
     * Edit an exisiting {@link Rule}. Requires {@link ForId#DEVICE} and {@link
     * ForId#SUBSCRIPTION}.
     */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Subscription, Subscription.Wrapper>> createRule(@NonNull String id,
        @NonNull SubscriptionSeed subscriptionSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .subcriptionEditWrapperBuilder(this, validateAndGetClient(), subscriptionSeed));
    }

    /**
     * Get {@link Trip.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<Trip, Trip.TimeSeries>> getTrips() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .tripsTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Trip} by id. */
    @NonNull
    public WrapperBuilder<Trip, Trip.Wrapper> getTrip(@NonNull String id) {
      return RequestFactories.inst.get() //
          .tripWrapperBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /** Get the current {@link User} for the given {@link #accessToken(String)}. */
    @NonNull
    public WrapperBuilder<User, User.Wrapper> getCurrentUser() {
      return RequestFactories.inst.get()
          .userWrapperBuilder(this, validateAndGetClient())
          .id("NONE");
    }

    /**
     * Get {@link ReportCard.TimeSeries} for a device or vehicle. Requires {@link ForId#DEVICE} or
     * {@link ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        TimeSeriesBuilder<ReportCard, ReportCard.TimeSeries>> getReportCards() {
      return new ForIdBuilder<>(
          RequestFactories.inst.get().reportCardsTimeSeriesBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link ReportCard} for a trip. Requires {@link ForId#TRIP}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<ReportCard, ReportCard.Wrapper>> getReportCard() {
      return new ForIdBuilder<>(RequestFactories.inst.get()
          .reportCardForTripWrapperBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link ReportCard} by id. */
    @NonNull
    public WrapperBuilder<ReportCard, ReportCard.Wrapper> getReportCard(@NonNull String id) {
      return RequestFactories.inst.get()
          .reportCardWrapperBuilder(this, validateAndGetClient())
          .id(id);
    }

    /**
     * Get {@link OverallReportCard} for a device. Requires {@link ForId#DEVICE}. Same as {@link
     * #getOverallReportCardSince(Long)} with a null param or {@link
     * #getOverallReportCardSinceUntil(Long, Long)} with all null params.
     *
     * @see #getOverallReportCardSince(Long)
     * @see #getOverallReportCardSinceUntil(Long, Long)
     */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OverallReportCard, OverallReportCard>> getOverallReportCard() {
      return getOverallReportCardSinceUntil((Long) null, null);
    }

    /** @see #getOverallReportCardSince(Long) */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OverallReportCard, OverallReportCard>> getOverallReportCardSince(
        @Nullable String since) {
      return getOverallReportCardSinceUntil(isoDateFormat.get().time(since), null);
    }

    /**
     * Get {@link OverallReportCard} for a device. Requires {@link ForId#DEVICE}. Same as {@link
     * #getOverallReportCardSinceUntil(Long, Long)} with a null <i>until</i> param. <i>since</i>
     * param sets beginning timestamp of overall report card calculation.
     *
     * @see #getOverallReportCard()
     * @see #getOverallReportCardSinceUntil(Long, Long)
     */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OverallReportCard, OverallReportCard>> getOverallReportCardSince(
        @Nullable Long since) {
      return getOverallReportCardSinceUntil(since, null);
    }

    /** @see #getOverallReportCardSinceUntil(Long, Long) */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OverallReportCard, OverallReportCard>> getOverallReportCardSinceUntil(
        @Nullable String since, @Nullable String until) {
      IsoDateFormat idf = isoDateFormat.get();
      return getOverallReportCardSinceUntil(idf.time(since), idf.time(until));
    }

    /**
     * Get {@link OverallReportCard} for a device. Requires {@link ForId#DEVICE}. <i>since</i>
     * param sets beginning timestamp of overall report card calculation. <i>until</i> param sets
     * ending timestamp of overall report card calculation.
     *
     * @see #getOverallReportCard()
     * @see #getOverallReportCardSince(Long)
     */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<OverallReportCard, OverallReportCard>> getOverallReportCardSinceUntil(
        @Nullable Long since, @Nullable Long until) {
      return new ForIdBuilder<>(RequestFactories.inst.get()
          .overallReportCardWrapperBuilder(this, validateAndGetClient(), since, until));
    }

    /**
     * Get {@link Rule.Page} for a device or vehicle. Requires {@link ForId#DEVICE} or {@link
     * ForId#VEHICLE}.
     */
    @NonNull
    public ForIdBuilder< //
        PageBuilder<Rule, Rule.Page>> getRules() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .rulesPageBuilder(this, validateAndGetClient()));
    }

    /** Get a single {@link Rule} by id. */
    @NonNull
    public WrapperBuilder<Rule, Rule.Wrapper> getRule(@NonNull String id) {
      return RequestFactories.inst.get() //
          .ruleWrapperBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /** Create a new {@link Rule}. Requires {@link ForId#DEVICE} or {@link ForId#VEHICLE}. */
    @NonNull
    public ForIdBuilder< //
        WrapperBuilder<Rule, Rule.Wrapper>> createRule(@NonNull RuleSeed ruleSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .ruleCreateWrapperBuilder(this, validateAndGetClient(), ruleSeed));
    }

    /** Delete a {@link Rule} by id. */
    @NonNull
    public ItemBuilder<Void> deleteRule(@NonNull String id) {
      return RequestFactories.inst.get() //
          .ruleDeleteItemBuilder(this, validateAndGetClient()) //
          .id(id);
    }

    /** Get {@link Dummy.Page} for the given {@link #accessToken(String)}. */
    @NonNull
    public PageBuilder<Dummy, Dummy.Page> getDummies() {
      return RequestFactories.inst.get() //
          .dummiesPageBuilder(this, validateAndGetClient());
    }

    /** Get a single {@link Dummy.Run}. Requires {@link ForId#DUMMY}. */
    @NonNull
    public ForIdBuilder<WrapperBuilder<Dummy.Run, Dummy.Run.Wrapper>> getCurrentRun() {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .runWrapperBuilder(this, validateAndGetClient()));
    }

    /** Create a new {@link Dummy.Run}. Requires {@link ForId#DUMMY}. */
    @NonNull
    public  //
    ForIdBuilder<WrapperBuilder<Dummy.Run, Dummy.Run.Wrapper>> createRun(
        @NonNull Dummy.RunSeed runSeed) {
      return new ForIdBuilder<>(RequestFactories.inst.get() //
          .runCreateWrapperBuilder(this, validateAndGetClient(), runSeed));
    }

    /** Delete a {@link Dummy.Run}. Requires {@link ForId#DUMMY}. */
    @NonNull
    public ItemBuilder<Void> deleteRun(@NonNull String id) {
      return RequestFactories.inst.get() //
          .runDeleteItemBuilder(this, validateAndGetClient()).id(id);
    }
  }
}
