package li.vin.netv2.model.contract;

import android.support.annotation.NonNull;

public interface ExtractableModel<T> extends StrictModel {

  @NonNull
  T extract();
}
