package de.onevision.math;

import de.onevision.Platform.Exceptions.Error;

import org.w3c.dom.Element;

public class TransMat {
    public enum Index {
        i11, i12, i13, i21, i22, i23
    }

    public static TransMat identity() {
        TransMat TM = new TransMat();
        TM.elements[i11] = 1;
        TM.elements[i12] = 0;
        TM.elements[i13] = 0;

        TM.elements[i21] = 0;
        TM.elements[i22] = 1;
        TM.elements[i23] = 0;

        return TM;
    }

    public static TransMat fromString(String values) throws Error {
        String[] split = values.split(" ");
        if (split.length != 6) {
            throw new Error("internal error", "", "cannot create matrix from string");
        }

        TransMat TM = new TransMat();
        TM.elements[i11] = Double.parseDouble(split[0]);
        TM.elements[i12] = Double.parseDouble(split[2]);
        TM.elements[i13] = Double.parseDouble(split[4]);

        TM.elements[i21] = Double.parseDouble(split[1]);
        TM.elements[i22] = Double.parseDouble(split[3]);
        TM.elements[i23] = Double.parseDouble(split[5]);
        return TM;
    }

    public String toString() {
        return this.elements[i11] + " " +
        this.elements[i21] + " " +
        this.elements[i12] + " " +
        this.elements[i22] + " " +
        this.elements[i13] + " " +
        this.elements[i23];
    }

    public TransMat translate(Point shift) {
        this.elements[i13] += shift.x();
        this.elements[i23] += shift.y();

        return this;
    }

    public TransMat rotate(Rot rot) {
        double sin = Math.sin((360.0 - rot.value()) * Constants.toRad);
        double cos = Math.cos((360.0 - rot.value()) * Constants.toRad);

        double help = this.elements[i11] * cos + this.elements[i12] * sin;
        this.elements[i12] = -this.elements[i11] * sin + this.elements[i12] * cos;
        this.elements[i11] = help;

        help = this.elements[i21] * cos + this.elements[i22] * sin;
        this.elements[i22] = -this.elements[i21] * sin + this.elements[i22] * cos;
        this.elements[i22] = help;

        return this;
    }

    public TransMat translateCenter(Point center, Rot rot) {
        double sin = Math.sin((360.0 - rot.value()) * Constants.toRad);
        double cos = Math.cos((360.0 - rot.value()) * Constants.toRad);
        double x = (1 - cos) * center.x() + sin * center.y();
        double y = (1 - cos) * center.y() - sin * center.x();

        double help = this.elements[i11] * cos + this.elements[i12] * sin;
        this.elements[i12] = -this.elements[i11] * sin + this.elements[i12] * cos;

        double help2 = this.elements[i21] * cos + this.elements[i22] * sin;
        this.elements[i22] = -this.elements[i21] * sin + this.elements[i22] * cos;

        this.elements[i13] += this.elements[i11] * x + this.elements[i12] * y;
        this.elements[i23] += this.elements[i21] * x + this.elements[i22] * y;

        this.elements[i11] = help;
        this.elements[i21] = help2;

        return this;
    }

    public TransMat scale(double xScale, double yScale) {
        this.elements[i11] *= xScale;
        this.elements[i12] *= yScale;

        this.elements[i21] *= xScale;
        this.elements[i22] *= yScale;

        return this;
    }

    public TransMat skewX(Rot xRot) {
        double tan = Math.tan((360.0 - xRot.value()) * Constants.toRad);

        this.elements[i11] += tan * this.elements[i21];
        this.elements[i12] += tan * this.elements[i22];
        this.elements[i13] += tan * this.elements[i23];

        return this;
    }

    public TransMat skewY(Rot yRot) {
        double tan = Math.tan((360.0 - yRot.value()) * Constants.toRad);

        this.elements[i21] += tan * this.elements[i11];
        this.elements[i22] += tan * this.elements[i12];
        this.elements[i23] += tan * this.elements[i13];

        return this;
    }

    public TransMat mult(TransMat rhs) {
        double help = this.elements[i11] * rhs.elements[i11] + this.elements[i12] * rhs.elements[i21];
        this.elements[i12] = this.elements[i11] * rhs.elements[i12] + this.elements[i12] * rhs.elements[i22];

        double help2 = this.elements[i21] * rhs.elements[i11] + this.elements[i22] * rhs.elements[i21];
        this.elements[i22] = this.elements[i21] * rhs.elements[i12] + this.elements[i22] * rhs.elements[i22];

        this.elements[i13] += this.elements[i11] * rhs.elements[i13] + this.elements[i12] * rhs.elements[i23];
        this.elements[i23] += this.elements[i21] * rhs.elements[i13] + this.elements[i22] * rhs.elements[i23];

        this.elements[i11] = help;
        this.elements[i21] = help2;

        return this;
    }

    public double element(Index i) {
        return this.elements[toIndex(i)];
    }

    public Element appendAttributes(Element elem) {
        elem.setAttribute("matrix", Double.toString(elements[i11]) + " "
                + Double.toString(elements[i21]) + " "
                + Double.toString(elements[i12]) + " "
                + Double.toString(elements[i22]) + " "
                + Double.toString(elements[i13]) + " "
                + Double.toString(elements[i23]));
        return elem;
    }

    private TransMat() {
    }

    private byte toIndex(Index i) {
        switch (i) {
            case i11:
                return i11;
            case i12:
                return i12;
            case i13:
                return i13;
            case i21:
                return i21;
            case i22:
                return i22;
            case i23:
                return i23;
            default:
                return -1;
        }
    }

    private double elements[] = new double[6];
    static private final byte i11 = 0;
    static private final byte i21 = 1;
    static private final byte i12 = 2;
    static private final byte i22 = 3;
    static private final byte i13 = 4;
    static private final byte i23 = 5;
}
