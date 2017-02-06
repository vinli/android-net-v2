package li.vin.netv2.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import li.vin.netv2.BuildConfig;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.ModelWrapper;
import li.vin.netv2.model.contract.StrictModel;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.Buffer;
import okio.Sink;
import okio.Source;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Action3;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static android.text.TextUtils.getTrimmedLength;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static java.util.Collections.sort;
import static java.util.Collections.synchronizedList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static li.vin.netv2.BuildConfig.DISK_CACHE_FILENAME_FORMAT;
import static li.vin.netv2.BuildConfig.MAX_DISK_CACHE_LOCK_MAP_SIZE;
import static li.vin.netv2.BuildConfig.MAX_DISK_CACHE_PRUNES_PER_PASS;
import static li.vin.netv2.util.VinliRx.AgeSince.SINCE_ACCESSED;
import static li.vin.netv2.util.VinliRx.AgeSince.SINCE_CREATED;
import static rx.Observable.concat;

/**
 * Collection of RxJava utilities that make interaction with the Vinli SDK easier. Typically for
 * use with {@link Observable#map(Func1)}, {@link Observable#flatMap(Func1)}, or {@link
 * Observable#compose(Observable.Transformer)}.
 */
public final class VinliRx {

  private VinliRx() {
  }

  public static <T extends StrictModel> Func1<ModelPage<T>, List<T>> extractPage() {
    return extractPage(null);
  }

  public static <T extends StrictModel> Func1<ModelPage<T>, List<T>> extractPage(Class<T> cls) {
    return new Func1<ModelPage<T>, List<T>>() {
      @Override
      public List<T> call(ModelPage<T> page) {
        return page.extract();
      }
    };
  }

  public static <T extends StrictModel> Func1<ModelPage<T>, Observable<T>> flattenPage() {
    return flattenPage(null);
  }

  public static <T extends StrictModel> Func1<ModelPage<T>, Observable<T>> flattenPage(
      Class<T> cls) {
    return new Func1<ModelPage<T>, Observable<T>>() {
      @Override
      public Observable<T> call(ModelPage<T> page) {
        return Observable.from(page.extract());
      }
    };
  }

  public static <T extends StrictModel> Func1<ModelTimeSeries<T>, List<T>> extractTimeSeries() {
    return extractTimeSeries(null);
  }

  public static <T extends StrictModel> Func1<ModelTimeSeries<T>, List<T>> extractTimeSeries(
      Class<T> cls) {
    return new Func1<ModelTimeSeries<T>, List<T>>() {
      @Override
      public List<T> call(ModelTimeSeries<T> ts) {
        return ts.extract();
      }
    };
  }

  public static <T extends StrictModel> Func1<ModelTimeSeries<T>, Observable<T>> flattenTimeSeries() {
    return flattenTimeSeries(null);
  }

  public static <T extends StrictModel> Func1<ModelTimeSeries<T>, Observable<T>> flattenTimeSeries(
      Class<T> cls) {
    return new Func1<ModelTimeSeries<T>, Observable<T>>() {
      @Override
      public Observable<T> call(ModelTimeSeries<T> ts) {
        return Observable.from(ts.extract());
      }
    };
  }

  public static <T extends StrictModel> Func1<ModelWrapper<T>, T> extractWrapper() {
    return extractWrapper(null);
  }

  public static <T extends StrictModel> Func1<ModelWrapper<T>, T> extractWrapper(Class<T> cls) {
    return new Func1<ModelWrapper<T>, T>() {
      @Override
      public T call(ModelWrapper<T> w) {
        return w.extract();
      }
    };
  }

  public static <T> Func1<? super Collection<T>, Observable<T>> flatten() {
    return flatten(null);
  }

  public static <T> Func1<? super Collection<T>, Observable<T>> flatten(Class<T> cls) {
    return new Func1<Collection<T>, Observable<T>>() {
      @Override
      public Observable<T> call(Collection<T> l) {
        return Observable.from(l);
      }
    };
  }

  public static <T> Func1<T, Boolean> nonNull() {
    return nonNull(null);
  }

  public static <T> Func1<T, Boolean> nonNull(Class<T> cls) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return t != null;
      }
    };
  }

  public static <T> Func1<T, Boolean> isNull() {
    return isNull(null);
  }

  public static <T> Func1<T, Boolean> isNull(Class<T> cls) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return t == null;
      }
    };
  }

  public static <T> Action1<T> pushInto(Observer<T> subj) {
    return pushInto(subj, null);
  }

  public static <T> Action1<T> pushInto(final Observer<T> subj, Class<T> cls) {
    return new Action1<T>() {
      @Override
      public void call(T t) {
        subj.onNext(t);
      }
    };
  }

  /**
   * Use with {@link Observable#compose(Observable.Transformer)} to delay each emission from a
   * source by a a given amount. Delays consecutively rather than concurrently, i.e., a lossless
   * throttle.
   **/
  public static <T> Observable.Transformer<T, T> delayEach(final int delay, final TimeUnit unit) {
    return new Observable.Transformer<T, T>() {
      @Override
      public Observable<T> call(Observable<T> o) {
        return o.concatMap(new Func1<T, Observable<? extends T>>() {
          @Override
          public Observable<? extends T> call(T t) {
            return Observable.just(t).delay(delay, unit);
          }
        });
      }
    };
  }

  /**
   * Use with {@link Observable#compose(Observable.Transformer)} to collect every item emitted by
   * the source into an {@link ArrayList}.
   *
   * @see #collectAllDistinct()
   **/
  public static <T> Observable.Transformer<T, ? extends Collection<T>> collectAll() {
    return new Observable.Transformer<T, List<T>>() {
      @Override
      public Observable<List<T>> call(Observable<T> o) {
        return o.collect(new Func0<List<T>>() {
          @Override
          public List<T> call() {
            return new ArrayList<>();
          }
        }, new Action2<List<T>, T>() {
          @Override
          public void call(List<T> l, T t) {
            l.add(t);
          }
        });
      }
    };
  }

  /**
   * Same as {@link #collectAll()}, but uses a {@link LinkedHashSet} so identical items aren't
   * allowed.
   *
   * @see #collectAll()
   **/
  public static <T> Observable.Transformer<T, ? extends Collection<T>> collectAllDistinct() {
    return new Observable.Transformer<T, Set<T>>() {
      @Override
      public Observable<Set<T>> call(Observable<T> o) {
        return o.collect(new Func0<Set<T>>() {
          @Override
          public Set<T> call() {
            return new LinkedHashSet<>();
          }
        }, new Action2<Set<T>, T>() {
          @Override
          public void call(Set<T> l, T t) {
            l.add(t);
          }
        });
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) && p2.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) && p2.call(t) && p3.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) && p2.call(t) && p3.call(t) && p4.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4, final Func1<T, Boolean> p5) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) && p2.call(t) && p3.call(t) && p4.call(t) && p5.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4, final Func1<T, Boolean> p5, final Func1<T, Boolean> p6) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) && p2.call(t) && p3.call(t) && p4.call(t) && p5.call(t) && p6.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> and( //
      final Func1<T, Boolean>[] preds) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        for (Func1<T, Boolean> p : preds) {
          if (!p.call(t)) return false;
        }
        return true;
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) || p2.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) || p2.call(t) || p3.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) || p2.call(t) || p3.call(t) || p4.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4, final Func1<T, Boolean> p5) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) || p2.call(t) || p3.call(t) || p4.call(t) || p5.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean> p1, final Func1<T, Boolean> p2, final Func1<T, Boolean> p3,
      final Func1<T, Boolean> p4, final Func1<T, Boolean> p5, final Func1<T, Boolean> p6) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        return p1.call(t) || p2.call(t) || p3.call(t) || p4.call(t) || p5.call(t) || p6.call(t);
      }
    };
  }

  public static <T> Func1<T, Boolean> or( //
      final Func1<T, Boolean>[] preds) {
    return new Func1<T, Boolean>() {
      @Override
      public Boolean call(T t) {
        for (Func1<T, Boolean> p : preds) {
          if (p.call(t)) return true;
        }
        return false;
      }
    };
  }

  // ---------

  /** Defines age criteria for {@link RxCache#get(String, Type, int, TimeUnit, AgeSince)}. */
  public enum AgeSince {
    /** Get items from cache matching an age since the cache item was created. */
    SINCE_CREATED,
    /** Get items from cache matching an age since the cache item was accessed. */
    SINCE_ACCESSED
  }

  /** Rx-ified cache. */
  public interface RxCache {

    /** A type for this cache, can be used for equality, matching, logging, etc. */
    @NonNull
    String type();

    /**
     * Synchronously put an item into this cache. If the item is of a generic type, the type param
     * provided MUST capture generic type information - see {@link TypeToken} for details. In
     * general, implementations should log and ignore errors rather than throw unchecked
     * exceptions.
     */
    void put(@NonNull String k, @Nullable Object v, @NonNull Type t);

    /**
     * Synchronously get an item from this cache. Null returned in case no item is found matching
     * the given age criteria. If the item is of a generic type, the type param provided MUST
     * capture generic type information - see {@link TypeToken} for details. In general,
     * implementations should log and ignore errors - returning null - rather than throw unchecked
     * exceptions.
     */
    @Nullable
    Object get(@NonNull String k, @NonNull Type t, //
        int maxAge, TimeUnit maxAgeUnit, @NonNull AgeSince ageSince);

    /**
     * Optionally provide a scheduler on which all {@link #get(String, Type, int, TimeUnit,
     * AgeSince)} operations for this cache should take place. If null is returned, operations
     * should be designed to run safely on any number of threads concurrently.
     */
    @Nullable
    Scheduler readScheduler();

    /**
     * Optionally provide a scheduler on which all {@link #put(String, Object, Type)} operations
     * for this cache should take place. If null is returned, operations should be designed to run
     * safely on any number of threads concurrently.
     */
    @Nullable
    Scheduler writeScheduler();
  }

  /**
   * Observe getting an item matching the given age critera from the given caches, checked in order
   * until a result is found. If the caches contain no result, an empty observable results.
   */
  public static Observable<Object> observeGetFromCache( //
      @NonNull final String k, @NonNull final Type t, //
      final int maxAge, final TimeUnit maxAgeUnit, @NonNull final AgeSince ageSince, //
      final boolean putIntoLowerCaches, //
      @NonNull final RxCache... caches) {
    List<Observable<Object>> gets = new ArrayList<>();
    int cacheIndex = 0;
    for (final RxCache cache : caches) {
      final int fCacheIndex = cacheIndex;
      Observable<Object> obs = Observable.create(new Observable.OnSubscribe<Object>() {
        @Override
        public void call(final Subscriber<? super Object> s) {
          if (s.isUnsubscribed()) return;
          try {
            final Object val = cache.get(k, t, maxAge, maxAgeUnit, ageSince);
            if (val != null) {
              Log.e("TESTO", "cache hit from from " + cache.type() + " ... "); // FIXME
              if (putIntoLowerCaches) {
                schedulePutIntoCache(k, val, t, copyOf(caches, fCacheIndex));
              }
              s.onNext(val);
            } else {
              Log.e("TESTO", "cache miss from from " + cache.type() + " ... "); // FIXME
            }
            s.onCompleted();
          } catch (Exception e) {
            s.onError(e);
          }
        }
      });
      Scheduler scheduler = cache.readScheduler();
      if (scheduler != null) obs = obs.subscribeOn(scheduler);
      gets.add(obs);
      cacheIndex++;
    }
    return concat(gets).takeFirst(nonNull());
  }

  /**
   * Variant of {@link #schedulePutIntoCache(String, Object, Type, Scheduler, RxCache...)} that
   * provides no default scheduler. If a cache also provides no scheduler preference, in this case,
   * the given item will be synchonously put into the cache.
   */
  public static void schedulePutIntoCache( //
      @NonNull final String k, @Nullable final Object v, @NonNull final Type t, //
      @NonNull final RxCache... caches) {
    schedulePutIntoCache(k, v, t, null, caches);
  }

  /**
   * Schedule an item to be put into all of the given caches according to each cache's desired
   * scheduler. If the cache has no scheduler preference, and a defaultScheduler param is provided,
   * that scheduler will be used. If a cache has no scheduler preference and no defaultScheduler is
   * provided, the item will be synchonously put into that cache.
   */
  public static void schedulePutIntoCache( //
      @NonNull final String k, @Nullable final Object v, @NonNull final Type t, //
      @Nullable final Scheduler defaultScheduler, //
      @NonNull final RxCache... caches) {
    for (final RxCache cache : caches) {
      Scheduler scheduler = cache.writeScheduler();
      if (scheduler == null) scheduler = defaultScheduler;
      if (scheduler == null) {
        Log.e("TESTO", "putting into " + cache.type() + "cache"); // FIXME
        cache.put(k, v, t);
      } else {
        final Scheduler.Worker w = scheduler.createWorker();
        w.schedule(new Action0() {
          @Override
          public void call() {
            try {
              cache.put(k, v, t);
              Log.e("TESTO", "putting into " + cache.type() + "cache"); // FIXME
            } finally {
              w.unsubscribe();
            }
          }
        });
      }
    }
  }

  /**
   * Simple cache file naming func. This simply replaces out any characters that aren't valid in
   * a filename with an underscore (_). Care should be taken when using this approach not to
   * generate accidental filename collisions by choosing keys that are too short and, with invalid
   * characters replaced by underscores, might be identical. Keys greater than 64 chars in length
   * are truncated. Null or empty keys are replaced with randomly generated UUIDs.
   */
  public static Func1<String, String> simpleCacheFileNamer() {
    return new Func1<String, String>() {
      @Override
      public String call(String k) {
        if (k == null || getTrimmedLength(k) == 0) k = UUID.randomUUID().toString();
        if (k.length() > 64) k = k.substring(0, 64);
        return k.replaceAll("\\W+", "_");
      }
    };
  }

  /**
   * Disk cache writer func that uses a given Gson instance to serialize items. Assumes that all
   * items written are Gson-compatible.
   */
  public static Action3<Object, Type, OutputStream> gsonDiskCacheWriter(@NonNull final Gson gson) {
    return new Action3<Object, Type, OutputStream>() {
      @Override
      public void call(Object o, Type t, OutputStream os) {
        try {
          String json = gson.toJson(o, t);
          os.write(json.getBytes("UTF-8"));
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      }
    };
  }

  /**
   * Disk cache reader func that uses a given Gson instance to deserialize items. Assumes that all
   * items read are Gson-compatible.
   */
  public static Func2<Type, InputStream, Object> gsonDiskCacheReader(@NonNull final Gson gson) {
    return new Func2<Type, InputStream, Object>() {
      @Override
      public Object call(Type t, InputStream is) {
        try {
          int length;
          byte[] buffer = new byte[1024];
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          while ((length = is.read(buffer)) != -1) baos.write(buffer, 0, length);
          return gson.fromJson(baos.toString("UTF-8"), t);
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        }
      }
    };
  }

  /**
   * Disk cache writer func that uses Java {@link Serializable} behavior. Assumes that all items
   * written are designed to be safely {@link Serializable}. Boilerplate Java serialization should
   * generally be considered a last resort; use this only for simple built-in types where
   * versioning and backwards-compatibility are not important.
   */
  public static Action3<Object, Type, OutputStream> serializableDiskCacheWriter() {
    return new Action3<Object, Type, OutputStream>() {
      @Override
      public void call(Object o, Type t, OutputStream os) {
        ObjectOutputStream oos = null;
        try {
          (oos = new ObjectOutputStream(new BufferedOutputStream(os))).writeObject(o);
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        } finally {
          if (oos != null) {
            try {
              oos.close();
            } catch (Exception ignored) {
            }
          }
        }
      }
    };
  }

  /**
   * Disk cache writer func that uses Java {@link Serializable} behavior. Assumes that all items
   * written are designed to be safely {@link Serializable}. Boilerplate Java serialization should
   * generally be considered a last resort; use this only for simple built-in types where
   * versioning and backwards-compatibility are not important.
   */
  public static Func2<Type, InputStream, Object> serializableDiskCacheReader() {
    return new Func2<Type, InputStream, Object>() {
      @Override
      public Object call(Type t, InputStream is) {
        ObjectInputStream ois = null;
        //noinspection TryWithIdenticalCatches
        try {
          return (ois = new ObjectInputStream(new BufferedInputStream(is))).readObject();
        } catch (IOException ioe) {
          throw new RuntimeException(ioe);
        } catch (ClassNotFoundException cnf) {
          throw new RuntimeException(cnf);
        } finally {
          if (ois != null) {
            try {
              ois.close();
            } catch (Exception ignored) {
            }
          }
        }
      }
    };
  }

  private static byte[] longToBytes(long l, int byteLen) {
    byte[] result = new byte[byteLen];
    for (int i = byteLen - 1; i >= 0; i--) {
      result[i] = (byte) (l & 0xFF);
      l >>= Byte.SIZE;
    }
    return result;
  }

  private static long bytesToLong(byte[] b, int byteLen) {
    long result = 0;
    for (int i = 0; i < byteLen; i++) {
      result <<= Byte.SIZE;
      result |= (b[i] & 0xFF);
    }
    return result;
  }

  public static RxCache diskLruCache( //
      final long size, //
      @NonNull final File cacheDir, //
      @NonNull final Action3<Object, Type, OutputStream> writerFunc, //
      @NonNull final Func2<Type, InputStream, Object> readerFunc, //
      @Nullable Func1<String, String> fileNamingFunc //
  ) {
    if (cacheDir.exists() && !cacheDir.isDirectory()) {
      throw new IllegalArgumentException("cacheDir must be directory.");
    }
    if (!cacheDir.exists() && !cacheDir.mkdirs()) {
      throw new IllegalArgumentException("cacheDir must be creatable as a directory.");
    }
    final Func1<String, String> namer = fileNamingFunc == null
        ? simpleCacheFileNamer()
        : fileNamingFunc;
    return new RxCache() {

      final DiskLruCache cache = DiskLruCache.create(FileSystem.SYSTEM, cacheDir, 1, 1, size);
      final Map<String, Pair<Object, Type>> rescheduledPuts = new HashMap<>();

      @NonNull
      @Override
      public String type() {
        return "diskLru";
      }

      private void reschedulePut(@NonNull final String k, @Nullable Object v, @NonNull Type t,
          boolean force) {

        boolean hasPrev;
        synchronized (rescheduledPuts) {
          hasPrev = rescheduledPuts.put(k, new Pair<>(v, t)) != null;
        }

        if (!force && hasPrev) return;

        final Scheduler.Worker w = Schedulers.io().createWorker();
        w.schedule(new Action0() {
          @Override
          public void call() {
            Pair<Object, Type> vals;
            synchronized (rescheduledPuts) {
              vals = rescheduledPuts.get(k);
            }
            if (vals == null) throw new RuntimeException("illegal null vals");
            try {
              put(k, vals.first, vals.second);
            } finally {
              w.unsubscribe();
              boolean newPuts;
              synchronized (rescheduledPuts) {
                newPuts = rescheduledPuts.get(k) != vals;
                if (!newPuts) rescheduledPuts.remove(k);
              }
              if (newPuts) reschedulePut(k, vals.first, vals.second, true);
            }
          }
        });
      }

      @Override
      public void put(@NonNull final String kk, @Nullable final Object v, @NonNull final Type t) {
        final String k = namer.call(kk);

        OutputStream os = null;
        Sink snk = null;
        DiskLruCache.Editor ed = null;

        try {
          if (v == null) {
            cache.remove(k);
            return;
          }

          ed = cache.edit(k);

          if (ed == null) {
            reschedulePut(kk, v, t, false);
            return;
          }

          snk = ed.newSink(0);
          Buffer b = new okio.Buffer();
          os = b.outputStream();

          // write prefix bytes of timestamps - modified at, created at
          byte byteLen = (byte) Long.SIZE / (byte) Byte.SIZE;
          byte[] nowBytes = longToBytes(currentTimeMillis(), byteLen);
          byte[] timestampBytes = new byte[nowBytes.length * 2 + 1];
          timestampBytes[0] = byteLen;
          arraycopy(nowBytes, 0, timestampBytes, 1, nowBytes.length);
          arraycopy(nowBytes, 0, timestampBytes, 1 + nowBytes.length, nowBytes.length);
          os.write(timestampBytes);

          writerFunc.call(v, t, os);
          b.readAll(snk);
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "diskLruCache put err", e);
          }
        } finally {
          if (os != null) {
            try {
              os.close();
            } catch (Exception ignored) {
            }
          }
          if (snk != null) {
            try {
              snk.close();
            } catch (Exception ignored) {
            }
          }
          if (ed != null) {
            try {
              ed.commit();
            } catch (Exception ignored) {
            }
          }
        }
      }

      @Nullable
      @Override
      public Object get(@NonNull String k, @NonNull Type t, int maxAge, TimeUnit maxAgeUnit,
          @NonNull AgeSince ageSince) {
        k = namer.call(k);

        InputStream is = null;
        Source src = null;
        DiskLruCache.Snapshot sn = null;

        Sink snk = null;
        DiskLruCache.Editor ed = null;

        Object val = null;

        try {
          sn = cache.get(k);
          if (sn == null) return null;
          src = sn.getSource(0);
          Buffer b = new okio.Buffer();
          b.writeAll(src);
          is = b.inputStream();

          byte byteLen = (byte) is.read();

          Buffer bb = new Buffer();
          bb.writeByte(byteLen);
          byte[] nowBytes = longToBytes(currentTimeMillis(), byteLen);
          bb.write(nowBytes);
          b.copyTo(bb, nowBytes.length, b.size() - nowBytes.length);

          byte[] b1 = new byte[byteLen];
          byte[] b2 = new byte[byteLen];
          //noinspection StatementWithEmptyBody
          for (int r = 0; r < byteLen; r += is.read(b1, r, byteLen - r)) {
          }
          //noinspection StatementWithEmptyBody
          for (int r = 0; r < byteLen; r += is.read(b2, r, byteLen - r)) {
          }
          if (ageSince == SINCE_CREATED) {
            if (currentTimeMillis() - bytesToLong(b2, byteLen) > maxAgeUnit.toMillis(maxAge)) {
              return null;
            }
          } else if (ageSince == SINCE_ACCESSED) {
            if (currentTimeMillis() - bytesToLong(b1, byteLen) > maxAgeUnit.toMillis(maxAge)) {
              return null;
            }
          }

          val = readerFunc.call(t, is);

          if ((ed = sn.edit()) != null) bb.readAll(snk = ed.newSink(0));
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "diskLruCache get err", e);
          }
        } finally {
          if (is != null) {
            try {
              is.close();
            } catch (Exception ignored) {
            }
          }
          if (src != null) {
            try {
              src.close();
            } catch (Exception ignored) {
            }
          }
          if (sn != null) {
            try {
              sn.close();
            } catch (Exception ignored) {
            }
          }
          if (snk != null) {
            try {
              snk.close();
            } catch (Exception ignored) {
            }
          }
          if (ed != null) {
            try {
              ed.commit();
            } catch (Exception ignored) {
            }
          }
        }

        return val;
      }

      @Nullable
      @Override
      public Scheduler readScheduler() {
        return Schedulers.io();
      }

      @Nullable
      @Override
      public Scheduler writeScheduler() {
        return Schedulers.io();
      }
    };
  }

  public static RxCache diskCache( //
      final int size, //
      @NonNull final File cacheDir, //
      @NonNull final Action3<Object, Type, OutputStream> writerFunc, //
      @NonNull final Func2<Type, InputStream, Object> readerFunc, //
      @Nullable Func1<String, String> fileNamingFunc //
  ) {
    if (cacheDir.exists() && !cacheDir.isDirectory()) {
      throw new IllegalArgumentException("cacheDir must be directory.");
    }
    final Func1<String, String> namer = fileNamingFunc == null
        ? simpleCacheFileNamer()
        : fileNamingFunc;
    return new RxCache() {

      final Map<String, ReentrantReadWriteLock> locks = new LinkedHashMap<>();
      final ReentrantLock locksLock = new ReentrantLock();
      final String cacheFilePrefix = format(DISK_CACHE_FILENAME_FORMAT, "");
      final AtomicBoolean pruning = new AtomicBoolean();

      @NonNull
      @Override
      public String type() {
        return "disk";
      }

      // quietly delete the least recently modified files until cache is desired size.
      private boolean pruneCache() {
        //long nano = System.nanoTime(); // FIXME
        //if (!pruning.compareAndSet(false, true)) return;
        Log.e("TESTO", "prune starting...."); // FIXME
        try {
          List<File> files = new ArrayList<>(asList(cacheDir.listFiles()));
          if (files.isEmpty()) return true;
          for (Iterator<File> i = files.iterator(); i.hasNext(); ) {
            File f = i.next();
            if (f.isDirectory() || !f.getName().startsWith(cacheFilePrefix)) i.remove();
          }
          if (files.isEmpty()) return true;

          if (files.size() > 1) {
            final Map<File, Long> lmm = new HashMap<>();
            for (File f : files) lmm.put(f, f.lastModified());
            sort(files, new Comparator<File>() {
              @Override
              public int compare(File f1, File f2) {
                Long f1l = lmm.get(f1);
                Long f2l = lmm.get(f2);
                if (f1l == null && f2l == null) return 0;
                if (f2l == null) return 1;
                if (f1l == null) return -1;
                if (f1l > f2l) return 1;
                if (f1l < f2l) return -1;
                return 0;
              }
            });
          }

          int filesRemaining = files.size();
          for (int i = 0; //
              i < files.size() && //
                  filesRemaining > size && //
                  files.size() - filesRemaining < MAX_DISK_CACHE_PRUNES_PER_PASS; //
              i++) {
            File fileToPrune = files.get(i);
            ReentrantReadWriteLock l = null;
            try {
              l = lockForKey(fileToPrune.getName(), true, true);
              if (l == null) continue;
              if (fileToPrune.delete()) filesRemaining--;
            } finally {
              if (l != null) l.writeLock().unlock();
            }
          }

          Log.e("TESTO", "deleted " + (files.size() - filesRemaining)); // FIXME

          if (filesRemaining > size) return false;
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "pruneCache err", e);
          }
        } finally {
          Log.e("TESTO", "..... prune finished"); // FIXME
        }
        //finally {
        //pruning.set(false);
        //}
        //log("PRUNE TOOK " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nano)
        //    + " ms"); // FIXME
        return true;
      }

      private ReentrantReadWriteLock lockForKey(@NonNull String k, boolean write) {
        return lockForKey(k, write, false);
      }

      private boolean tryLock(Lock lk) {
        try {
          return lk.tryLock(0, SECONDS);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      private ReentrantReadWriteLock lockForKey(@NonNull String k, boolean write, boolean tryLk) {

        boolean locksLockLocked = false;
        try {
          if (tryLk) {
            if (!tryLock(locksLock)) return null;
          } else {
            locksLock.lock();
          }
          locksLockLocked = true;

          ReentrantReadWriteLock lock = locks.remove(k);
          if (lock == null) lock = new ReentrantReadWriteLock();
          locks.put(k, lock);

          // while we're here, don't let the number of locks grow indefinitely.
          for (Iterator<Entry<String, ReentrantReadWriteLock>> i = locks.entrySet().iterator();
              i.hasNext() && locks.size() > MAX_DISK_CACHE_LOCK_MAP_SIZE; ) {
            //i.hasNext() && locks.size() > 3; ) { // FIXME

            ReentrantReadWriteLock l = i.next().getValue();
            if (l == lock) continue;

            boolean lked = false;
            try {
              if (tryLock(l.writeLock())) {
                lked = true;
                i.remove();
              }
            } finally {
              if (lked) l.writeLock().unlock();
            }
          }

          if (write) {
            if (tryLk) {
              if (!tryLock(lock.writeLock())) return null;
            } else {
              lock.writeLock().lock();
            }
          } else {
            if (tryLk) {
              if (!tryLock(lock.readLock())) {
                return null;
              }
            } else {
              lock.readLock().lock();
            }
          }
          return lock;
        } finally {
          if (locksLockLocked) locksLock.unlock();
        }
      }

      @Override
      public void put(@NonNull String k, @Nullable final Object v, @NonNull Type t) {
        //long now = System.nanoTime(); // FIXME
        k = format(DISK_CACHE_FILENAME_FORMAT, namer.call(k));
        File f = new File(cacheDir, k);

        OutputStream os = null;
        ReentrantReadWriteLock lock = null;
        try {
          lock = lockForKey(k, true);

          if (v == null) {
            //noinspection ResultOfMethodCallIgnored
            f.delete();
            return;
          }
          //noinspection ResultOfMethodCallIgnored
          cacheDir.mkdirs();
          os = new FileOutputStream(f);

          // write prefix bytes of timestamps - created at, modified at
          byte byteLen = (byte) Long.SIZE / (byte) Byte.SIZE;
          byte[] nowBytes = longToBytes(currentTimeMillis(), byteLen);
          byte[] timestampBytes = new byte[nowBytes.length * 2 + 1];
          timestampBytes[0] = byteLen;
          arraycopy(nowBytes, 0, timestampBytes, 1, nowBytes.length);
          arraycopy(nowBytes, 0, timestampBytes, 1 + nowBytes.length, nowBytes.length);
          os.write(timestampBytes);

          writerFunc.call(v, t, os);
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "diskCache put err", e);
          }
        } finally {
          if (os != null) {
            try {
              os.close();
            } catch (Exception ignored) {
            }
          }
          if (lock != null) lock.writeLock().unlock();
        }

        schedulePrune();
        //Log.e("TESTO",
        //    "PUT TOOK " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - now) + " ms"); // FIXME
      }

      private void schedulePrune() {
        if (!pruning.compareAndSet(false, true)) return;
        final Scheduler.Worker w = Schedulers.io().createWorker();
        w.schedule(new Action0() {
          @Override
          public void call() {
            boolean shouldReschedule = false;
            try {
              shouldReschedule = !pruneCache();
            } finally {
              w.unsubscribe();
              pruning.set(false);
              if (shouldReschedule) schedulePrune();
            }
          }
        }, 3, SECONDS);
      }

      @Nullable
      @Override
      public Object get(@NonNull String k, @NonNull Type t, //
          int maxAge, TimeUnit maxAgeUnit, @NonNull AgeSince ageSince) {
        //long now = System.nanoTime(); // FIXME
        k = format(DISK_CACHE_FILENAME_FORMAT, namer.call(k));
        File f = new File(cacheDir, k);

        Object val = null;
        InputStream is = null;
        ReentrantReadWriteLock lock = null;
        byte byteLen = 0;
        try {
          lock = lockForKey(k, false);
          is = new FileInputStream(f);

          byteLen = (byte) is.read();
          byte[] b1 = new byte[byteLen];
          byte[] b2 = new byte[byteLen];
          //noinspection StatementWithEmptyBody
          for (int r = 0; r < byteLen; r += is.read(b1, r, byteLen - r)) {
          }
          //noinspection StatementWithEmptyBody
          for (int r = 0; r < byteLen; r += is.read(b2, r, byteLen - r)) {
          }
          if (ageSince == SINCE_CREATED) {
            if (currentTimeMillis() - bytesToLong(b2, byteLen) > maxAgeUnit.toMillis(maxAge)) {
              return null;
            }
          } else if (ageSince == SINCE_ACCESSED) {
            if (currentTimeMillis() - bytesToLong(b1, byteLen) > maxAgeUnit.toMillis(maxAge)) {
              return null;
            }
          }

          //noinspection ResultOfMethodCallIgnored
          f.setLastModified(currentTimeMillis());

          val = readerFunc.call(t, is);
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "diskCache get err", e);
          }
        } finally {
          if (is != null) {
            try {
              is.close();
            } catch (Exception ignored) {
            }
          }
          if (lock != null) lock.readLock().unlock();
        }

        if (val == null) return null;

        // Update the access time prefix bytes ...

        lock = null;
        RandomAccessFile raf = null;
        try {
          lock = lockForKey(k, true);
          raf = new RandomAccessFile(f, "w");
          raf.seek(byteLen);
          raf.write(longToBytes(currentTimeMillis(), byteLen));
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.e(VinliRx.class.getSimpleName(), "diskCache update access time err", e);
          }
        } finally {
          if (raf != null) {
            try {
              raf.close();
            } catch (Exception ignored) {
            }
          }
          if (lock != null) lock.writeLock().unlock();
        }

        //Log.e("TESTO",
        //    "GET TOOK " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - now) + " ms"); // FIXME
        return val;
      }

      @Nullable
      @Override
      public Scheduler readScheduler() {
        return Schedulers.io();
      }

      @Nullable
      @Override
      public Scheduler writeScheduler() {
        return Schedulers.io();
      }
    };
  }

  private static class MemCacheVal {

    final long creationTime;
    volatile long accessTime;
    final Object val;

    private MemCacheVal(Object val) {
      this.creationTime = this.accessTime = currentTimeMillis();
      this.val = val;
    }
  }

  public static RxCache memCache( //
      final int size, @Nullable final Func2<String, Object, Integer> sizeOfFunc) {
    return new RxCache() {

      final LruCache<String, MemCacheVal> c = new LruCache<String, MemCacheVal>(size) {
        @Override
        protected int sizeOf(String key, MemCacheVal value) {
          if (sizeOfFunc == null) return super.sizeOf(key, value);
          return sizeOfFunc.call(key, value.val);
        }
      };

      @NonNull
      @Override
      public String type() {
        return "mem";
      }

      @Override
      public void put(@NonNull String k, @Nullable Object v, @NonNull Type t) {
        if (v == null) {
          c.remove(k);
        } else {
          c.put(k, new MemCacheVal(v));
        }
      }

      @Nullable
      @Override
      public Object get(@NonNull String k, @NonNull Type t, //
          int maxAge, TimeUnit maxAgeUnit, @NonNull AgeSince ageSince) {
        MemCacheVal val = c.get(k);
        if (val == null) return null;
        long now = currentTimeMillis();
        long lastAccess = val.accessTime;
        val.accessTime = now;
        if (ageSince == SINCE_CREATED) {
          if (now - val.creationTime > maxAgeUnit.toMillis(maxAge)) {
            return null;
          }
        } else if (ageSince == SINCE_ACCESSED) {
          if (now - lastAccess > maxAgeUnit.toMillis(maxAge)) {
            return null;
          }
        }
        return val.val;
      }

      @Nullable
      @Override
      public Scheduler readScheduler() {
        return null;
      }

      @Nullable
      @Override
      public Scheduler writeScheduler() {
        return null;
      }
    };
  }

  /**
   * Variant of {@link #cachifyOnError(String, Type, int, TimeUnit, AgeSince, Func1, RxCache...)}
   * that provides no error predicate, so all errors are eligible for return-from-cache behavior.
   */
  public static <T> Observable.Transformer<T, T> cachifyOnError( //
      @NonNull final String k, @NonNull final Type listType, //
      final int maxAge, final TimeUnit maxAgeUnit, @NonNull final AgeSince ageSince, //
      @NonNull final RxCache... caches) {
    return cachifyOnError(k, listType, maxAge, maxAgeUnit, ageSince, null, caches);
  }

  /**
   * Variant of {@link #cachify(String, Type, int, TimeUnit, AgeSince, boolean, RxCache...)} that
   * does not return cached values unless an error occurs and the given predicate, if provided,
   * returns true for the error. If no error occurs, the caches are seeded with fresh values from
   * the source observable. When an error occurs, the latest of these values is returned. If no
   * cached value exists meeting the age criteria provided, or the predicate returns false, the
   * error is propagated normally (so this is NOT a mechanism for ignoring errors entirely; error
   * handling must still be provided on the other end of this transformation).
   */
  public static <T> Observable.Transformer<T, T> cachifyOnError( //
      @NonNull final String k, @NonNull final Type listType, //
      final int maxAge, final TimeUnit maxAgeUnit, @NonNull final AgeSince ageSince, //
      @Nullable final Func1<Throwable, Boolean> predicate, //
      @NonNull final RxCache... caches) {
    return new Observable.Transformer<T, T>() {
      @Override
      public Observable<T> call(Observable<T> source) {
        return source.compose(VinliRx.<T>seedCachify(k, listType, caches))
            .onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
              @Override
              public Observable<? extends T> call(Throwable throwable) {
                Observable<T> errObs = Observable.error(throwable);
                if (predicate != null && !predicate.call(throwable)) return errObs;
                return errObs.compose(VinliRx.<T>cachify( //
                    k, listType, maxAge, maxAgeUnit, ageSince, caches));
              }
            });
      }
    };
  }

  /**
   * Variant of {@link #cachify(String, Type, int, TimeUnit, AgeSince, boolean, RxCache...)} that
   * only seeds the caches with fresh values from the source observable, but never returns any
   * values from the caches. Useful if you want to keep the caches warm for other usages.
   */
  public static <T> Observable.Transformer<T, T> seedCachify( //
      @NonNull final String k, @NonNull final Type listType, //
      @NonNull final RxCache... caches) {
    return cachify(k, listType, 0, SECONDS, SINCE_CREATED, true, caches);
  }

  /**
   * Utility that transforms the source observable to always instead return cached values if
   * available, pulling from caches in order of priority (expected to be be passed in order of
   * least- to most-expensive). Cached values that don't meet the given age criteria are ignored,
   * but not evicted from the underlying caches (in case some other usage of cachify has less
   * strict criteria). Values returned from the source observable or more expensive caches are
   * automatically seeded into lower priority caches so that the least-expensive caches are most
   * likely to remain warm.
   */
  public static <T> Observable.Transformer<T, T> cachify( //
      @NonNull final String k, @NonNull final Type listType, //
      final int maxAge, final TimeUnit maxAgeUnit, @NonNull final AgeSince ageSince, //
      @NonNull final RxCache... caches) {
    return cachify(k, listType, maxAge, maxAgeUnit, ageSince, false, caches);
  }

  private static <T> Observable.Transformer<T, T> cachify( //
      @NonNull final String k, @NonNull final Type listType, //
      final int maxAge, final TimeUnit maxAgeUnit, @NonNull final AgeSince ageSince, //
      final boolean onlySeedCaches, //
      @NonNull final RxCache... caches) {

    return new Observable.Transformer<T, T>() {

      @Override
      public Observable<T> call(final Observable<T> source) {

        final Object sentinel = new Object();

        Observable<?> cachesObs;
        if (onlySeedCaches) {
          cachesObs = Observable.empty();
        } else {
          cachesObs = observeGetFromCache( //
              k, listType, maxAge, maxAgeUnit, ageSince, true, caches) //
              .flatMap(new Func1<Object, Observable<?>>() {
                @Override
                public Observable<?> call(Object o) {
                  return Observable //
                      .from((List<?>) o) //
                      .concatWith(Observable.just(sentinel));
                }
              });
        }

        Observable<?> sourceObs = Observable.using(new Func0<List<T>>() {
          @Override
          public List<T> call() {
            return synchronizedList(new ArrayList<T>());
          }
        }, new Func1<List<T>, Observable<T>>() {
          @Override
          public Observable<T> call(final List<T> l) {
            return source //
                .doOnNext(new Action1<T>() {
                  @Override
                  public void call(T t) {
                    l.add(t);
                  }
                }) //
                .doOnCompleted(new Action0() {
                  @Override
                  public void call() {
                    synchronized (l) {
                      schedulePutIntoCache(k, l, listType, caches);
                    }
                  }
                });
          }
        }, new Action1<List<T>>() {
          @Override
          public void call(List<T> ts) {
            // no-op
          }
        });

        return concat(cachesObs, sourceObs) //
            .takeWhile(new Func1<Object, Boolean>() {
              @Override
              public Boolean call(Object o) {
                return o != sentinel;
              }
            }) //
            .map(new Func1<Object, T>() {
              @Override
              public T call(Object o) {
                //noinspection unchecked
                return (T) o;
              }
            });
      }
    };
  }

  private static final class SharedObservableHolder {
    private static final Map<Object, Observable> SHARED = new HashMap<>();
  }

  private static <T> Observable<T> getSharedObs(@NonNull Object key,
      @NonNull Func0<Observable<T>> newObsFactory) {
    synchronized (SharedObservableHolder.SHARED) {
      Observable o = SharedObservableHolder.SHARED.get(key);
      if (o == null) SharedObservableHolder.SHARED.put(key, o = newObsFactory.call());
      //noinspection unchecked
      return o;
    }
  }

  public static <T> Observable.Transformer<T, T> shareify() {
    return shareify(null, -1, SECONDS);
  }

  public static <T> Observable.Transformer<T, T> shareify( //
      int linger, @NonNull TimeUnit lingerUnit) {
    return shareify(null, linger, lingerUnit);
  }

  public static <T> Observable.Transformer<T, T> shareify(@Nullable final Object key) {
    return shareify(key, -1, SECONDS);
  }

  /**
   * Convenience to globally share an Observable so that concurrent subscribers don't resubscribe
   * to the source. If a nonnull key is given, this is used to uniquely identify the source.
   * Otherwise, the source observable itself is used as a key. Non-negative, non-zero linger values
   * will cause the shared source observable to "linger" and replay its last emissions for some
   * amount of time after completion, rather than immediately resubscribing to the source.
   */
  public static <T> Observable.Transformer<T, T> shareify(@Nullable final Object key,
      final int linger, @NonNull final TimeUnit lingerUnit) {

    return new Observable.Transformer<T, T>() {

      @Override
      public Observable<T> call(final Observable<T> source) {

        final Object fKey = key == null
            ? source
            : key;

        return getSharedObs(fKey, new Func0<Observable<T>>() {

          @Override
          public Observable<T> call() {

            return getSharedObs(fKey, new Func0<Observable<T>>() {
              @Override
              public Observable<T> call() {

                final List<Subscriber<? super T>> subs = new ArrayList<>();
                final List<T> nexts = new ArrayList<>();
                final AtomicBoolean completeButEmpty = new AtomicBoolean();
                final List<T> complete = new ArrayList<>();

                return Observable.create(new Observable.OnSubscribe<T>() {
                  @Override
                  public void call(final Subscriber<? super T> s) {

                    boolean subToSource;

                    synchronized (subs) {
                      if (!complete.isEmpty() || completeButEmpty.get()) {
                        for (T t : complete) s.onNext(t);
                        s.onCompleted();
                        return;
                      }
                      for (T t : nexts) s.onNext(t);
                      subToSource = subs.isEmpty();
                      subs.add(s);
                    }

                    if (!subToSource) return;

                    source.subscribe(new Subscriber<T>() {

                      @Override
                      public void onCompleted() {

                        boolean shouldLinger = linger > 0;

                        synchronized (subs) {
                          for (Subscriber<? super T> s : subs) s.onCompleted();
                          if (shouldLinger) {
                            if (nexts.isEmpty()) completeButEmpty.set(true);
                            complete.addAll(nexts);
                          }
                          nexts.clear();
                          subs.clear();
                        }

                        if (!shouldLinger) return;

                        final Scheduler.Worker w = Schedulers.computation().createWorker();
                        w.schedule(new Action0() {
                          @Override
                          public void call() {

                            try {
                              synchronized (subs) {
                                completeButEmpty.set(false);
                                complete.clear();
                              }
                            } finally {
                              w.unsubscribe();
                            }
                          }
                        }, linger, lingerUnit);
                      }

                      @Override
                      public void onError(Throwable e) {

                        synchronized (subs) {
                          for (Subscriber<? super T> s : subs) s.onError(e);
                          nexts.clear();
                          subs.clear();
                        }
                      }

                      @Override
                      public void onNext(T t) {

                        synchronized (subs) {
                          nexts.add(t);
                          for (Subscriber<? super T> s : subs) s.onNext(t);
                        }
                      }
                    });
                  }
                }).subscribeOn(Schedulers.computation());
              }
            });
          }
        });
      }
    };
  }
}
