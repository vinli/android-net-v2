package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.error.NoResourceExistsException;
import li.vin.netv2.request.RequestPkgHooks;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class DtcDiagnosis extends BaseModels.BaseModelId {

  DtcDiagnosis() {
  }

  public static class NoDtcDiagnosisException extends NoResourceExistsException {

    public NoDtcDiagnosisException() {
      super("No DTC diagnosis available for the given number.");
    }
  }

  @AllowNull String make;
  @AllowNull String system;
  @AllowNull String subsystem;
  String number;
  String description;

  @ReqLink({ //
      "self" //
  }) Map links;

  @Nullable
  public String make() {
    return make;
  }

  @Nullable
  public String system() {
    return system;
  }

  @Nullable
  public String subsystem() {
    return subsystem;
  }

  @NonNull
  public String number() {
    return number;
  }

  @NonNull
  public String description() {
    return description;
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  public static class Page extends BaseModels.BaseModelPage<DtcDiagnosis> {

    Page() {
    }

    List<DtcDiagnosis> codes;

    @Override
    List<DtcDiagnosis> rawPageContent() {
      return codes;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Page>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<DtcDiagnosis> {

    Wrapper() {
    }

    @AllowNull DtcDiagnosis code;

    @NonNull
    @Override
    public DtcDiagnosis extract() {
      if (code == null) throw new NoDtcDiagnosisException();
      return code;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }

    public static void provideWrapper(RequestPkgHooks hooks, DtcDiagnosis dtcDiagnosis) {
      hooks.dtcDiagWrapperHook = new Wrapper();
      hooks.dtcDiagWrapperHook.code = dtcDiagnosis;
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<DtcDiagnosis>>() {
    }.getType();
  }
}
