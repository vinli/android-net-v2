package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.model.misc.StrictValidations.AllowNull;
import li.vin.netv2.model.misc.StrictValidations.ReqLink;
import li.vin.netv2.request.RequestPkgHooks;

import static li.vin.netv2.model.ModelPkgHooks.maps;

/**
 * Created by JoshBeridon on 2/14/17.
 */

public class Dummy extends BaseModels.BaseModelId {

  Dummy() {

  }

  String name;
  String caseId;
  String deviceId;

  @ReqLink({
      "self", //
      "runs", //
      "device", //
      "messages", //
      "events" //
  }) Map links;

  @NonNull
  public String name() {
    return name;
  }

  @NonNull
  public String caseId() {
    return caseId;
  }

  @NonNull
  public String deviceId() {
    return deviceId;
  }

  @NonNull
  public Link<Rule.Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Event.TimeSeries> runsLink() {
    return Link.create(maps.get().getStr(links, "runs"));
  }

  @NonNull
  public Link<Event.TimeSeries> deviceLink() {
    return Link.create(maps.get().getStr(links, "device"));
  }

  @NonNull
  public Link<Event.TimeSeries> messagesLink() {
    return Link.create(maps.get().getStr(links, "messages"));
  }

  @NonNull
  public Link<Event.TimeSeries> eventsLink() {
    return Link.create(maps.get().getStr(links, "events"));
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Dummy> {
    Wrapper() {

    }

    Dummy dummy;

    @NonNull
    @Override
    public Dummy extract() {
      return dummy;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  public static class Page extends BaseModels.BaseModelPage<Dummy> {
    Page() {

    }

    List<Dummy> dummies;

    @Override
    List<Dummy> rawPageContent() {
      return dummies;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Page>>() {
      }.getType();
    }
  }

  public static class Run extends BaseModels.BaseModelId {
    Run() {

    }

    @AllowNull Map status;

    @ReqLink({ //
        "self" //
    }) Map links;

    @NonNull
    public Data status() {
      return Data.create(status);
    }

    @NonNull
    public Link<Rule.Wrapper> selfLink() {
      return Link.create(maps.get().getStr(links, "self"));
    }

    public static class Wrapper extends BaseModels.BaseModelWrapper<Run> {
      Wrapper() {

      }

      @AllowNull Run run;

      @NonNull
      @Override
      public Run extract() {
        return run;
      }

      @NonNull
      public static Type listType() {
        return new TypeToken<List<Wrapper>>() {
        }.getType();
      }
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Dummy>>() {
      }.getType();
    }
  }

  public static class RunSeed extends BaseModels.BaseModel implements ModelSeed {

    @NonNull
    public static RunSeed create() {
      return new RunSeed(null, null);
    }

    RunSeed(final String vin, final String routeId) {
      this.vin = vin;
      this.routeId = routeId;
    }

    public RunSeed vin(String vin) {
      return new RunSeed(vin, routeId);
    }

    public RunSeed routeId(String routeId) {
      return new RunSeed(vin, routeId);
    }

    final String vin;
    final String routeId;

    @Override
    public void validate() {
      if (vin == null) throw new IllegalArgumentException("vin required");
      if (routeId == null) throw new IllegalArgumentException("routeId required");
    }

    public static class Wrapper {
      Wrapper() {

      }

      RunSeed run;

      public static void provideWrapper(RequestPkgHooks hooks, RunSeed runSeed) {
        hooks.runSeedWrapperHook = new RunSeed.Wrapper();
        hooks.runSeedWrapperHook.run = runSeed;
      }
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Dummy>>() {
    }.getType();
  }
}
