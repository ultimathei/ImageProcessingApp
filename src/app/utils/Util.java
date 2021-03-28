package app.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {
  private Util() {
    // hiding constructor
  }

  public static double clamp(double val, double min, double max) {
    return Math.max(min, Math.min(max, val));
  }
  public static int clamp(int val, int min, int max) {
    return Math.max(min, Math.min(max, val));
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
}
