package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.EnumSet;
import li.vin.netv2.model.contract.ModelWrapper;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.util.DeepCopyable;
import rx.Observable;

import static java.lang.String.format;
import static li.vin.netv2.model.Link.NO_LINK;

// TODO - volatile fields and yet not threadsafe; validate is called while fields are mutating???
public final class WrapperBuilder<T extends StrictModel, MW extends ModelWrapper<T>>
    implements DeepCopyable<WrapperBuilder<T, MW>> {

  @NonNull final VinliRequest.Builder builder;
  @NonNull final RequestFactories.WrapperObservableFactory<T, MW> observableFactory;
  @NonNull final EnumSet<ForId> allowedIds;

  @Nullable String link;
  @Nullable String id;
  @Nullable ForIdVals forIdVals;

  WrapperBuilder(@NonNull VinliRequest.Builder builder,
      @NonNull RequestFactories.WrapperObservableFactory<T, MW> observableFactory,
      @NonNull EnumSet<ForId> allowedIds) {
    this.builder = builder.copy();
    this.observableFactory = observableFactory;
    this.allowedIds = EnumSet.copyOf(allowedIds);
  }

  @NonNull
  @Override
  public WrapperBuilder<T, MW> copy() {
    return copy(true);
  }

  WrapperBuilder<T, MW> copy(boolean preserveParams) {
    WrapperBuilder<T, MW> b = new WrapperBuilder<>(builder, observableFactory, allowedIds);
    if (preserveParams) {
      b.link = link;
      b.id = id;
      b.forIdVals = forIdVals;
    }
    return b;
  }

  WrapperBuilder<T, MW> link(@NonNull String link) {
    this.link = link;
    return this;
  }

  WrapperBuilder<T, MW> id(@NonNull String id) {
    this.id = id;
    return this;
  }

  Observable<MW> validateAndMakeObservable() {
    if (id != null) {
      if (link != null || forIdVals != null) {
        return Observable.error(new RuntimeException("id, forId, link mutally exclusive."));
      }
    }
    if (link != null) {
      if (forIdVals != null) {
        return Observable.error(new RuntimeException("id, forId, link mutally exclusive."));
      }
      if (NO_LINK.equals(link)) {
        return Observable.error(new RuntimeException("no link exists to follow."));
      }
    }
    if (forIdVals != null) {
      if (link != null) {
        return Observable.error(new RuntimeException("id, forId, link mutally exclusive."));
      }
      forIdVals.validate();
    }
    if (!allowedIds.isEmpty() && forIdVals == null && link == null) {
      return Observable.error(
          new RuntimeException(format("forId one of %s required.", allowedIds.toString())));
    }
    return observableFactory.call(this);
  }

  public WrapperRequest<T, MW> build() {
    return new WrapperRequest<>(this);
  }

  public VinliRequest.Builder baseBuilder() {
    return builder.copy();
  }
}
