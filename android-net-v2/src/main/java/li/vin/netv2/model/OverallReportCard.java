package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static li.vin.netv2.model.BaseModels.BaseModelWrapper;
import static li.vin.netv2.model.ModelPkgHooks.maps;
import static li.vin.netv2.model.misc.StrictValidations.AllowNull;
import static li.vin.netv2.model.misc.StrictValidations.OptStr;

public class OverallReportCard extends BaseModelWrapper<OverallReportCard> {

  OverallReportCard() {
  }

  int tripSampleSize;
  @AllowNull @OptStr({ "overallGrade" }) Map reportCard;
  @AllowNull @OptStr({ "A", "B", "C", "D", "F", "I" }) Map gradeCount;

  public int tripSampleSize() {
    return tripSampleSize;
  }

  @NonNull
  public ReportCard.Grade overallGrade() {
    String str = maps.get().getStrNullable(reportCard, "overallGrade");
    if (str == null) return ReportCard.Grade.UNKNOWN;
    return ReportCard.Grade.fromString(str);
  }

  public int gradeCount(@NonNull ReportCard.Grade grade) {
    Integer count = maps.get().getIntNullable(gradeCount, grade.toString());
    if (count == null) return 0;
    return count;
  }

  // A little hacky for this model to be its own wrapper, but it's a weird model. Works for now.
  @NonNull
  @Override
  public OverallReportCard extract() {
    return this;
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<OverallReportCard>>() {
    }.getType();
  }
}
