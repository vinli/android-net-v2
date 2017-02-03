package li.vin.netv2.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.OptStr;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqLink;

public class Rule extends BaseModels.BaseModelId {

  Rule() {
  }

  String name;
  @AllowNull String deviceId;
  @AllowNull Boolean evaluated;
  @AllowNull Boolean covered;
  @ReqIsoDate String createdAt;
  @AllowNull @OptStr({ "id", "type" }) Map object;

  @ReqLink({ //
      "self", //
      "events", //
      "subscriptions" //
  }) Map links;

  @NonNull
  public String name() {
    return name;
  }

  @Nullable
  public String deviceId() {
    return deviceId;
  }

  public boolean evaluated() {
    return evaluated != null && evaluated;
  }

  public boolean covered() {
    return covered != null && covered;
  }

  @NonNull
  public String createdAt() {
    return createdAt;
  }

  @Nullable
  public String objectId() {
    return maps.get().getStrNullable(object, "id");
  }

  @Nullable
  public String objectType() {
    return maps.get().getStrNullable(object, "type");
  }

  @NonNull
  public Link<Wrapper> selfLink() {
    return Link.create(maps.get().getStr(links, "self"));
  }

  @NonNull
  public Link<Event.TimeSeries> eventsLink() {
    return Link.create(maps.get().getStr(links, "events"));
  }

  // TODO - convert this to a Link class
  @NonNull
  public String subscriptionsLink() {
    return maps.get().getStr(links, "subscriptions");
  }

  public static class Page extends BaseModels.BaseModelPage<Rule> {

    Page() {
    }

    List<Rule> rules;

    @Override
    List<Rule> rawPageContent() {
      return rules;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Page>>() {
      }.getType();
    }
  }

  public static class Wrapper extends BaseModels.BaseModelWrapper<Rule> {

    Wrapper() {
    }

    Rule rule;

    @NonNull
    @Override
    public Rule extract() {
      return rule;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<Wrapper>>() {
      }.getType();
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<Rule>>() {
    }.getType();
  }
}
