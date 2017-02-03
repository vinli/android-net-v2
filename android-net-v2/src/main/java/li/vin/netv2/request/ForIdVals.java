package li.vin.netv2.request;

import android.support.annotation.NonNull;
import java.util.EnumSet;

import static java.lang.String.format;

class ForIdVals {

  @NonNull final ForId forId;
  @NonNull final String target;
  @NonNull final EnumSet<ForId> allowed;

  ForIdVals(@NonNull ForId forId, @NonNull String target, @NonNull EnumSet<ForId> allowed) {
    this.forId = forId;
    this.target = target;
    this.allowed = EnumSet.copyOf(allowed);
  }

  void validate() {
    if (!allowed.contains(forId)) {
      throw new IllegalArgumentException(format("for %s not allowed.", forId.toString()));
    }
  }
}
