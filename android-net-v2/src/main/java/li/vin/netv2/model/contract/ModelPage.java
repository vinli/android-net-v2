package li.vin.netv2.model.contract;

import android.support.annotation.NonNull;
import java.util.List;
import li.vin.netv2.model.Link;

public interface ModelPage<T extends StrictModel> extends ExtractableModel<List<T>> {

  @NonNull
  Link<? extends ModelPage<T>> firstPageLink();

  @NonNull
  Link<? extends ModelPage<T>> lastPageLink();

  @NonNull
  Link<? extends ModelPage<T>> nextPageLink();

  @NonNull
  Link<? extends ModelPage<T>> prevPageLink();

  int total();

  int limit();

  int offset();
}
