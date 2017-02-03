package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.EnumSet;
import li.vin.netv2.util.DeepCopyable;
import li.vin.netv2.model.SortDir;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.StrictModel;
import rx.Observable;

import static java.lang.String.format;
import static li.vin.netv2.model.Link.NO_LINK;
import static li.vin.netv2.model.SortDir.ASCENDING;
import static li.vin.netv2.model.SortDir.DESCENDING;

// TODO - volatile fields and yet not threadsafe; validate is called while fields are mutating???
public final class TimeSeriesBuilder<T extends StrictModel, MT extends ModelTimeSeries<T>>
    implements DeepCopyable<TimeSeriesBuilder<T, MT>> {

  @NonNull final VinliRequest.Builder builder;
  @NonNull final RequestFactories.TimeSeriesObservableFactory<T, MT> observableFactory;
  @NonNull final EnumSet<ForId> allowedIds;

  @Nullable String link;
  @Nullable Long since;
  @Nullable Long until;
  @Nullable Integer limit;
  @Nullable String sortDir;
  @Nullable ForIdVals forIdVals;

  TimeSeriesBuilder(@NonNull VinliRequest.Builder builder,
      @NonNull RequestFactories.TimeSeriesObservableFactory<T, MT> observableFactory,
      @NonNull EnumSet<ForId> allowedIds) {
    this.builder = builder.copy();
    this.observableFactory = observableFactory;
    this.allowedIds = EnumSet.copyOf(allowedIds);
  }

  @NonNull
  @Override
  public TimeSeriesBuilder<T, MT> copy() {
    return copy(true);
  }

  TimeSeriesBuilder<T, MT> copy(boolean preserveParams) {
    TimeSeriesBuilder<T, MT> b = new TimeSeriesBuilder<>(builder, observableFactory, allowedIds);
    if (preserveParams) {
      b.link = link;
      b.since = since;
      b.until = until;
      b.limit = limit;
      b.sortDir = sortDir;
      b.forIdVals = forIdVals;
    }
    return b;
  }

  TimeSeriesBuilder<T, MT> link(@NonNull String link) {
    this.link = link;
    return this;
  }

  public TimeSeriesBuilder<T, MT> since(long since) {
    this.since = since;
    return this;
  }

  public TimeSeriesBuilder<T, MT> until(long until) {
    this.until = until;
    return this;
  }

  public TimeSeriesBuilder<T, MT> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public TimeSeriesBuilder<T, MT> sortDir(@NonNull SortDir sortDir) {
    this.sortDir = sortDir.toString();
    return this;
  }

  public TimeSeriesBuilder<T, MT> latest() {
    this.limit = 1;
    this.sortDir = DESCENDING.toString();
    return this;
  }

  public TimeSeriesBuilder<T, MT> earliest() {
    this.limit = 1;
    this.sortDir = ASCENDING.toString();
    return this;
  }

  Observable<MT> validateAndMakeObservable() {
    if (limit != null || until != null || since != null || sortDir != null) {
      if (link != null) {
        return Observable.error(new RuntimeException("pagination, link mutally exclusive."));
      }
    }
    if (link != null) {
      if (forIdVals != null) {
        return Observable.error(new RuntimeException("forId, link mutally exclusive."));
      }
      if (NO_LINK.equals(link)) {
        return Observable.error(new RuntimeException("no link exists to follow."));
      }
    }
    if (forIdVals != null) {
      if (link != null) {
        return Observable.error(new RuntimeException("forId, link mutally exclusive."));
      }
      forIdVals.validate();
    }
    if (!allowedIds.isEmpty() && forIdVals == null && link == null) {
      return Observable.error(
          new RuntimeException(format("forId one of %s required.", allowedIds.toString())));
    }
    return observableFactory.call(this);
  }

  public TimeSeriesRequest<T, MT> build() {
    return new TimeSeriesRequest<>(this);
  }

  public VinliRequest.Builder baseBuilder() {
    return builder.copy();
  }
}
