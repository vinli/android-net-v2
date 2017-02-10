package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import li.vin.netv2.internal.CachedHttpClients.ClientAndServices;
import li.vin.netv2.model.BatteryStatus;
import li.vin.netv2.model.Collision;
import li.vin.netv2.model.Device;
import li.vin.netv2.model.Distance;
import li.vin.netv2.model.Dtc;
import li.vin.netv2.model.DtcDiagnosis;
import li.vin.netv2.model.Event;
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
import li.vin.netv2.model.Trip;
import li.vin.netv2.model.User;
import li.vin.netv2.model.Vehicle;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.ModelWrapper;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.request.VinliRequest.Builder;
import li.vin.netv2.util.LazyOrSet;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static java.lang.String.format;
import static li.vin.netv2.request.ForId.DEVICE;
import static li.vin.netv2.request.ForId.EVENT;
import static li.vin.netv2.request.ForId.SUBSCRIPTION;
import static li.vin.netv2.request.ForId.TRIP;
import static li.vin.netv2.request.ForId.VEHICLE;
import static li.vin.netv2.request.RequestPkgHooks.dtcDiagWrapper;
import static li.vin.netv2.request.RequestPkgHooks.odometerSeedWrapper;
import static li.vin.netv2.request.RequestPkgHooks.odometerTriggerSeedWrapper;
import static li.vin.netv2.request.RequestPkgHooks.ruleSeedWrapper;

class RequestFactories {

  RequestFactories() {
  }

  // lazy init singleton inst

  static final LazyOrSet<RequestFactories> inst = //
      LazyOrSet.create(new Func0<RequestFactories>() {
        @Override
        public RequestFactories call() {
          return new RequestFactories();
        }
      });

  // default impl

  public PageBuilder<Device, Device.Page> devicesPageBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new PageBuilder<>(builder, new PageObservableFactory<Device, Device.Page>() {
      @NonNull
      @Override
      public Observable<Device.Page> call(@NonNull PageBuilder<Device, Device.Page> b) {
        if (b.link != null) return client.devices.get().devicesForUrl(b.link);
        return client.devices.get().devices(b.limit, b.offset);
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<Device, Device.Wrapper> deviceWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Device, Device.Wrapper>() {
      @NonNull
      @Override
      public Observable<Device.Wrapper> call(@NonNull WrapperBuilder<Device, Device.Wrapper> b) {
        if (b.link != null) return client.devices.get().deviceForUrl(b.link);
        if (b.id != null) return client.devices.get().device(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public PageBuilder<Vehicle, Vehicle.Page> vehiclesPageBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new PageBuilder<>(builder, new PageObservableFactory<Vehicle, Vehicle.Page>() {
      @NonNull
      @Override
      public Observable<Vehicle.Page> call(@NonNull PageBuilder<Vehicle, Vehicle.Page> b) {
        if (b.link != null) return client.vehicles.get().vehiclesForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.vehicles.get().vehiclesForDevice(b.forIdVals.target, b.limit, b.offset);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(DEVICE));
  }

  public WrapperBuilder<Vehicle, Vehicle.Wrapper> latestVehicleWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Vehicle, Vehicle.Wrapper>() {
      @NonNull
      @Override
      public Observable<Vehicle.Wrapper> call(@NonNull WrapperBuilder<Vehicle, Vehicle.Wrapper> b) {
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.vehicles.get().latestVehicleForDevice(b.forIdVals.target);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(DEVICE));
  }

  public WrapperBuilder<Vehicle, Vehicle.Wrapper> vehicleWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Vehicle, Vehicle.Wrapper>() {
      @NonNull
      @Override
      public Observable<Vehicle.Wrapper> call(@NonNull WrapperBuilder<Vehicle, Vehicle.Wrapper> b) {
        if (b.link != null) return client.vehicles.get().vehicleForUrl(b.link);
        if (b.id != null) return client.vehicles.get().vehicle(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Collision, Collision.TimeSeries> collisionsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Collision, Collision.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Collision.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Collision, Collision.TimeSeries> b) {
        if (b.link != null) return client.collisions.get().collisionsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.collisions.get().collisionsForDevice( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.collisions.get().collisionsForVehicle( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<Collision, Collision.Wrapper> collisionWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<Collision, Collision.Wrapper>() {
      @NonNull
      @Override
      public Observable<Collision.Wrapper> call(
          @NonNull WrapperBuilder<Collision, Collision.Wrapper> b) {
        if (b.link != null) return client.collisions.get().collisionForUrl(b.link);
        if (b.id != null) return client.collisions.get().collision(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<BatteryStatus, BatteryStatus.Wrapper> batteryStatusWrapperBuilder(
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<BatteryStatus, BatteryStatus.Wrapper>() {
      @NonNull
      @Override
      public Observable<BatteryStatus.Wrapper> call(
          @NonNull WrapperBuilder<BatteryStatus, BatteryStatus.Wrapper> b) {
        if (b.link != null) return client.diagnostics.get().batteryStatusForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.diagnostics.get().currentBatteryStatus(b.forIdVals.target);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE));
  }

  public WrapperBuilder<Distance, Distance.Wrapper> distanceWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @NonNull final Distance.Unit unit) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<Distance, Distance.Wrapper>() {
      @NonNull
      @Override
      public Observable<Distance.Wrapper> call(
          @NonNull WrapperBuilder<Distance, Distance.Wrapper> b) {
        if (b.link != null) return client.distances.get().distanceForUrl(b.link, unit.toString());
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.distances.get().latestDistance(b.forIdVals.target, unit.toString());
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE));
  }

  public TimeSeriesBuilder<Odometer, Odometer.TimeSeries> odometersTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder,
        new TimeSeriesObservableFactory<Odometer, Odometer.TimeSeries>() {
          @NonNull
          @Override
          public Observable<Odometer.TimeSeries> call(
              @NonNull TimeSeriesBuilder<Odometer, Odometer.TimeSeries> b) {
            if (b.link != null) return client.distances.get().odometerReportsForUrl(b.link);
            if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
              return client.distances.get()
                  .odometerReports(b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
            }
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.of(VEHICLE));
  }

  public WrapperBuilder<Odometer, Odometer.Wrapper> odometerWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder,
        new WrapperObservableFactory<Odometer, Odometer.Wrapper>() {
          @NonNull
          @Override
          public Observable<Odometer.Wrapper> call(
              @NonNull WrapperBuilder<Odometer, Odometer.Wrapper> b) {
            if (b.link != null) return client.distances.get().odometerReportForUrl(b.link);
            if (b.id != null) return client.distances.get().odometerReport(b.id);
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<Odometer, Odometer.Wrapper> odometerCreateWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @NonNull final OdometerSeed seed) {
    return new WrapperBuilder<>(builder,
        new WrapperObservableFactory<Odometer, Odometer.Wrapper>() {
          @NonNull
          @Override
          public Observable<Odometer.Wrapper> call(
              @NonNull WrapperBuilder<Odometer, Odometer.Wrapper> b) {
            try {
              seed.validate();
            } catch (RuntimeException rte) {
              return Observable.error(rte);
            }
            if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
              return client.distances.get().createOdometerReport( //
                  b.forIdVals.target, odometerSeedWrapper(seed));
            }
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.of(VEHICLE));
  }

  public ItemBuilder<Void> odometerDeleteItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<Void>() {
      @NonNull
      @Override
      public Observable<Void> call(@NonNull ItemBuilder<Void> b) {
        if (b.id != null) return client.distances.get().deleteOdometerReport(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<OdometerTrigger, OdometerTrigger.TimeSeries> odometerTriggersTimeSeriesBuilder(
      //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder,
        new TimeSeriesObservableFactory<OdometerTrigger, OdometerTrigger.TimeSeries>() {
          @NonNull
          @Override
          public Observable<OdometerTrigger.TimeSeries> call(
              @NonNull TimeSeriesBuilder<OdometerTrigger, OdometerTrigger.TimeSeries> b) {
            if (b.link != null) return client.distances.get().odometerTriggersForUrl(b.link);
            if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
              return client.distances.get()
                  .odometerTriggers(b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
            }
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.of(VEHICLE));
  }

  public WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper> odometerTriggerWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder,
        new WrapperObservableFactory<OdometerTrigger, OdometerTrigger.Wrapper>() {
          @NonNull
          @Override
          public Observable<OdometerTrigger.Wrapper> call(
              @NonNull WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper> b) {
            if (b.link != null) return client.distances.get().odometerTriggerForUrl(b.link);
            if (b.id != null) return client.distances.get().odometerTrigger(b.id);
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper> odometerTriggerCreateWrapperBuilder(
      //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @NonNull final OdometerTriggerSeed seed) {
    return new WrapperBuilder<>(builder,
        new WrapperObservableFactory<OdometerTrigger, OdometerTrigger.Wrapper>() {
          @NonNull
          @Override
          public Observable<OdometerTrigger.Wrapper> call(
              @NonNull WrapperBuilder<OdometerTrigger, OdometerTrigger.Wrapper> b) {
            try {
              seed.validate();
            } catch (RuntimeException rte) {
              return Observable.error(rte);
            }
            if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
              return client.distances.get().createOdometerTrigger( //
                  b.forIdVals.target, odometerTriggerSeedWrapper(seed));
            }
            throw new RuntimeException("validations failed: this should never happen!");
          }
        }, EnumSet.of(VEHICLE));
  }

  public ItemBuilder<Void> odometerTriggerDeleteItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<Void>() {
      @NonNull
      @Override
      public Observable<Void> call(@NonNull ItemBuilder<Void> b) {
        if (b.id != null) return client.distances.get().deleteOdometerTrigger(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Dtc, Dtc.TimeSeries> dtcsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @Nullable final Dtc.State state) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Dtc, Dtc.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Dtc.TimeSeries> call(@NonNull TimeSeriesBuilder<Dtc, Dtc.TimeSeries> b) {
        if (b.link != null) return client.diagnostics.get().dtcsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.diagnostics.get().dtcs( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir, state == null
                  ? null
                  : state.toString());
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE));
  }

  public WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> diagDtcByNumberBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory< //
        DtcDiagnosis, DtcDiagnosis.Wrapper>() {
      @NonNull
      @Override
      public Observable<DtcDiagnosis.Wrapper> call(
          @NonNull WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> b) {
        Observable<DtcDiagnosis.Page> o;
        if (b.id != null) {
          o = client.diagnostics.get().diagnoseByNumber(b.id);
        } else {
          throw new RuntimeException("validations failed: this should never happen!");
        }
        return o.map(new Func1<DtcDiagnosis.Page, DtcDiagnosis.Wrapper>() {
          @Override
          public DtcDiagnosis.Wrapper call(DtcDiagnosis.Page page) {
            List<DtcDiagnosis> e = page.extract();
            if (e.isEmpty()) return dtcDiagWrapper(null);
            return dtcDiagWrapper(e.get(0));
          }
        });
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> diagDtcByIdBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory< //
        DtcDiagnosis, DtcDiagnosis.Wrapper>() {
      @NonNull
      @Override
      public Observable<DtcDiagnosis.Wrapper> call(
          @NonNull WrapperBuilder<DtcDiagnosis, DtcDiagnosis.Wrapper> b) {
        if (b.link != null) return client.diagnostics.get().diagnosisByIdForUrl(b.link);
        if (b.id != null) return client.diagnostics.get().diagnoseById(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Event, Event.TimeSeries> eventsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client, //
      @Nullable final String type, @Nullable final String objectId) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Event, Event.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Event.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Event, Event.TimeSeries> b) {
        if (b.link != null) return client.events.get().eventsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.events.get().eventsForDevice( //
              b.forIdVals.target, type, objectId, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.events.get().eventsForVehicle( //
              b.forIdVals.target, type, objectId, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<Event, Event.Wrapper> eventWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<Event, Event.Wrapper>() {
      @NonNull
      @Override
      public Observable<Event.Wrapper> call(@NonNull WrapperBuilder<Event, Event.Wrapper> b) {
        if (b.link != null) return client.events.get().eventForUrl(b.link);
        if (b.id != null) return client.events.get().event(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Location, Location.TimeSeries> locationsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Location, Location.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Location.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Location, Location.TimeSeries> b) {
        if (b.link != null) return client.locations.get().locationsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.locations.get().locationsForDevice( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.locations.get().locationsForVehicle( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public TimeSeriesBuilder<Message, Message.TimeSeries> messagesTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Message, Message.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Message.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Message, Message.TimeSeries> b) {
        if (b.link != null) return client.messages.get().messagesForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.messages.get().messagesForDevice( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.messages.get().messagesForVehicle( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<Message, Message.Wrapper> messageWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Message, Message.Wrapper>() {
      @NonNull
      @Override
      public Observable<Message.Wrapper> call(@NonNull WrapperBuilder<Message, Message.Wrapper> b) {
        if (b.link != null) return client.messages.get().messageForUrl(b.link);
        if (b.id != null) return client.messages.get().message(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Snapshot, Snapshot.TimeSeries> snapshotsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client, final String fields) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Snapshot, Snapshot.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Snapshot.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Snapshot, Snapshot.TimeSeries> b) {
        if (b.link != null) return client.snapshots.get().snapshotsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.snapshots.get()
              .snapshots(b.forIdVals.target, fields, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.snapshots.get()
              .vehicleSnapshots(b.forIdVals.target, fields, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public TimeSeriesBuilder<Notification, Notification.TimeSeries> notificationsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Notification, Notification.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Notification.TimeSeries> call(
          @NonNull TimeSeriesBuilder<Notification, Notification.TimeSeries> b) {
        if (b.link != null) return client.notifications.get().notificationsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == EVENT) {
          return client.notifications.get().notificationsForEvent( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == SUBSCRIPTION) {
          return client.notifications.get().notificationsForSubscription( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(EVENT, SUBSCRIPTION));
  }

  public WrapperBuilder<Notification, Notification.Wrapper> notificationWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<Notification, Notification.Wrapper>() {
      @NonNull
      @Override
      public Observable<Notification.Wrapper> call(
          @NonNull WrapperBuilder<Notification, Notification.Wrapper> b) {
        if (b.link != null) return client.notifications.get().notificationForUrl(b.link);
        if (b.id != null) return client.notifications.get().notification(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<Trip, Trip.TimeSeries> tripsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        Trip, Trip.TimeSeries>() {
      @NonNull
      @Override
      public Observable<Trip.TimeSeries> call(@NonNull TimeSeriesBuilder<Trip, Trip.TimeSeries> b) {
        if (b.link != null) return client.trips.get().tripsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.trips.get().tripsForDevice( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.trips.get().tripsForVehicle( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<Trip, Trip.Wrapper> tripWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Trip, Trip.Wrapper>() {
      @NonNull
      @Override
      public Observable<Trip.Wrapper> call(@NonNull WrapperBuilder<Trip, Trip.Wrapper> b) {
        if (b.link != null) return client.trips.get().tripForUrl(b.link);
        if (b.id != null) return client.trips.get().trip(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<User, User.Wrapper> userWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<User, User.Wrapper>() {
      @NonNull
      @Override
      public Observable<User.Wrapper> call(@NonNull WrapperBuilder<User, User.Wrapper> b) {
        if (b.link != null) return client.users.get().userForUrl(b.link);
        return client.users.get().currentUser();
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public TimeSeriesBuilder<ReportCard, ReportCard.TimeSeries> reportCardsTimeSeriesBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new TimeSeriesBuilder<>(builder, new TimeSeriesObservableFactory< //
        ReportCard, ReportCard.TimeSeries>() {
      @NonNull
      @Override
      public Observable<ReportCard.TimeSeries> call(
          @NonNull TimeSeriesBuilder<ReportCard, ReportCard.TimeSeries> b) {
        if (b.link != null) return client.reportCards.get().reportCardsForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.reportCards.get().reportCardsForDevice( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.reportCards.get().reportCardsForVehicle( //
              b.forIdVals.target, b.since, b.until, b.limit, b.sortDir);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<ReportCard, ReportCard.Wrapper> reportCardWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<ReportCard, ReportCard.Wrapper>() {
      @NonNull
      @Override
      public Observable<ReportCard.Wrapper> call(
          @NonNull WrapperBuilder<ReportCard, ReportCard.Wrapper> b) {
        if (b.link != null) return client.reportCards.get().reportCardForUrl(b.link);
        if (b.id != null) return client.reportCards.get().reportCard(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<ReportCard, ReportCard.Wrapper> reportCardForTripWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<ReportCard, ReportCard.Wrapper>() {
      @NonNull
      @Override
      public Observable<ReportCard.Wrapper> call(
          @NonNull WrapperBuilder<ReportCard, ReportCard.Wrapper> b) {
        if (b.link != null) return client.reportCards.get().reportCardForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == TRIP) {
          return client.reportCards.get().reportCardForTrip(b.forIdVals.target);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(TRIP));
  }

  public WrapperBuilder<OverallReportCard, OverallReportCard> overallReportCardWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client, //
      @Nullable final Long since, @Nullable final Long until) {
    return new WrapperBuilder<>( //
        builder, new WrapperObservableFactory<OverallReportCard, OverallReportCard>() {
      @NonNull
      @Override
      public Observable<OverallReportCard> call(
          @NonNull WrapperBuilder<OverallReportCard, OverallReportCard> b) {
        if (b.link != null) return client.reportCards.get().overallReportCardForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.reportCards.get().overallReportCardForDevice( //
              b.forIdVals.target, since, until);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(DEVICE));
  }

  public PageBuilder<Rule, Rule.Page> rulesPageBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new PageBuilder<>(builder, new PageObservableFactory< //
        Rule, Rule.Page>() {
      @NonNull
      @Override
      public Observable<Rule.Page> call(@NonNull PageBuilder<Rule, Rule.Page> b) {
        if (b.link != null) return client.rules.get().rulesForUrl(b.link);
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.rules.get().rulesForDevice( //
              b.forIdVals.target, b.limit, b.offset);
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.rules.get().rulesForVehicle( //
              b.forIdVals.target, b.limit, b.offset);
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public WrapperBuilder<Rule, Rule.Wrapper> ruleWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Rule, Rule.Wrapper>() {
      @NonNull
      @Override
      public Observable<Rule.Wrapper> call(@NonNull WrapperBuilder<Rule, Rule.Wrapper> b) {
        if (b.link != null) return client.rules.get().ruleForUrl(b.link);
        if (b.id != null) return client.rules.get().rule(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public WrapperBuilder<Rule, Rule.Wrapper> ruleCreateWrapperBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @NonNull final RuleSeed seed) {
    return new WrapperBuilder<>(builder, new WrapperObservableFactory<Rule, Rule.Wrapper>() {
      @NonNull
      @Override
      public Observable<Rule.Wrapper> call(@NonNull WrapperBuilder<Rule, Rule.Wrapper> b) {
        try {
          seed.validate();
        } catch (RuntimeException rte) {
          return Observable.error(rte);
        }
        if (b.forIdVals != null && b.forIdVals.forId == DEVICE) {
          return client.rules.get().createRuleForDevice( //
              b.forIdVals.target, ruleSeedWrapper(seed));
        }
        if (b.forIdVals != null && b.forIdVals.forId == VEHICLE) {
          return client.rules.get().createRuleForVehicle( //
              b.forIdVals.target, ruleSeedWrapper(seed));
        }
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.of(VEHICLE, DEVICE));
  }

  public ItemBuilder<Void> ruleDeleteItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<Void>() {
      @NonNull
      @Override
      public Observable<Void> call(@NonNull ItemBuilder<Void> b) {
        if (b.id != null) return client.rules.get().deleteRule(b.id);
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  // internal requests

  public ItemBuilder<ResponseBody> createUserItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client,
      @NonNull final String wrappedUserJson) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<ResponseBody>() {
      @NonNull
      @Override
      public Observable<ResponseBody> call(@NonNull ItemBuilder<ResponseBody> builder) {
        return client.genericAuth.get().genericPost("users", //
            RequestBody.create(MediaType.parse("application/json"), wrappedUserJson));
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public ItemBuilder<ResponseBody> getAllTermsItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<ResponseBody>() {
      @NonNull
      @Override
      public Observable<ResponseBody> call(@NonNull ItemBuilder<ResponseBody> builder) {
        return client.genericTos.get().genericGet("terms");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  public ItemBuilder<ResponseBody> getTermsHtmlItemBuilder( //
      @NonNull Builder builder, @NonNull final ClientAndServices client) {
    return new ItemBuilder<>(builder, new ItemObservableFactory<ResponseBody>() {
      @NonNull
      @Override
      public Observable<ResponseBody> call(@NonNull ItemBuilder<ResponseBody> b) {
        if (b.id != null) return client.genericTos.get().genericGetHtml(format("terms/%s", b.id));
        throw new RuntimeException("validations failed: this should never happen!");
      }
    }, EnumSet.noneOf(ForId.class));
  }

  // observable factory interfaces

  interface TimeSeriesObservableFactory<T extends StrictModel, MT extends ModelTimeSeries<T>> {

    @NonNull
    Observable<MT> call(@NonNull TimeSeriesBuilder<T, MT> builder);
  }

  interface PageObservableFactory<T extends StrictModel, P extends ModelPage<T>> {

    @NonNull
    Observable<P> call(@NonNull PageBuilder<T, P> builder);
  }

  interface WrapperObservableFactory<T extends StrictModel, MW extends ModelWrapper<T>> {

    @NonNull
    Observable<MW> call(@NonNull WrapperBuilder<T, MW> builder);
  }

  interface ItemObservableFactory<T> {

    @NonNull
    Observable<T> call(@NonNull ItemBuilder<T> builder);
  }
}
