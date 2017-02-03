package li.vin.netv2.request;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import li.vin.netv2.error.NoResourceExistsException;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public final class ItemRequest<T> {

  final ItemBuilder<T> builder;

  ItemRequest(ItemBuilder<T> builder) {
    this.builder = builder.copy();
  }

  <X> Observable<X> finish(Observable<X> obs) {
    if (builder.builder.missingResourcesAsNull) {
      obs = obs.onErrorResumeNext(new Func1<Throwable, Observable<? extends X>>() {
        @Override
        public Observable<? extends X> call(Throwable throwable) {
          if (throwable instanceof NoResourceExistsException) {
            return Observable.just(null);
          }
          return Observable.error(throwable);
        }
      });
    }
    if (builder.builder.retryPolicy != null) {
      obs = obs.retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
        @Override
        public Observable<?> call(final Observable<? extends Throwable> attempts) {
          return Observable.using(new Func0<AtomicInteger>() {
            @Override
            public AtomicInteger call() {
              return new AtomicInteger();
            }
          }, new Func1<AtomicInteger, Observable<?>>() {
            @Override
            public Observable<?> call(final AtomicInteger ctr) {
              return attempts.flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                  //noinspection ConstantConditions
                  long del = builder.builder.retryPolicy.delay(throwable, ctr.incrementAndGet());
                  if (del < 0L) return Observable.error(throwable);
                  return Observable.timer(del, TimeUnit.MILLISECONDS);
                }
              });
            }
          }, new Action1<AtomicInteger>() {
            @Override
            public void call(AtomicInteger ai) {
            }
          });
        }
      });
    }
    obs = obs.timeout(builder.builder.overallTimeoutAmount, builder.builder.overallTimeoutUnit);
    return obs;
  }

  Observable<T> observeInternal() {
    return builder.validateAndMakeObservable();
  }

  public Observable<T> observe() {
    return finish(observeInternal());
  }

  public ItemBuilder<T> itemBuilder() {
    return builder.copy();
  }
}
