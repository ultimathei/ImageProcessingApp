package app.utils;

public class Util {
  private Util() {
    // hiding constructor
  }

  public static double clamp(double val, double min, double max) {
    return Math.max(min, Math.min(max, val));
  }
}
