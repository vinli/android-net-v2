package li.vin.netv2.model;

import android.support.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import li.vin.netv2.model.contract.ModelSeed;
import li.vin.netv2.request.RequestPkgHooks;

import static java.lang.System.arraycopy;

// TODO - make RuleSeed abstract class in BaseModels
public class RuleSeed extends BaseModels.BaseModel implements ModelSeed {

  @NonNull
  public static RuleSeed create() {
    return new RuleSeed(null, null);
  }

  public interface Boundary extends ModelSeed {
  }

  RuleSeed(final String name, final List<Boundary> boundaries) {
    this.name = name;
    this.boundaries = boundaries == null
        ? new ArrayList<Boundary>()
        : new ArrayList<>(boundaries);
  }

  final String name;
  final List<Boundary> boundaries;

  public RuleSeed name(@NonNull String name) {
    return new RuleSeed(name, boundaries);
  }

  public RuleSeed boundary(@NonNull Boundary boundary) {
    List<Boundary> newBoundaries = new ArrayList<>(boundaries);
    newBoundaries.add(boundary);
    return new RuleSeed(name, newBoundaries);
  }

  @Override
  public void validate() {
    if (name == null) throw new IllegalArgumentException("name required.");
    if (boundaries.isEmpty()) {
      throw new IllegalArgumentException("at least one boundary required.");
    }
    for (Boundary b : boundaries) b.validate();
  }

  public static class ParametricBoundary extends BaseModels.BaseModel implements Boundary {

    @NonNull
    public static ParametricBoundary create() {
      return new ParametricBoundary(null, null, null);
    }

    ParametricBoundary(Double max, Double min, String parameter) {
      this.max = max;
      this.min = min;
      this.parameter = parameter;
    }

    final String type = "parametric";
    final Double max;
    final Double min;
    final String parameter;

    public ParametricBoundary max(double max) {
      return new ParametricBoundary(max, min, parameter);
    }

    public ParametricBoundary min(double min) {
      return new ParametricBoundary(max, min, parameter);
    }

    public ParametricBoundary parameter(@NonNull String parameter) {
      return new ParametricBoundary(max, min, parameter);
    }

    @Override
    public void validate() {
      if (max == null && min == null) throw new IllegalArgumentException("max or min required.");
      if (parameter == null) throw new IllegalArgumentException("parameter required.");
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<ParametricBoundary>>() {
      }.getType();
    }
  }

  public static class RadiusBoundary extends BaseModels.BaseModel implements Boundary {

    @NonNull
    public static RadiusBoundary create() {
      return new RadiusBoundary(null, null, null);
    }

    RadiusBoundary(Double radius, Double lat, Double lon) {
      this.radius = radius;
      this.lat = lat;
      this.lon = lon;
    }

    final String type = "radius";
    final Double radius;
    final Double lat;
    final Double lon;

    public RadiusBoundary radius(double radius) {
      return new RadiusBoundary(radius, lat, lon);
    }

    public RadiusBoundary lat(double lat) {
      return new RadiusBoundary(radius, lat, lon);
    }

    public RadiusBoundary lon(double lon) {
      return new RadiusBoundary(radius, lat, lon);
    }

    @Override
    public void validate() {
      if (radius == null) throw new IllegalArgumentException("radius required.");
      if (lat == null || lon == null) {
        throw new IllegalArgumentException("lat and lon required.");
      }
      if (lat < -90 || lat > 90) {
        throw new IllegalArgumentException("lat must be >= -90, <= 90.");
      }
      if (lon < -180 || lon > 180) {
        throw new IllegalArgumentException("lon must be >= -180, <= 180.");
      }
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<RadiusBoundary>>() {
      }.getType();
    }
  }

  public static class PolygonBoundary extends BaseModels.BaseModel implements Boundary {

    @NonNull
    public static PolygonBoundary create() {
      return new PolygonBoundary(null);
    }

    PolygonBoundary(List<List<double[]>> coordinates) {
      this.coordinates = deepCpyCoords(coordinates);
    }

    final String type = "polygon";
    final List<List<double[]>> coordinates;

    public PolygonBoundary newPolygon() {
      List<List<double[]>> newCoordinates = deepCpyCoords(coordinates);
      newCoordinates.add(new ArrayList<double[]>());
      return new PolygonBoundary(newCoordinates);
    }

    public PolygonBoundary addCoord(@NonNull double[] latLon) {
      double[] latLonCpy = new double[2];
      arraycopy(latLon, 0, latLonCpy, 0, 2);
      List<List<double[]>> newCoordinates = deepCpyCoords(coordinates);
      if (newCoordinates.isEmpty()) newCoordinates.add(new ArrayList<double[]>());
      List<double[]> curPoly = newCoordinates.get(newCoordinates.size() - 1);
      //if (!curPoly.isEmpty()) { // // todo
      //  double[] firstLatLon = curPoly.get(0);
      //  double[] lastLatLon = curPoly.get(curPoly.size() - 1);
      //  if (!Arrays.equals(firstLatLon, lastLatLon)) {
      //    curPoly.add(new double[] { firstLatLon[0], firstLatLon[1] });
      //  }
      //}
      curPoly.add(latLonCpy);
      return new PolygonBoundary(newCoordinates);
    }

    @Override
    public void validate() {
      if (coordinates.isEmpty()) {
        throw new IllegalArgumentException("at least one polygon required.");
      }
      for (List<double[]> poly : coordinates) {
        if (poly.size() < 4) {
          throw new IllegalArgumentException("each polygon must have at least 4 coords.");
        }
        for (double[] latLon : poly) {
          double lat = latLon[0];
          double lon = latLon[1];
          if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("lat must be >= -90, <= 90.");
          }
          if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("lon must be >= -180, <= 180.");
          }
        }
      }
    }

    private static List<List<double[]>> deepCpyCoords(List<List<double[]>> coords) {
      List<List<double[]>> coordsCpy = new ArrayList<>();
      if (coords != null) {
        for (List<double[]> poly : coords) {
          ArrayList<double[]> polyCpy = new ArrayList<>();
          for (double[] latLon : poly) {
            double[] latLonCpy = new double[2];
            arraycopy(latLon, 0, latLonCpy, 0, 2);
            polyCpy.add(latLonCpy);
          }
          coordsCpy.add(polyCpy);
        }
      }
      return coordsCpy;
    }

    @NonNull
    public static Type listType() {
      return new TypeToken<List<PolygonBoundary>>() {
      }.getType();
    }
  }

  public static class Wrapper {

    Wrapper() {
    }

    RuleSeed rule;

    public static void provideWrapper(RequestPkgHooks hooks, RuleSeed ruleSeed) {
      hooks.ruleSeedWrapperHook = new Wrapper();
      hooks.ruleSeedWrapperHook.rule = ruleSeed;
    }
  }

  @NonNull
  public static Type listType() {
    return new TypeToken<List<RuleSeed>>() {
    }.getType();
  }
}
