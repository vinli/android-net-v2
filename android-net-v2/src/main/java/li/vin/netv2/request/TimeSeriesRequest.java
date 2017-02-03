package li.vin.netv2.request;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import li.vin.netv2.error.NoResourceExistsException;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.util.VinliRx;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

import static li.vin.netv2.model.SortDir.ASCENDING;
import static li.vin.netv2.model.SortDir.DESCENDING;

public final class TimeSeriesRequest<T extends StrictModel, TS extends ModelTimeSeries<T>> {

  final TimeSeriesBuilder<T, TS> builder;

  TimeSeriesRequest(TimeSeriesBuilder<T, TS> builder) {
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

  Observable<TS> observeInternal() {
    return builder.validateAndMakeObservable();
  }

  Observable<TS> observeAllInternal() {
    return observeInternal().flatMap(new Func1<TS, Observable<TS>>() {
      @Override
      public Observable<TS> call(TS ts) {
        if (ts.sortDir() == DESCENDING) {
          if (ts.priorTimeLink().isNoLink()) return Observable.just(ts);
          return Observable.just(ts).concatWith( //
              builder.copy(false).link(ts.priorTimeLink().raw()).build().observeAllInternal());
        }
        if (ts.sortDir() == ASCENDING) {
          if (ts.nextTimeLink().isNoLink()) return Observable.just(ts);
          return Observable.just(ts).concatWith( //
              builder.copy(false).link(ts.nextTimeLink().raw()).build().observeAllInternal());
        }
        return Observable.error(new RuntimeException("unknown sortDir - should never happen."));
      }
    });
  }

  Observable<List<T>> observeExtractedInternal() {
    return observeInternal().map(VinliRx.<T>extractTimeSeries());
  }

  Observable<List<T>> observeAllExtractedInternal() {
    return observeAllInternal().map(VinliRx.<T>extractTimeSeries());
  }

  Observable<T> observeFlattenedInternal() {
    return observeExtractedInternal().flatMap(VinliRx.<T>flatten());
  }

  Observable<T> observeAllFlattenedInternal() {
    return observeAllExtractedInternal().flatMap(VinliRx.<T>flatten());
  }

  public Observable<TS> observe() {
    return finish(observeInternal());
  }

  public Observable<TS> observeAll() {
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

  public TimeSeriesBuilder<T, TS> timeSeriesBuilder() {
    return builder.copy();
  }
}
