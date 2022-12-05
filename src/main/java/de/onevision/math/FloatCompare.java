package de.onevision.math;

import java.lang.Double;

public class FloatCompare {
    public static boolean eq(double lhs, double rhs) {
      return Double.compare(lhs, rhs) == 0;
    }

    public static boolean neq(double lhs, double rhs) {
        return Double.compare(lhs, rhs) != 0;
      }

    public static boolean leq(double lhs, double rhs) {
        return Double.compare(lhs, rhs) <= 0;
    }

    public static boolean geq(double lhs, double rhs) {
        return Double.compare(lhs, rhs) >= 0;
    }

    public static boolean lo(double lhs, double rhs) {
        return Double.compare(lhs, rhs) < 0;
    }

    public static boolean gr(double lhs, double rhs) {
        return Double.compare(lhs, rhs) > 0;
    }
}
