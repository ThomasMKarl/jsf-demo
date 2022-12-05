package de.onevision.color;

import java.util.Arrays;
import org.w3c.dom.Element;

import de.onevision.math.FloatCompare;

public class CMYK {
    public CMYK(double cyan, double magenta, double yellow, double black) {
        colors[CMYK.cyan] = cyan;
        colors[CMYK.magenta] = magenta;
        colors[CMYK.yellow] = yellow;
        colors[CMYK.black] = black;
        shiftToPositive();
        scale();
    }

    public boolean equals(CMYK rhs) {
        if(FloatCompare.neq(this.colors[cyan], rhs.colors[cyan]))
          return false;
        if(FloatCompare.neq(this.colors[magenta], rhs.colors[magenta]))
          return false;
        if(FloatCompare.neq(this.colors[yellow], rhs.colors[yellow]))
          return false;
        if(FloatCompare.neq(this.colors[black], rhs.colors[black]))
          return false;
        return true;
    }

    public double cyan() {
        return colors[cyan];
    }

    public double magenta() {
        return colors[magenta];
    }

    public double yellow() {
        return colors[yellow];
    }

    public double black() {
        return colors[black];
    }

    public Element appendAttributes(Element elem) {
      elem.setAttribute("cmyk", Double.toString(cyan()) + " " + Double.toString(magenta()) + " " + Double.toString(yellow()) + " " + Double.toString(black()));
      return elem;
    }

    public static CMYK Cyan = new CMYK(1.0, 0.0, 0.0, 0.0);
    public static CMYK Magenta = new CMYK(0.0, 1.0, 0.0, 0.0);
    public static CMYK Yellow = new CMYK(0.0, 0.0, 1.0, 0.0);
    public static CMYK Black = new CMYK(0.0, 0.0, 0.0, 1.0);
    public static CMYK White = new CMYK(0.0, 0.0, 0.0, 0.0);

    public static CMYK All = new CMYK(1.0, 1.0, 1.0, 1.0);
    public static CMYK None = new CMYK(0.0, 0.0, 0.0, 0.0);

    public static CMYK Red = new CMYK(0.0, 1.0, 1.0, 0.0);
    public static CMYK Green = new CMYK(1.0, 0.0, 1.0, 0.0);
    public static CMYK Blue = new CMYK(1.0, 1.0, 0.0, 0.0);

    private void shiftToPositive() {
        double min = Arrays.stream(colors).min().getAsDouble();
        if(min < 0) {
            colors[cyan] += min;
            colors[magenta] += min;
            colors[yellow] += min;
            colors[black] += min;
        }
    }

    private void scale() {
        double max = Arrays.stream(colors).max().getAsDouble();
        if(max > 1) {
            colors[cyan] /= max;
            colors[magenta] /= max;
            colors[yellow] /= max;
            colors[black] /= max;
        }
    }

    private double colors[] = new double[4];
    static private final byte cyan = 0;
    static private final byte magenta = 0;
    static private final byte yellow = 0;
    static private final byte black = 0;
}
