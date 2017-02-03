package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.EnumSet;
import li.vin.netv2.util.DeepCopyable;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.StrictModel;
import rx.Observable;

import static java.lang.String.format;
import static li.vin.netv2.model.Link.NO_LINK;

// TODO - volatile fields and yet not threadsafe; validate is called while fields are mutating???
public final class PageBuilder<T extends StrictModel, P extends ModelPage<T>>
    implements DeepCopyable<PageBuilder<T, P>> {

  @NonNull final VinliRequest.Builder builder;
  @NonNull final RequestFactories.PageObservableFactory<T, P> observableFactory;
  @NonNull final EnumSet<ForId> allowedIds;

  @Nullable String link;
  @Nullable Integer limit;
  @Nullable Integer offset;
  @Nullable ForIdVals forIdVals;

  PageBuilder(@NonNull VinliRequest.Builder builder,
      @NonNull RequestFactories.PageObservableFactory<T, P> observableFactory,
      @NonNull EnumSet<ForId> allowedIds) {
    this.builder = builder.copy();
    this.observableFactory = observableFactory;
    this.allowedIds = EnumSet.copyOf(allowedIds);
  }

  @NonNull
  @Override
  public PageBuilder<T, P> copy() {
    return copy(true);
  }

  PageBuilder<T, P> copy(boolean preserveParams) {
    PageBuilder<T, P> b = new PageBuilder<>(builder, observableFactory, allowedIds);
    if (preserveParams) {
      b.link = link;
      b.limit = limit;
      b.offset = offset;
      b.forIdVals = forIdVals;
    }
    return b;
  }

  PageBuilder<T, P> link(@NonNull String link) {
    this.link = link;
    return this;
  }

  public PageBuilder<T, P> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public PageBuilder<T, P> offset(int offset) {
    this.offset = offset;
    return this;
  }

  Observable<P> validateAndMakeObservable() {
    if (limit != null || offset != null) {
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

  public PageRequest<T, P> build() {
    return new PageRequest<>(this);
  }

  public VinliRequest.Builder baseBuilder() {
    return builder.copy();
  }
}
