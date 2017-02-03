package li.vin.netv2.request;

import android.support.annotation.NonNull;
import li.vin.netv2.model.BatteryStatus;
import li.vin.netv2.model.Collision;
import li.vin.netv2.model.Device;
import li.vin.netv2.model.Distance;
import li.vin.netv2.model.Dtc;
import li.vin.netv2.model.DtcDiagnosis;
import li.vin.netv2.model.Event;
import li.vin.netv2.model.Link;
import li.vin.netv2.model.Location;
import li.vin.netv2.model.Message;
import li.vin.netv2.model.Notification;
import li.vin.netv2.model.OverallReportCard;
import li.vin.netv2.model.ReportCard;
import li.vin.netv2.model.Rule;
import li.vin.netv2.model.Trip;
import li.vin.netv2.model.User;
import li.vin.netv2.model.Vehicle;

/**
 * Used to follow {@link Link}s produced by models. Inherits all properties from its parent {@link
 * VinliRequest.Builder}.
 */
public final class Linker {

  @NonNull final VinliRequest.Builder builder;

  Linker(@NonNull VinliRequest.Builder builder) {
    this.builder = builder.copy();
  }

  @NonNull
  public PageRequest<Device, Device.Page> devices(@NonNull Link<Device.Page> link) {
    return new PageRequest<>( //
        RequestFactories.inst.get()
            .devicesPageBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Device, Device.Wrapper> device(@NonNull Link<Device.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .deviceWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public PageRequest<Vehicle, Vehicle.Page> vehicles(@NonNull Link<Vehicle.Page> link) {
    return new PageRequest<>( //
        RequestFactories.inst.get()
            .vehiclesPageBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Vehicle, Vehicle.Wrapper> vehicle(@NonNull Link<Vehicle.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .vehicleWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Collision, Collision.TimeSeries> collisions(
      @NonNull Link<Collision.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .collisionsTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Collision, Collision.Wrapper> collision(
      @NonNull Link<Collision.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .collisionWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<BatteryStatus, BatteryStatus.Wrapper> batteryStatus(
      @NonNull Link<BatteryStatus.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .batteryStatusWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Distance, Distance.Wrapper> distance( //
      @NonNull Link<Distance.Wrapper> link, @NonNull Distance.Unit unit) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .distanceWrapperBuilder(builder, builder.validateAndGetClient(), unit)
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Dtc, Dtc.TimeSeries> dtcs(@NonNull Link<Dtc.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .dtcsTimeSeriesBuilder(builder, builder.validateAndGetClient(), null)
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<DtcDiagnosis, DtcDiagnosis.Wrapper> dtcDiagnosis(
      @NonNull Link<DtcDiagnosis.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .diagDtcByIdBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Event, Event.TimeSeries> events(@NonNull Link<Event.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .eventsTimeSeriesBuilder(builder, builder.validateAndGetClient(), null, null)
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Event, Event.Wrapper> event(@NonNull Link<Event.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .eventWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Location, Location.TimeSeries> locations(
      @NonNull Link<Location.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .locationsTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Message, Message.Wrapper> location(@NonNull Link<Message.Wrapper> link) {
    return message(link);
  }

  @NonNull
  public TimeSeriesRequest<Message, Message.TimeSeries> messages(
      @NonNull Link<Message.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .messagesTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Message, Message.Wrapper> message(@NonNull Link<Message.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .messageWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Notification, Notification.TimeSeries> notifications(
      @NonNull Link<Notification.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .notificationsTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Notification, Notification.Wrapper> notification(
      @NonNull Link<Notification.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .notificationWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<Trip, Trip.TimeSeries> trips(@NonNull Link<Trip.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .tripsTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Trip, Trip.Wrapper> trip(@NonNull Link<Trip.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .tripWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<User, User.Wrapper> user(@NonNull Link<User.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .userWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public TimeSeriesRequest<ReportCard, ReportCard.TimeSeries> reportCards(
      @NonNull Link<ReportCard.TimeSeries> link) {
    return new TimeSeriesRequest<>( //
        RequestFactories.inst.get()
            .reportCardsTimeSeriesBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<ReportCard, ReportCard.Wrapper> reportCard(
      @NonNull Link<ReportCard.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .reportCardWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<OverallReportCard, OverallReportCard> overallReportCard(
      @NonNull Link<OverallReportCard> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .overallReportCardWrapperBuilder(builder, builder.validateAndGetClient(), null, null)
            .link(link.raw()) //
    );
  }

  @NonNull
  public PageRequest<Rule, Rule.Page> rules(@NonNull Link<Rule.Page> link) {
    return new PageRequest<>( //
        RequestFactories.inst.get()
            .rulesPageBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }

  @NonNull
  public WrapperRequest<Rule, Rule.Wrapper> rule(@NonNull Link<Rule.Wrapper> link) {
    return new WrapperRequest<>( //
        RequestFactories.inst.get()
            .ruleWrapperBuilder(builder, builder.validateAndGetClient())
            .link(link.raw()) //
    );
  }
}
