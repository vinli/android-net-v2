package li.vin.netv2.model;

import android.support.annotation.NonNull;

public enum SortDir {

  ASCENDING("asc"),
  DESCENDING("desc"),
  UNKNOWN("unknown");

  @NonNull private final String str;

  SortDir(@NonNull String str) {
    this.str = str;
  }

  @Override
  public String toString() {
    return str;
  }

  @NonNull
  public static SortDir fromString(@NonNull String str) {
    if ("asc".equals(str)) return ASCENDING;
    if ("desc".equals(str)) return DESCENDING;
    return UNKNOWN;
  }
}
