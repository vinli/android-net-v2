package li.vin.netv2.model;

import android.support.annotation.NonNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import li.vin.netv2.model.contract.ModelPage;
import li.vin.netv2.model.contract.ModelTimeSeries;
import li.vin.netv2.model.contract.ModelWrapper;
import li.vin.netv2.model.contract.StrictModel;
import li.vin.netv2.model.contract.StrictModelId;
import li.vin.netv2.util.ParcelableModel;

import static li.vin.netv2.BuildConfig.BASE_MODEL_SERIALIZATION_VER;
import static li.vin.netv2.model.ModelPkgHooks.autoMethodImpls;
import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.OptStr;
import static li.vin.netv2.model.misc.StrictValidations.ReqInt;
import static li.vin.netv2.model.misc.StrictValidations.ReqIsoDate;
import static li.vin.netv2.model.misc.StrictValidations.ReqStr;

final class BaseModels {

  private BaseModels() {
  }

  // every model, with equals / hashCode / toString auto-impls

  @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
  abstract static class BaseModel implements StrictModel, Serializable {

    private static final long serialVersionUID = BASE_MODEL_SERIALIZATION_VER;

    BaseModel() {
    }

    @NonNull
    @Override
    public ParcelableModel toParcelable() {
      return ParcelableModel.fromModel(this);
    }

    @Override
    public boolean equals(Object obj) {
      return autoMethodImpls.get().autoEquals(this, obj);
    }

    private volatile transient int hashCode;

    @Override
    public int hashCode() {
      if (hashCode == 0) hashCode = autoMethodImpls.get().autoHashCode(this);
      return hashCode;
    }

    @Override
    public String toString() {
      return autoMethodImpls.get().autoToString(this);
    }
  }

  // "id" field model impl

  abstract static class BaseModelId extends BaseModel implements StrictModelId {

    BaseModelId() {
    }

    String id;

    @NonNull
    @Override
    public String id() {
      return id;
    }
  }

  // simple paging model impl

  abstract static class BaseModelPage<T extends StrictModel> extends BaseModel
      implements ModelPage<T> {

    BaseModelPage() {
    }

    @ReqStr({ //
        "pagination.links.first", //
        "pagination.links.last" //
    }) //
    @OptStr({ //
        "pagination.links.next", //
        "pagination.links.prev" //
    }) //
    @ReqInt({ //
        "pagination.total", //
        "pagination.limit", //
        "pagination.offset" //
    }) Map meta;

    abstract List<T> rawPageContent();

    @NonNull
    @Override
    public List<T> extract() {
      List<T> l = rawPageContent();
      if (l == null) return new ArrayList<>();
      return new ArrayList<>(l);
    }

    @NonNull
    @Override
    public Link<BaseModelPage<T>> firstPageLink() {
      return Link.create(maps.get().getStr(meta, "pagination.links.first"));
    }

    @NonNull
    @Override
    public Link<BaseModelPage<T>> lastPageLink() {
      return Link.create(maps.get().getStr(meta, "pagination.links.last"));
    }

    @NonNull
    @Override
    public Link<BaseModelPage<T>> nextPageLink() {
      String link = maps.get().getStrNullable(meta, "pagination.links.next");
      if (link == null) return Link.create(Link.NO_LINK);
      return Link.create(link);
    }

    @NonNull
    @Override
    public Link<BaseModelPage<T>> prevPageLink() {
      String link = maps.get().getStrNullable(meta, "pagination.links.prev");
      if (link == null) return Link.create(Link.NO_LINK);
      return Link.create(link);
    }

    @Override
    public int total() {
      return maps.get().getInt(meta, "pagination.total");
    }

    @Override
    public int limit() {
      return maps.get().getInt(meta, "pagination.limit");
    }

    @Override
    public int offset() {
      return maps.get().getInt(meta, "pagination.offset");
    }
  }

  // timeseries paging model impl

  abstract static class BaseModelTimeSeries<T extends StrictModel> extends BaseModel
      implements ModelTimeSeries<T> {

    BaseModelTimeSeries() {
    }

    @OptStr({ //
        "pagination.links.next", //
        "pagination.links.prior" //
    }) //
    @ReqIsoDate({ //
        "pagination.until", //
        "pagination.since" //
    }) //
    @ReqStr({ //
        "pagination.sortDir" //
    }) //
    @ReqInt({ //
        "pagination.remaining", //
        "pagination.limit" //
    }) Map meta;

    abstract List<T> rawTimeSeriesContent();

    @NonNull
    @Override
    public List<T> extract() {
      List<T> l = rawTimeSeriesContent();
      if (l == null) return new ArrayList<>();
      return new ArrayList<>(l);
    }

    @NonNull
    @Override
    public Link<BaseModelTimeSeries<T>> nextTimeLink() {
      String link = maps.get().getStrNullable(meta, "pagination.links.next");
      if (link == null) return Link.create(Link.NO_LINK);
      return Link.create(link);
    }

    @NonNull
    @Override
    public Link<BaseModelTimeSeries<T>> priorTimeLink() {
      String link = maps.get().getStrNullable(meta, "pagination.links.prior");
      if (link == null) return Link.create(Link.NO_LINK);
      return Link.create(link);
    }

    @NonNull
    @Override
    public String until() {
      return maps.get().getStr(meta, "pagination.until");
    }

    @NonNull
    @Override
    public String since() {
      return maps.get().getStr(meta, "pagination.since");
    }

    @NonNull
    @Override
    public SortDir sortDir() {
      return SortDir.fromString(maps.get().getStr(meta, "pagination.sortDir"));
    }

    @Override
    public int remaining() {
      return maps.get().getInt(meta, "pagination.remaining");
    }

    @Override
    public int limit() {
      return maps.get().getInt(meta, "pagination.limit");
    }
  }

  // wrapper model impl

  abstract static class BaseModelWrapper<T extends StrictModel> extends BaseModel
      implements ModelWrapper<T> {

    BaseModelWrapper() {
    }
  }
}
