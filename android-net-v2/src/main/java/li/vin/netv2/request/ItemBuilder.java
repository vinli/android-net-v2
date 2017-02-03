package li.vin.netv2.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.EnumSet;
import li.vin.netv2.util.DeepCopyable;
import rx.Observable;

import static java.lang.String.format;
import static li.vin.netv2.model.Link.NO_LINK;

public final class ItemBuilder<T> implements DeepCopyable<ItemBuilder<T>> {

  @NonNull final VinliRequest.Builder builder;
  @NonNull final RequestFactories.ItemObservableFactory<T> observableFactory;
  @NonNull final EnumSet<ForId> allowedIds;

  @Nullable String link;
  @Nullable String id;
  @Nullable ForIdVals forIdVals;

  ItemBuilder(@NonNull VinliRequest.Builder builder,
      @NonNull RequestFactories.ItemObservableFactory<T> observableFactory,
      @NonNull EnumSet<ForId> allowedIds) {
    this.builder = builder.copy();
    this.observableFactory = observableFactory;
    this.allowedIds = EnumSet.copyOf(allowedIds);
  }

  @NonNull
  @Override
  public ItemBuilder<T> copy() {
    return copy(true);
  }

  ItemBuilder<T> copy(boolean preserveParams) {
    ItemBuilder<T> b = new ItemBuilder<>(builder, observableFactory, allowedIds);
    if (preserveParams) {
      b.link = link;
      b.id = id;
      b.forIdVals = forIdVals;
    }
    return b;
  }

  ItemBuilder<T> link(@NonNull String link) {
    this.link = link;
    return this;
  }

  ItemBuilder<T> id(@NonNull String id) {
    this.id = id;
    return this;
  }

  Observable<T> validateAndMakeObservable() {
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

  public ItemRequest<T> build() {
    return new ItemRequest<>(this);
  }

  public VinliRequest.Builder baseBuilder() {
    return builder.copy();
  }
}
