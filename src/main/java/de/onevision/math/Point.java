package de.onevision.math;

import java.lang.Math;

public class Point {
    public Point() {}

    public Point(double x, double y) {
        mX = x;
        mY = y;
    }

    public Point add(Point rhs) {
        this.mX += rhs.mX;
        this.mY += rhs.mY;
        return this;
    }

    public Point sub(Point rhs) {
        this.mX -= rhs.mX;
        this.mY -= rhs.mY;
        return this;
    }

    public Point scale(double factor) {
        this.mX *= factor;
        this.mY *= factor;
        return this;
    }

    public boolean equals(Point rhs) {
        if (FloatCompare.neq(mX, rhs.mX)) {
            return false;
        }
        if (FloatCompare.neq(mY, rhs.mY)) {
            return false;
        }
        return true;
    }

    public double length() {
        return Math.sqrt(this.mX * this.mX + this.mY * this.mY);
    }

    public double distance(Point rhs) {
        double xdist = rhs.mX - this.mX;
        double ydist = rhs.mY - this.mY;
        return Math.sqrt(xdist * xdist + ydist * ydist);
    }

    public Point mult(TransMat TM) {
        double help = this.mX;
        this.mX = this.mX * TM.element(TransMat.Index.i11) + this.mY * TM.element(TransMat.Index.i12) + TM.element(TransMat.Index.i13);
        this.mY = help * TM.element(TransMat.Index.i21) + this.mY * TM.element(TransMat.Index.i22) + TM.element(TransMat.Index.i23);
        return this;
    }

    public double x() {
        return mX;
    }

    public double y() {
        return mY;
    }

    public void x(double x) {
        mX = x;
    }

    public void y(double y) {
        mY = y;
    }

    private double mX = 0;
    private double mY = 0;
}
