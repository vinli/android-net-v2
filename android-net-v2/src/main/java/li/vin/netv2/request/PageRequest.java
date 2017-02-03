package li.vin.netv2.request;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import li.vin.netv2.error.NoResourceExistsException;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.util.VinliRx;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public final class PageRequest<T extends StrictModel, P extends ModelPage<T>> {

  final PageBuilder<T, P> builder;

  PageRequest(PageBuilder<T, P> builder) {
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

  Observable<P> observeInternal() {
    return builder.validateAndMakeObservable();
  }

  Observable<P> observeAllInternal() {
    return observeInternal().flatMap(new Func1<P, Observable<P>>() {
      @Override
      public Observable<P> call(P p) {
        if (p.nextPageLink().isNoLink()) return Observable.just(p);
        return Observable.just(p).concatWith( //
            builder.copy(false).link(p.nextPageLink().raw()).build().observeAllInternal());
      }
    });
  }

  Observable<List<T>> observeExtractedInternal() {
    return observeInternal().map(VinliRx.<T>extractPage());
  }

  Observable<List<T>> observeAllExtractedInternal() {
    return observeAllInternal().map(VinliRx.<T>extractPage());
  }

  Observable<T> observeFlattenedInternal() {
    return observeExtractedInternal().flatMap(VinliRx.<T>flatten());
  }

  Observable<T> observeAllFlattenedInternal() {
    return observeAllExtractedInternal().flatMap(VinliRx.<T>flatten());
  }

  public Observable<P> observe() {
    return finish(observeInternal());
  }

  public Observable<P> observeAll() {
    return finish(observeAllInternal());
  }

  public Observable<List<T>> observeExtracted() {
    return finish(observeExtractedInternal());
  }

  public Observable<List<T>> observeAllExtracted() {
    return finish(observeAllExtractedInternal());
  }

  public Observable<T> observeFlattened() {
    return finish(observeFlattenedInternal());
  }

  public Observable<T> observeAllFlattened() {
    return finish(observeAllFlattenedInternal());
  }

  public PageBuilder<T, P> pageBuilder() {
    return builder.copy();
  }
}
