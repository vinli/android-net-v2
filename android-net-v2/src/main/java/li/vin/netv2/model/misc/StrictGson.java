package li.vin.netv2.model.misc;

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import li.vin.netv2.Vinli;
import li.vin.netv2.internal.InternalPkgHooks;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.util.LazyOrSet;
import rx.functions.Func0;

public class StrictGson {

  StrictGson() {
  }

  // lazy init singleton inst

  static final LazyOrSet<StrictGson> inst = //
      LazyOrSet.create(new Func0<StrictGson>() {
        @Override
        public StrictGson call() {
          return new StrictGson();
        }
      });

  // inst providers

  public static void provideInst(InternalPkgHooks hooks) {
    hooks.strictGsonHook = inst.get();
  }

  public static void provideInst(Vinli.MainPkgHooks hooks) {
    hooks.strictGsonHook = inst.get();
  }

  // default impl

  @NonNull
  public Gson gson() {
    return strictGson;
  }

  private final Gson strictGson = new GsonBuilder() //
      .registerTypeAdapterFactory(new TypeAdapterFactory() {

        final Gson stockGson = new Gson();

        @Override
        public <T> TypeAdapter<T> create(Gson gs, final TypeToken<T> type) {
          final Class<? super T> rawType = type.getRawType();

          if (!StrictModel.class.isAssignableFrom(rawType)) {
            return null;
          }

          return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
              stockGson.toJson(value, type.getType(), out);
            }

            @Override
            public T read(JsonReader in) throws IOException {
              T val = stockGson.fromJson(in, type.getType());
              StrictValidations.inst.get().checkStrict(val);
              return val;
            }
          };
        }
      }) //
      .create();
}
