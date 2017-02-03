package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

import static li.vin.netv2.model.misc.StrictValidations.*;
import static li.vin.netv2.model.BaseModels.*;

public class User extends BaseModelId {

  User() {
  }

  public enum Unit {

    IMPERIAL("imperial"),
    METRIC("metric"),
    UNKNOWN("unknown");

    @NonNull private final String str;

    Unit(@NonNull String str) {
      this.str = str;
    }

    @Override
    public String toString() {
      return str;
    }

    @NonNull
    public static Unit fromString(@NonNull String str) {
      if ("imperial".equals(str)) return IMPERIAL;
      if ("metric".equals(str)) return METRIC;
      return UNKNOWN;
    }
  }

  @AllowNull String firstName;
  @AllowNull String lastName;
  @AllowNull String phone;
  @AllowNull String image;
  String email;
  @AllowNull @OptIsoDate String createdAt;
  @AllowNull @OptIsoDate String updatedAt;
  @AllowNull Settings settings;

  @Nullable
  public String firstName() {
    return firstName;
  }

  @Nullable
  public String lastName() {
    return lastName;
  }

  @Nullable
  public String phone() {
    return phone;
  }

  @Nullable
  public String image() {
    return image;
  }

  @NonNull
  public String email() {
    return email;
  }

  @Nullable
  public String createdAt() {
    return createdAt;
  }

  @Nullable
  public String updatedAt() {
    return updatedAt;
  }

  @NonNull
  public Settings settings() {
    return settings == null
        ? new Settings()
        : settings;
  }

  public static class Settings extends BaseModel {

    Settings() {
    }

    String unit;
    String locale;

    @NonNull
    public Unit unit() {
      return Unit.fromString(unit);
    }

    @NonNull
    public String locale() {
      return locale;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Settings>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModelWrapper<User> {

    Wrapper() {
    }

    User user;

    @NonNull
    @Override
    public User extract() {
      return user;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<User>>() {
    }.getType();
  }
}
