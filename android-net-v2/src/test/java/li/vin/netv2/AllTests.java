package li.vin.netv2;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import li.vin.netv2.model.BatteryStatus;
import li.vin.netv2.model.Collision;
import li.vin.netv2.model.Device;
import li.vin.netv2.model.Distance;
import li.vin.netv2.model.Distance.Unit;
import li.vin.netv2.model.Dtc;
import li.vin.netv2.model.DtcDiagnosis;
import li.vin.netv2.model.Event;
import li.vin.netv2.model.Link;
import li.vin.netv2.model.Location;
import li.vin.netv2.model.Message;
import li.vin.netv2.model.Notification;
import li.vin.netv2.model.Odometer;
import li.vin.netv2.model.OdometerTrigger;
import li.vin.netv2.model.OverallReportCard;
import li.vin.netv2.model.ReportCard;
import li.vin.netv2.model.SortDir;
import li.vin.netv2.model.Trip;
import li.vin.netv2.model.User;
import li.vin.netv2.model.Vehicle;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.model.contract.StrictModelId;
import li.vin.netv2.model.misc.ModelMiscTestUtil;
import li.vin.netv2.request.VinliRequest.Builder;
import li.vin.netv2.util.NetworkErrors;
import li.vin.netv2.util.Pair;
import li.vin.netv2.util.VinliRx;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertTrue;
import static li.vin.netv2.model.Distance.Unit.KILOMETERS;
import static li.vin.netv2.model.Dtc.State.ACTIVE;
import static li.vin.netv2.model.Dtc.State.INACTIVE;
import static li.vin.netv2.model.SortDir.DESCENDING;
import static li.vin.netv2.request.ForId.DEVICE;
import static li.vin.netv2.request.ForId.EVENT;
import static li.vin.netv2.request.ForId.VEHICLE;
import static li.vin.netv2.request.RequestTestUtil.accessTokenFromBuilder;
import static li.vin.netv2.request.RequestTestUtil.baseBuilder;
import static li.vin.netv2.request.RequestTestUtil.clientFromBuilder;
import static li.vin.netv2.request.RetryPolicy.exponential;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class AllTests {

  static class BuilderPair<T> extends Pair<T, Builder> {

    public BuilderPair(T first, Builder second) {
      super(first, second);
    }
  }

  static class DeviceBuilderPair extends BuilderPair<Device> {

    static Func1<Device, DeviceBuilderPair> mapFunc(final Builder b) {
      return new Func1<Device, DeviceBuilderPair>() {
        @Override
        public DeviceBuilderPair call(Device device) {
          return new DeviceBuilderPair(device, b);
        }
      };
    }

    public DeviceBuilderPair(Device first, Builder second) {
      super(first, second);
    }
  }

  static class VehicleBuilderPair extends BuilderPair<Vehicle> {

    static Func1<Vehicle, VehicleBuilderPair> mapFunc(final Builder b) {
      return new Func1<Vehicle, VehicleBuilderPair>() {
        @Override
        public VehicleBuilderPair call(Vehicle vehicle) {
          return new VehicleBuilderPair(vehicle, b);
        }
      };
    }

    public VehicleBuilderPair(Vehicle first, Builder second) {
      super(first, second);
    }
  }

  static class CollisionBuilderPair extends BuilderPair<Collision> {

    static Func1<Collision, CollisionBuilderPair> mapFunc(final Builder b) {
      return new Func1<Collision, CollisionBuilderPair>() {
        @Override
        public CollisionBuilderPair call(Collision vehicle) {
          return new CollisionBuilderPair(vehicle, b);
        }
      };
    }

    public CollisionBuilderPair(Collision first, Builder second) {
      super(first, second);
    }
  }

  static class EventBuilderPair extends BuilderPair<Event> {

    static Func1<Event, EventBuilderPair> mapFunc(final Builder b) {
      return new Func1<Event, EventBuilderPair>() {
        @Override
        public EventBuilderPair call(Event vehicle) {
          return new EventBuilderPair(vehicle, b);
        }
      };
    }

    public EventBuilderPair(Event first, Builder second) {
      super(first, second);
    }
  }

  static class LocationBuilderPair extends BuilderPair<Location> {

    static Func1<Location, LocationBuilderPair> mapFunc(final Builder b) {
      return new Func1<Location, LocationBuilderPair>() {
        @Override
        public LocationBuilderPair call(Location vehicle) {
          return new LocationBuilderPair(vehicle, b);
        }
      };
    }

    public LocationBuilderPair(Location first, Builder second) {
      super(first, second);
    }
  }

  static class MessageBuilderPair extends BuilderPair<Message> {

    static Func1<Message, MessageBuilderPair> mapFunc(final Builder b) {
      return new Func1<Message, MessageBuilderPair>() {
        @Override
        public MessageBuilderPair call(Message vehicle) {
          return new MessageBuilderPair(vehicle, b);
        }
      };
    }

    public MessageBuilderPair(Message first, Builder second) {
      super(first, second);
    }
  }

  static class NotificationBuilderPair extends BuilderPair<Notification> {

    static Func1<Notification, NotificationBuilderPair> mapFunc(final Builder b) {
      return new Func1<Notification, NotificationBuilderPair>() {
        @Override
        public NotificationBuilderPair call(Notification vehicle) {
          return new NotificationBuilderPair(vehicle, b);
        }
      };
    }

    public NotificationBuilderPair(Notification first, Builder second) {
      super(first, second);
    }
  }

  static class TripBuilderPair extends BuilderPair<Trip> {

    static Func1<Trip, TripBuilderPair> mapFunc(final Builder b) {
      return new Func1<Trip, TripBuilderPair>() {
        @Override
        public TripBuilderPair call(Trip vehicle) {
          return new TripBuilderPair(vehicle, b);
        }
      };
    }

    public TripBuilderPair(Trip first, Builder second) {
      super(first, second);
    }
  }

  static class ReportCardBuilderPair extends BuilderPair<ReportCard> {

    static Func1<ReportCard, ReportCardBuilderPair> mapFunc(final Builder b) {
      return new Func1<ReportCard, ReportCardBuilderPair>() {
        @Override
        public ReportCardBuilderPair call(ReportCard vehicle) {
          return new ReportCardBuilderPair(vehicle, b);
        }
      };
    }

    public ReportCardBuilderPair(ReportCard first, Builder second) {
      super(first, second);
    }
  }

  static class OverallReportCardBuilderPair extends BuilderPair<OverallReportCard> {

    static Func1<OverallReportCard, OverallReportCardBuilderPair> mapFunc(final Builder b) {
      return new Func1<OverallReportCard, OverallReportCardBuilderPair>() {
        @Override
        public OverallReportCardBuilderPair call(OverallReportCard vehicle) {
          return new OverallReportCardBuilderPair(vehicle, b);
        }
      };
    }

    public OverallReportCardBuilderPair(OverallReportCard first, Builder second) {
      super(first, second);
    }
  }

  static Builder builder;
  static List<String> tokens;

  static ConnectableObservable<DeviceBuilderPair> sharedDeviceObs;
  static rx.Subscription sharedDeviceSub;

  static ConnectableObservable<VehicleBuilderPair> sharedVehicleObs;
  static rx.Subscription sharedVehicleSub;

  @BeforeClass
  public static void setup() {

    ShadowLog.stream = System.out;

    builder = baseBuilder("dev");



    // LET'S LOAD SOME DATA!

    sharedDeviceObs = Observable.from(tokens) //
        .flatMap(new Func1<String, Observable<DeviceBuilderPair>>() {
          @Override
          public Observable<DeviceBuilderPair> call(String token) {
            Builder b = builder.copy() //
                .accessToken(token) //
                .retryPolicy(exponential(2, SECONDS, true));
            return b.getDevices() //
                .build() //
                .observeAll() //
                .doOnNext(checkPageAction(20, 0)) //
                .flatMap(VinliRx.<Device>flattenPage()) //
                .map(DeviceBuilderPair.mapFunc(b));
          }
        }) //
        .distinct(new Func1<DeviceBuilderPair, Device>() {
          @Override
          public Device call(DeviceBuilderPair pair) {
            return pair.first;
          }
        }) //
        .replay();

    sharedVehicleObs = sharedDeviceObs //
        .flatMap(new Func1< //
            DeviceBuilderPair, Observable<VehicleBuilderPair>>() {
          @Override
          public Observable< //
              VehicleBuilderPair> call(DeviceBuilderPair pair) {
            return pair.second.getVehicles() //
                .forId(DEVICE, pair.first.id()) //
                .build() //
                .observeAll() //
                .doOnNext(checkPageAction(20, 0)) //
                .flatMap(VinliRx.<Vehicle>flattenPage()) //
                .map(VehicleBuilderPair.mapFunc(pair.second));
          }
        }) //
        .distinct(new Func1<VehicleBuilderPair, Vehicle>() {
          @Override
          public Vehicle call(VehicleBuilderPair pair) {
            return pair.first;
          }
        }) //
        .replay();
  }

  @AfterClass
  public static void teardown() {
    if (sharedDeviceSub != null) sharedDeviceSub.unsubscribe();
    if (sharedVehicleSub != null) sharedVehicleSub.unsubscribe();
  }

  @Before
  public void beforeEach() {
    if (sharedDeviceSub == null) sharedDeviceSub = sharedDeviceObs.connect();
    if (sharedVehicleSub == null) sharedVehicleSub = sharedVehicleObs.connect();
  }

  private static <T extends StrictModel> void sanityCheckModel(T t, Class<T> cls, Builder b,
      boolean followLinks) {
    sanityCheckModel(t, cls, b, followLinks, true);
  }

  private static <T extends StrictModel> void sanityCheckModel(T t, Class<T> cls, Builder b,
      boolean followLinks, boolean gson) {

    assertNotNull(t);

    Set<String> linksToFollow = new HashSet<>();

    try {
      // Strictly check all the model fields ...
      ModelMiscTestUtil.checkStrict(t);

      // Invoke every public no-arg method on the model (just make sure nothing throws) ...
      // ... also build up a list of every link on the model
      for (Method m : t.getClass().getMethods()) {
        if (m.getDeclaringClass() == t.getClass() && m.getParameterTypes().length == 0) {
          //System.out.println("invoking " + m.getName());
          if (m.getName().toLowerCase(Locale.US).endsWith("link") && //
              m.getReturnType() == Link.class) {
            linksToFollow.add(((Link) m.invoke(t)).raw());
          } else if (m.getName().toLowerCase(Locale.US).endsWith("link") && //
              m.getReturnType() == String.class) {
            linksToFollow.add((String) m.invoke(t));
          } else {
            m.invoke(t);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Check for non-null id ...
    if (t instanceof StrictModelId) {
      assertNotNull(((StrictModelId) t).id());
      assertNotNull(UUID.fromString(((StrictModelId) t).id()));
    }

    // Follow all links and make sure they return valid HTTP 2xx results ...
    if (followLinks) {
      OkHttpClient client = clientFromBuilder(b);
      for (String link : linksToFollow) {
        try {
          Response r = client.newCall(new Request.Builder().url(link).build()).execute();
          assertNotNull(r.body().string());
          if (r.code() != 200) System.err.println("BAD LINK !!! " + r.toString());
          if (r.code() != 404) { // TODO remove this when platform 404s are fixed
            assertEquals(200, r.code());
            System.out.printf("followed link %s\n", link);
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Sanity check gson / serializable compat ...
    if (gson) {

      // to and from gson ...
      Gson g = ModelMiscTestUtil.gson();
      T gt = g.fromJson(g.toJson(t), cls);

      // to and from serialized bytes ...
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(baos);
        oos.writeObject(gt);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ois = new ObjectInputStream(bais);
        //noinspection unchecked
        gt = (T) ois.readObject();
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        if (oos != null) {
          try {
            oos.close();
          } catch (Exception ignored) {
          }
        }
        if (ois != null) {
          try {
            ois.close();
          } catch (Exception ignored) {
          }
        }
      }

      // ... recheck to make sure it's still sane after all that
      sanityCheckModel(gt, cls, b, followLinks, false);
    }
  }

  private static <T> Subscriber<T> testSub() {
    return testSub(null);
  }

  private static <T> Subscriber<T> testSub(final Action1<T> action) {
    return new Subscriber<T>() {
      @Override
      public void onCompleted() {
        unsubscribe();
      }

      @Override
      public void onError(Throwable e) {
        unsubscribe();
        if (!NetworkErrors.isCode(e, 404)) {
          e.printStackTrace();
          assertTrue(false);
        }
      }

      @Override
      public void onNext(T t) {
        if (action != null) action.call(t);
      }
    };
  }

  private static <T> Action1<T> simplePrintAction(final PrintStream ps, final String str) {
    return new Action1<T>() {
      @Override
      public void call(T t) {
        ps.println( //
            str.replaceAll("\\{time\\}", format(Locale.US, "[%d]", currentTimeMillis()))
                .replace("{val}", t == null
                    ? "null"
                    : t.toString()) //
        );
      }
    };
  }

  private static <T> Action1<T> assertEqAction(final T t1) {
    return new Action1<T>() {
      @Override
      public void call(T t2) {
        boolean result = t1.equals(t2);
        if (!result) System.err.println(format("assertEqAction failure: %s vs %s", t1, t2));
        assertTrue(result);
      }
    };
  }

  private static <MP extends ModelPage<? extends StrictModel>> Action1<MP> checkPageAction(
      final int limit, final int offset) {
    final AtomicInteger off = new AtomicInteger(offset);
    return new Action1<MP>() {
      @Override
      public void call(MP page) {
        assertNotNull(page);
        assertNotNull(page.extract());
        assertTrue(page.extract().size() <= limit);
        assertEquals(limit, page.limit());
        assertEquals(off.getAndAdd(limit), page.offset());
        assertTrue(page.total() >= page.extract().size());
      }
    };
  }

  private static <MP extends ModelTimeSeries<? extends StrictModel>> Action1<MP> checkTmSerAction(
      final int limit, final SortDir sortDir) {
    return new Action1<MP>() {
      @Override
      public void call(MP ts) {
        assertNotNull(ts);
        assertNotNull(ts.extract());
        assertFalse(ts.extract().size() > limit);
        assertEquals(limit, ts.limit());
        assertTrue(ts.remaining() >= 0);
        assertEquals(sortDir, ts.sortDir());
        assertNotNull(ts.until());
        assertNotNull(ts.since());
      }
    };
  }

  private static Action1<StrictModel> assertNullAction() {
    return new Action1<StrictModel>() {
      @Override
      public void call(StrictModel m) {
        assertNull(m);
      }
    };
  }

  private static Action1<Device> checkDeviceAction(@NonNull final Builder b) {
    return new Action1<Device>() {
      @Override
      public void call(Device device) {
        sanityCheckModel(device, Device.class, b, true);
        assertNotNull(device.chipId());
        assertNotNull(device.createdAt());
      }
    };
  }

  private static Action1<Vehicle> checkVehicleAction(@NonNull final Builder b) {
    return new Action1<Vehicle>() {
      @Override
      public void call(Vehicle v) {
        sanityCheckModel(v, Vehicle.class, b, true);
        assertNotNull(v.createdAt());
        assertNotNull(v.data());
      }
    };
  }

  private static Action1<Collision> checkCollisionAction(@NonNull final Builder b) {
    return new Action1<Collision>() {
      @Override
      public void call(Collision collision) {
        sanityCheckModel(collision, Collision.class, b, false);
        assertNotNull(collision.deviceId());
        assertNotNull(collision.vehicleId());
        assertNotNull(collision.timestamp());
      }
    };
  }

  private static Action1<BatteryStatus> checkBatteryStatusAction(@NonNull final Builder b) {
    return new Action1<BatteryStatus>() {
      @Override
      public void call(BatteryStatus batteryStatus) {
        sanityCheckModel(batteryStatus, BatteryStatus.class, b, false);
        assertNotNull(batteryStatus.status());
        assertFalse(batteryStatus.status().equals(BatteryStatus.Color.UNKNOWN));
        assertNotNull(batteryStatus.timestamp());
      }
    };
  }

  private static Action1<Distance> checkDistanceAction(@NonNull final Builder b,
      @NonNull final Unit u) {
    return new Action1<Distance>() {
      @Override
      public void call(Distance distance) {
        sanityCheckModel(distance, Distance.class, b, false);
        assertNotNull(distance.vehicleId());
        assertTrue(distance.value() >= 0f);
        assertTrue(distance.confidenceMin() >= 0f);
        assertTrue(distance.confidenceMax() >= 0f);
        assertTrue(u.equals(distance.unit()));
        assertFalse(distance.unit().equals(Unit.UNKNOWN));
      }
    };
  }

  private static Action1<Odometer> checkOdometerAction(@NonNull final Builder b) {
    return new Action1<Odometer>() {
      @Override
      public void call(Odometer odometer) {
        sanityCheckModel(odometer, Odometer.class, b, false);
        assertNotNull(odometer.vehicleId());
        assertTrue(odometer.reading() >= 0f);
        assertFalse(odometer.unit().equals(Unit.UNKNOWN));
        assertNotNull(odometer.timestamp());
      }
    };
  }

  private static Action1<OdometerTrigger> checkOdometerTriggerAction(@NonNull final Builder b) {
    return new Action1<OdometerTrigger>() {
      @Override
      public void call(OdometerTrigger odometerTrigger) {
        sanityCheckModel(odometerTrigger, OdometerTrigger.class, b, false);
        assertNotNull(odometerTrigger.vehicleId());
        assertTrue(odometerTrigger.threshhold()>= 0f);
        assertNotNull(odometerTrigger.unit());
        assertTrue(odometerTrigger.events()>= 0f);
        assertNotNull(odometerTrigger.type());
      }
    };
  }

  private static Action1<Dtc> checkDtcAction(@NonNull final Builder b) {
    return new Action1<Dtc>() {
      @Override
      public void call(Dtc dtc) {
        sanityCheckModel(dtc, Dtc.class, b, false);
        assertNotNull(dtc.vehicleId());
        assertNotNull(dtc.deviceId());
        assertNotNull(dtc.number());
        assertNotNull(dtc.description());
        assertNotNull(dtc.start());
      }
    };
  }

  private static Action1<DtcDiagnosis> checkDtcDiagAction(@NonNull final Builder b) {
    return new Action1<DtcDiagnosis>() {
      @Override
      public void call(DtcDiagnosis diag) {
        sanityCheckModel(diag, DtcDiagnosis.class, b, false);
        assertNotNull(diag.number());
        assertNotNull(diag.description());
      }
    };
  }

  private static Action1<Event> checkEventAction(@NonNull final Builder b) {
    return new Action1<Event>() {
      @Override
      public void call(Event event) {
        sanityCheckModel(event, Event.class, b, false);
        assertNotNull(event.deviceId());
        assertNotNull(event.timestamp());
        assertNotNull(event.type());
      }
    };
  }

  private static Action1<Location> checkLocationAction(@NonNull final Builder b) {
    return new Action1<Location>() {
      @Override
      public void call(Location loc) {
        sanityCheckModel(loc, Location.class, b, false);
        assertNotNull(loc.latLon());
        assertNotNull(loc.timestamp());
      }
    };
  }

  private static Action1<Message> checkMessageAction(@NonNull final Builder b) {
    return new Action1<Message>() {
      @Override
      public void call(Message msg) {
        sanityCheckModel(msg, Message.class, b, false);
        assertNotNull(msg.data());
        assertNotNull(msg.timestamp());
      }
    };
  }

  private static Action1<Notification> checkNotificationAction(@NonNull final Builder b) {
    return new Action1<Notification>() {
      @Override
      public void call(Notification not) {
        sanityCheckModel(not, Notification.class, b, false);
        assertNotNull(not.eventId());
        assertNotNull(not.eventType());
        assertNotNull(not.eventTimestamp());
        assertNotNull(not.subscriptionId());
        assertNotNull(not.url());
        assertNotNull(not.payload());
        assertNotNull(not.state());
        assertNotNull(not.createdAt());
      }
    };
  }

  private static Action1<Trip> checkTripAction(@NonNull final Builder b,
      final boolean followLinks) {
    return new Action1<Trip>() {
      @Override
      public void call(Trip trip) {
        sanityCheckModel(trip, Trip.class, b, followLinks);
        assertNotNull(trip.deviceId());
        assertNotNull(trip.vehicleId());
        assertNotNull(trip.status());
        assertNotNull(trip.start());
        assertNotNull(trip.stats());
      }
    };
  }

  private static Action1<User> checkUserAction(@NonNull final Builder b) {
    return new Action1<User>() {
      @Override
      public void call(User user) {
        sanityCheckModel(user, User.class, b, false);
        assertNotNull(user.email());
        //assertNotNull(user.createdAt()); // :( :( :(
        //assertNotNull(user.updatedAt()); // :( :( :(
        assertNotNull(user.settings().unit());
        assertFalse(user.settings().unit().equals(User.Unit.UNKNOWN));
        assertNotNull(user.settings().locale());
      }
    };
  }

  private static Action1<ReportCard> checkReportCardAction(@NonNull final Builder b,
      final boolean followLinks) {
    return new Action1<ReportCard>() {
      @Override
      public void call(ReportCard reportCard) {
        sanityCheckModel(reportCard, ReportCard.class, b, followLinks);
        assertNotNull(reportCard.deviceId());
        assertNotNull(reportCard.vehicleId());
        assertNotNull(reportCard.tripId());
        assertNotNull(reportCard.grade());
        assertFalse(reportCard.grade().equals(ReportCard.Grade.UNKNOWN));
      }
    };
  }

  private static Action1<OverallReportCard> checkOverallReportCardAction(@NonNull final Builder b,
      final boolean followLinks) {
    return new Action1<OverallReportCard>() {
      @Override
      public void call(OverallReportCard reportCard) {
        sanityCheckModel(reportCard, OverallReportCard.class, b, followLinks);
        assertTrue(reportCard.tripSampleSize() >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.A) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.B) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.C) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.D) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.F) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.I) >= 0);
        assertTrue(reportCard.gradeCount(ReportCard.Grade.UNKNOWN) >= 0);
        assertNotNull(reportCard.overallGrade());
        assertFalse(reportCard.overallGrade().equals(ReportCard.Grade.UNKNOWN));
      }
    };
  }

  @Test
  public void testUsers() {

    Observable.from(tokens) //
        .flatMap(new Func1<String, Observable<?>>() {
          @Override
          public Observable<?> call(String token) {
            Builder b = builder.copy() //
                .accessToken(token) //
                .retryPolicy(exponential(2, SECONDS, true));
            return b.getCurrentUser() //
                .build() //
                .observeExtracted() //
                .doOnNext(checkUserAction(b));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetDevices() {

    for (String t : tokens) {
      System.err.println(baseBuilder().accessToken(t).getCacheKey());
      System.err.println(baseBuilder().accessToken(t).getCacheKey());
      System.err.println(baseBuilder().accessToken(t).getCacheKey());
      System.err.println(baseBuilder().accessToken(t).getCacheKey());
      System.err.println("----------");
    }

    sharedDeviceObs //
        .flatMap(new Func1<DeviceBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(DeviceBuilderPair pair) {
            checkDeviceAction(pair.second).call(pair.first);
            return pair.second.getDevice(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkDeviceAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetVehicles() {

    // FIXME: failing test, requires platform fix on lastStartup model inconsistency
    sharedVehicleObs //
        .flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(VehicleBuilderPair pair) {
            checkVehicleAction(pair.second).call(pair.first);
            return pair.second.getVehicle(pair.first.id()) //
                .build() //
                .observeExtracted() //
                //.doOnNext(assertEqAction(pair.first)) // TODO turn this back on
                .doOnNext(checkVehicleAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetCollisions() {

    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        .flatMap(new Func1< //
            BuilderPair<? extends StrictModelId>, Observable<CollisionBuilderPair>>() {
          @Override
          public Observable<CollisionBuilderPair> call(BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getCollisions()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(15)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .doOnNext(checkTmSerAction(15, DESCENDING))
                .flatMap(VinliRx.<Collision>flattenTimeSeries())
                .doOnNext(checkCollisionAction(pair.second))
                .map(CollisionBuilderPair.mapFunc(pair.second));
          }
        }) //
        .flatMap(new Func1<CollisionBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(CollisionBuilderPair pair) {
            return pair.second.getCollision(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkCollisionAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetBatteryStatus() {

    sharedVehicleObs //
        // Slow down emissions so tests aren't a DDoS.
        .compose(VinliRx.<VehicleBuilderPair>delayEach(2, SECONDS)) //
        .flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(VehicleBuilderPair pair) {
            return pair.second.copy()
                .missingResourcesAsNull(true)
                .getCurrentBatteryStatus()
                .forId(VEHICLE, pair.first.id())
                .build()
                .observeExtracted()
                .filter(VinliRx.<BatteryStatus>nonNull())
                .doOnNext(checkBatteryStatusAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetLatestDistance() {

    sharedVehicleObs //
        .flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(VehicleBuilderPair pair) {
            return pair.second.getLatestDistance(KILOMETERS)
                .forId(VEHICLE, pair.first.id())
                .build()
                .observeExtracted()
                .doOnNext(checkDistanceAction(pair.second, KILOMETERS));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetDtcs() {

    sharedVehicleObs //
        .take(10) //
        .flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(VehicleBuilderPair pair) {
            Observable<Dtc.TimeSeries> o1 = pair.second.getDtcs()
                .forId(VEHICLE, pair.first.id())
                .limit(5)
                .build()
                .observeAll()
                .take(2);
            Observable<Dtc.TimeSeries> o2 = pair.second.getDtcsWithState(ACTIVE)
                .forId(VEHICLE, pair.first.id())
                .limit(5)
                .build()
                .observeAll()
                .take(2);
            Observable<Dtc.TimeSeries> o3 = pair.second.getDtcsWithState(INACTIVE)
                .forId(VEHICLE, pair.first.id())
                .limit(5)
                .build()
                .observeAll()
                .take(2);
            return Observable.merge(o1, o2, o3)
                .doOnNext(checkTmSerAction(5, DESCENDING))
                .flatMap(VinliRx.<Dtc>flattenTimeSeries())
                .doOnNext(checkDtcAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testDiagnoseDtc() {

    final Builder b = builder.copy().accessToken(tokens.get(0));

    b.diagnoseDtc("P0087")
        .build()
        .observeExtracted()
        .doOnNext(checkDtcDiagAction(b))
        .flatMap(new Func1<DtcDiagnosis, Observable<DtcDiagnosis>>() {
          @Override
          public Observable<DtcDiagnosis> call(DtcDiagnosis diag) {
            return b.getDtcDiagnosis(diag.id()).build().observeExtracted();
          }
        })
        .toBlocking()
        .subscribe(testSub(checkDtcDiagAction(b)));

    b.copy()
        .missingResourcesAsNull(true)
        .diagnoseDtc("P1111")
        .build()
        .observeExtracted()
        .toBlocking()
        .subscribe(testSub(assertNullAction()));
  }

  @Test
  public void testEvents() {

    // FIXME: failing test, requires platform fix on GET vehicles/{vehicleId}/events
    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        // Slow down emissions so tests aren't a DDoS.
        .compose(VinliRx.<BuilderPair<? extends StrictModelId>>delayEach(500, MILLISECONDS)) //
        .flatMap(new Func1<BuilderPair<? extends StrictModelId>, Observable<EventBuilderPair>>() {
          @Override
          public Observable<EventBuilderPair> call(BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getEvents()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(15)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(15, DESCENDING))
                .flatMap(VinliRx.<Event>flattenTimeSeries())
                .doOnNext(checkEventAction(pair.second))
                .map(EventBuilderPair.mapFunc(pair.second));
          }
        }) //
        .compose(VinliRx.<EventBuilderPair>collectAllDistinct()) // Load all pages ...
        .flatMap(VinliRx.<EventBuilderPair>flatten()) // ... before re-emitting as items ...
        .compose(VinliRx.<EventBuilderPair>delayEach(500, MILLISECONDS)) // ... slowed down.
        .flatMap(new Func1<EventBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(EventBuilderPair pair) {
            return pair.second.getEvent(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkEventAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetLocations() {

    // FIXME: failing test, requires platform fix on locations / messages (vehicularization prob)
    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        // Slow down emissions so tests aren't a DDoS.
        //.compose(VinliRx.<BuilderPair<? extends StrictModelId>>delayEach(500, MILLISECONDS)) //
        .flatMap(new Func1< //
            BuilderPair<? extends StrictModelId>, Observable<LocationBuilderPair>>() {
          @Override
          public Observable<LocationBuilderPair> call(BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getLocations()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(15)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(15, DESCENDING))
                .flatMap(VinliRx.<Location>flattenTimeSeries())
                .doOnNext(checkLocationAction(pair.second))
                .map(LocationBuilderPair.mapFunc(pair.second));
          }
        }) //
        //.compose(VinliRx.<LocationBuilderPair>collectAllDistinct()) // Load all pages ...
        //.flatMap(VinliRx.<LocationBuilderPair>flatten()) // ... before re-emitting as items ...
        //.compose(VinliRx.<LocationBuilderPair>delayEach(500, MILLISECONDS)) // ... slowed down.
        .flatMap(new Func1<LocationBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(LocationBuilderPair pair) {
            return pair.second.getLocation(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Message>>() {
                  @Override
                  public Observable<? extends Message> call(Throwable throwable) {
                    if (NetworkErrors.isCode(throwable, 404)) return Observable.empty();
                    return Observable.error(throwable);
                  }
                }) // TODO - remove this 404 ignore, just for now...
                //.doOnNext(assertEqAction(pair.first)) // No need; location by id is a Message
                .doOnNext(checkMessageAction(pair.second));
          }
        }) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testGetMessages() {

    // FIXME: failing test, requires platform fix on locations / messages (vehicularization prob)
    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        // Slow down emissions so tests aren't a DDoS.
        //.compose(VinliRx.<BuilderPair<? extends StrictModelId>>delayEach(2, SECONDS)) //
        .flatMap(new Func1< //
            BuilderPair<? extends StrictModelId>, Observable<MessageBuilderPair>>() {
          @Override
          public Observable<MessageBuilderPair> call(BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getMessages()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(4)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(4, DESCENDING))
                .flatMap(VinliRx.<Message>flattenTimeSeries())
                .doOnNext(checkMessageAction(pair.second))
                .map(MessageBuilderPair.mapFunc(pair.second));
          }
        }) //
        //.buffer(1, 4).flatMap(VinliRx.<MessageBuilderPair>flatten())
        //.compose(VinliRequest.<MessageBuilderPair>collectAllDistinct()) // Load all pages ...
        //.flatMap(VinliRequest.<MessageBuilderPair>flatten()) // ... before re-emitting as items ...
        //.compose(VinliRequest.<MessageBuilderPair>delayEach(100, MILLISECONDS)) // ... slowed down.
        .flatMap(new Func1<MessageBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(MessageBuilderPair pair) {
            return pair.second.getMessage(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Message>>() {
                  @Override
                  public Observable<? extends Message> call(Throwable throwable) {
                    if (NetworkErrors.isCode(throwable, 404)) return Observable.empty();
                    return Observable.error(throwable);
                  }
                }) // TODO - remove this 404 ignore, just for now...
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkMessageAction(pair.second));
          }
        }) //
        //.skip(255).take(1) //
        .toBlocking() //
        .subscribe(testSub());
  }

  // TODO - when subscriptions model is in
  //@Test
  //public void testGetNotificationsForSubscription() {
  //
  //  final Builder b = builder.copy()
  //      .accessToken("S76PjsfBKE1H820z56Ja9LlbL96Zk8Mo9_XwVVnKWD28NaDBUTqulSa8iFEwmtqs");
  //
  //  b.getNotifications()
  //      .forId(SUBSCRIPTION, "6348305f-f64a-4c0f-a958-cb2d9190f11a")
  //      .limit(5)
  //      .sortDir(DESCENDING)
  //      .build()
  //      .observeAll()
  //      .take(2)
  //      .doOnNext(checkTmSerAction(5, DESCENDING))
  //      .flatMap(VinliRequest.<VinliRequest.Notification>flattenTimeSeries())
  //      .toBlocking()
  //      .subscribe(testSub(checkNotificationAction(b)));
  //}

  @Test
  public void testNotifications() {

    // FIXME: failing test, requires platform fix on GET vehicles/{vehicleId}/events
    sharedDeviceObs //
        // Slow down emissions so tests aren't a DDoS.
        //.compose(VinliRx.<DeviceBuilderPair>delayEach(500, MILLISECONDS)) //
        .flatMap(new Func1<DeviceBuilderPair, Observable<EventBuilderPair>>() {
          @Override
          public Observable<EventBuilderPair> call(DeviceBuilderPair pair) {
            return pair.second.getEvents()
                .forId(DEVICE, pair.first.id())
                .limit(8)
                .build()
                .observeAll()
                .take(2)
                .flatMap(VinliRx.<Event>flattenTimeSeries())
                .map(EventBuilderPair.mapFunc(pair.second));
          }
        }) //
        //.take(64) //
        //.compose(VinliRequest.<EventBuilderPair>collectAllDistinct()) // Load all pages ...
        //.flatMap(VinliRequest.<EventBuilderPair>flatten()) // ... before re-emitting as items ...
        //.compose(VinliRx.<EventBuilderPair>delayEach(500, MILLISECONDS)) // ... slowed down.
        .flatMap(new Func1<EventBuilderPair, Observable<NotificationBuilderPair>>() {
          @Override
          public Observable<NotificationBuilderPair> call(EventBuilderPair pair) {
            return pair.second.getNotifications()
                .forId(EVENT, pair.first.id())
                .limit(8)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(8, DESCENDING))
                .flatMap(VinliRx.<Notification>flattenTimeSeries())
                .doOnNext(checkNotificationAction(pair.second))
                .map(NotificationBuilderPair.mapFunc(pair.second));
          }
        }) //
        //.take(32) //
        //.compose(VinliRequest.<NotificationBuilderPair>collectAllDistinct()) //
        //.flatMap(VinliRequest.<NotificationBuilderPair>flatten()) //
        //.compose(VinliRx.<NotificationBuilderPair>delayEach(500, MILLISECONDS)) //
        .flatMap(new Func1<NotificationBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(NotificationBuilderPair pair) {
            return pair.second.getNotification(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkNotificationAction(pair.second));
          }
        }) //
        //.skip(15).first() //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testTrips() {

    // FIXME: failing test, requires platform fix on GET vehicles/{vehicleId}/trips (404)
    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        // Slow down emissions so tests aren't a DDoS.
        //.compose(VinliRequest.<BuilderPair<? extends StrictModelId>>delayEach(2, SECONDS)) //
        .flatMap(new Func1<BuilderPair<? extends StrictModelId>, Observable<TripBuilderPair>>() {
          @Override
          public Observable<TripBuilderPair> call(final BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getTrips()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(4)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(4, DESCENDING))
                .flatMap(VinliRx.<Trip>flattenTimeSeries())
                .doOnNext(checkTripAction(pair.second, false))
                .map(TripBuilderPair.mapFunc(pair.second));
          }
        }) //
        .buffer(1, 4).flatMap(VinliRx.<TripBuilderPair>flatten()) // only pass 1/4 along
        //.compose(VinliRequest.<TripBuilderPair>collectAllDistinct()) // Load all pages ...
        //.flatMap(VinliRequest.<TripBuilderPair>flatten()) // ... before re-emitting as items ...
        //.compose(VinliRequest.<TripBuilderPair>delayEach(500, MILLISECONDS)) // ... slowed down.
        .flatMap(new Func1<TripBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(TripBuilderPair pair) {
            return pair.second.getTrip(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkTripAction(pair.second, true));
          }
        }) //
        .take(512) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testReportCards() {

    // FIXME: failing test, requires platform fix on GET vehicles/{vehicleId}/report_cards (404)
    // FIXME: GET report_cards/{id} also authing wrong
    Observable.merge(sharedDeviceObs, sharedVehicleObs) //
        // Slow down emissions so tests aren't a DDoS.
        .compose(VinliRx.<BuilderPair<? extends StrictModelId>>delayEach(2, SECONDS)) //
        .flatMap(new Func1< //
            BuilderPair<? extends StrictModelId>, Observable<ReportCardBuilderPair>>() {
          @Override
          public Observable<ReportCardBuilderPair> call(
              final BuilderPair<? extends StrictModelId> pair) {
            return pair.second.getReportCards()
                .forId(pair.first instanceof Device
                    ? DEVICE
                    : VEHICLE, pair.first.id())
                .limit(4)
                .sortDir(DESCENDING)
                .build()
                .observeAll()
                .take(2)
                .doOnNext(checkTmSerAction(4, DESCENDING))
                .flatMap(VinliRx.<ReportCard>flattenTimeSeries())
                .doOnNext(checkReportCardAction(pair.second, false))
                .map(ReportCardBuilderPair.mapFunc(pair.second));
          }
        }) //
        .buffer(1, 8).flatMap(VinliRx.<ReportCardBuilderPair>flatten()) // only pass 1/8 along
        .flatMap(new Func1<ReportCardBuilderPair, Observable<?>>() {
          @Override
          public Observable<?> call(final ReportCardBuilderPair pair) {
            return pair.second.getReportCard(pair.first.id()) //
                .build() //
                .observeExtracted() //
                .doOnNext(assertEqAction(pair.first)) //
                .doOnNext(checkReportCardAction(pair.second, true))
                .doOnError(new Action1<Throwable>() {
                  @Override
                  public void call(Throwable throwable) {
                    System.err.println(
                        "err for " + accessTokenFromBuilder(pair.second) + " : " + pair.first.id());
                  }
                });
          }
        }) //
        .take(512) //
        .toBlocking() //
        .subscribe(testSub());
  }

  @Test
  public void testOverallReportCards() {

    sharedDeviceObs //
        // Slow down emissions so tests aren't a DDoS.
        .compose(VinliRx.<DeviceBuilderPair>delayEach(2, SECONDS)) //
        .flatMap(new Func1<DeviceBuilderPair, Observable<OverallReportCardBuilderPair>>() {
          @Override
          public Observable<OverallReportCardBuilderPair> call(final DeviceBuilderPair pair) {
            return pair.second.getOverallReportCardSince(currentTimeMillis() - DAYS.toMillis(14))
                .forId(DEVICE, pair.first.id())
                .build()
                .observeExtracted()
                .doOnNext(checkOverallReportCardAction(pair.second, true))
                .map(OverallReportCardBuilderPair.mapFunc(pair.second));
          }
        }) //
        .take(512) //
        .toBlocking() //
        .subscribe(testSub());
  }

  //@Test
  //public void asdasdasd() {
  //  //PageRequest<Device, Device.Page> req = VinliRequest.builder() //
  //  //    .accessToken("asdf") //
  //  //    .getDevices() //
  //  //    .build();
  //  //
  //  //req.pageBuilder() //
  //  //    .baseBuilder() //
  //  //    .accessToken("asdf") //
  //  //    .getVehicles() //
  //  //    .forId(DEVICE, "hjkl") //
  //  //    .build();
  //
  //  baseBuilder() //
  //      .logLevel(HttpLoggingInterceptor.Level.BODY) //
  //      .accessToken("ZEfTFVOoTCHZWmMhR9IOjl3NbA0c7umaIXhbVELXF9EG3_v0S_cToLQwr8oea26c") //
  //      .createRule(RuleSeed.create() //
  //          .name("testrule") //
  //          //.boundary(ParametricBoundary.create() //
  //          //    .parameter("vehicleSpeed") //
  //          //    .max(75)) //
  //          //.boundary(RadiusBoundary.create() //
  //          //    .radius(100) //
  //          //    .lat(32.7767) //
  //          //    .lon(96.7970)) //
  //          .boundary(PolygonBoundary.create() //
  //              .newPolygon() //
  //              .addCoord(new double[] { 32.833765, -96.702414 }) // 1
  //              .addCoord(new double[] { 32.823091, -96.714859 }) // 2
  //              .addCoord(new double[] { 32.811928, -96.701307 }) // 3
  //              .addCoord(new double[] { 32.833765, -96.702414 }) // 1
  //              .newPolygon() //
  //              .addCoord(new double[] { 32.817586, -96.689671 }) // 4
  //              .addCoord(new double[] { 32.833765, -96.702414 }) // 1
  //              .addCoord(new double[] { 32.811928, -96.701307 }) // 3
  //              .addCoord(new double[] { 32.817586, -96.689671 }) // 4
  //              .newPolygon() //
  //              .addCoord(new double[] { 32.823091, -96.714859 }) // 2
  //              .addCoord(new double[] { 32.811928, -96.701307 }) // 3
  //              .addCoord(new double[] { 32.817586, -96.689671 }) // 4
  //              .addCoord(new double[] { 32.823091, -96.714859 }) // 2
  //          )) //
  //      .forId(DEVICE, "3d5d845c-5cc1-4038-aca7-94ac8878ed3b") //
  //      .build() //
  //      .observeExtractedWithBaseBuilder() //
  //      .doOnNext(simplePrintAction(System.err, "rule created ...")) //
  //      .flatMap(new Func1<Pair<Rule, Builder>, Observable<?>>() {
  //        @Override
  //        public Observable<?> call(Pair<Rule, Builder> ruleBuilderPair) {
  //          return ruleBuilderPair.second //
  //              .deleteRule(ruleBuilderPair.first.id()) //
  //              .build() //
  //              .observe();
  //        }
  //      }) //
  //      .doOnNext(simplePrintAction(System.err, "... rule deleted!")) //
  //      .toBlocking() //
  //      .subscribe(testSub());
  //}

  //@Test
  //public void tefdasdasd() {
  //
  //  final VinliRequest.RetryPolicy retry =
  //      VinliRequest.RetryPolicy.exponential(2, TimeUnit.SECONDS, true);
  //
  //  Observable.error(new retrofit2.adapter.rxjava.HttpException(
  //      Response.error(500, ResponseBody.create(null, ""))))
  //      .doOnSubscribe(new Action0() {
  //        @Override
  //        public void call() {
  //          System.err.println("child subbed ...");
  //        }
  //      })
  //      .delay(1, TimeUnit.SECONDS)
  //      .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
  //        @Override
  //        public Observable<?> call(final Observable<? extends Throwable> attempts) {
  //          return Observable.using(new Func0<AtomicInteger>() {
  //            @Override
  //            public AtomicInteger call() {
  //              return new AtomicInteger();
  //            }
  //          }, new Func1<AtomicInteger, Observable<?>>() {
  //            @Override
  //            public Observable<?> call(final AtomicInteger ctr) {
  //              return attempts.flatMap(new Func1<Throwable, Observable<?>>() {
  //                @Override
  //                public Observable<?> call(Throwable throwable) {
  //                  //noinspection ConstantConditions
  //                  long delay = retry.delay(throwable, ctr.incrementAndGet());
  //                  System.err.println("delaying by " + delay);
  //                  return Observable.timer(delay, TimeUnit.MILLISECONDS);
  //                }
  //              });
  //            }
  //          }, new Action1<AtomicInteger>() {
  //            @Override
  //            public void call(AtomicInteger ai) {
  //            }
  //          });
  //          //return attempts.zipWith(Observable.range(1, 3), new Func2<Throwable, Integer, Integer>() {
  //          //  @Override
  //          //  public Integer call(Throwable t, Integer i) {
  //          //    if (t instanceof HttpException && ((HttpException) t).code() / 100 == 5) return i;
  //          //    throw new RuntimeException(t);
  //          //  }
  //          //}).flatMap(new Func1<Integer, Observable<?>>() {
  //          //  @Override
  //          //  public Observable<?> call(Integer i) {
  //          //    return Observable.timer(i, TimeUnit.SECONDS);
  //          //  }
  //          //});
  //        }
  //      })
  //      .toBlocking()
  //      .subscribe(testSub());
  //}

  @Test
  public void testGetOdometers() {
    sharedVehicleObs.flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
      @Override
      public Observable<?> call(VehicleBuilderPair pair) {
        Observable<Odometer.TimeSeries> o1 = pair.second.getOdometers()
            .forId(VEHICLE, pair.first.id())
            .limit(1)
            .build()
            .observeAll()
            .take(1);
        return o1.doOnNext(checkTmSerAction(1, DESCENDING))
            .flatMap(VinliRx.<Odometer>flattenTimeSeries())
            .doOnNext(checkOdometerAction(pair.second));
      }
    }).toBlocking().subscribe(testSub());

  }

  @Test
  public void testGetOdometerTriggers() {
    sharedVehicleObs.flatMap(new Func1<VehicleBuilderPair, Observable<?>>() {
      @Override
      public Observable<?> call(VehicleBuilderPair pair) {
        Observable<OdometerTrigger.TimeSeries> o1 = pair.second.getOdometerTriggers()
            .forId(VEHICLE, pair.first.id())
            .limit(1)
            .build()
            .observeAll()
            .take(1);
        return o1.doOnNext(checkTmSerAction(1, DESCENDING))
            .flatMap(VinliRx.<OdometerTrigger>flattenTimeSeries())
            .doOnNext(checkOdometerTriggerAction(pair.second));
      }
    }).toBlocking().subscribe(testSub());

  }
}
