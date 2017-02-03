package li.vin.netv2.model.contract;

import android.support.annotation.NonNull;
import java.util.List;
import li.vin.netv2.model.Link;
import li.vin.netv2.model.SortDir;

public interface ModelTimeSeries<T extends StrictModel> extends ExtractableModel<List<T>> {

  @NonNull
  Link<? extends ModelTimeSeries<T>> nextTimeLink();

  @NonNull
  Link<? extends ModelTimeSeries<T>> priorTimeLink();

  @NonNull
  String until();

  @NonNull
  String since();

  @NonNull
  SortDir sortDir();

  int remaining();

  int limit();
}
