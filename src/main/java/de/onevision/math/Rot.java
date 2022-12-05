package de.onevision.math;

public class Rot {
    public Rot(double rotation) {
        mRotation = rotation;
        
        while(mRotation >= 360) {
            mRotation -= 360;
        }

        while(mRotation < 0) {
            mRotation += 360;
        }
    }

    static public Rot CW() {
        Rot rot = new Rot();
        rot.mRotation = 90;
        return rot;
    }

    static public Rot CCW() {
        Rot rot = new Rot();
        rot.mRotation = 270;
        return rot;
    }

    static public Rot Up() {
        return new Rot();
    }

    static public Rot Down() {
        Rot rot = new Rot();
        rot.mRotation = 180;
        return rot;
    }

    public double value() {
        return mRotation;
    }

    public Rot add(Rot rhs) {
        this.mRotation += rhs.mRotation;
        
        while(mRotation >= 360) {
            mRotation -= 360;
        }

        return this;
    }

    public Rot sub(Rot rhs) {
        this.mRotation -= rhs.mRotation;
        
        while(mRotation < 0) {
            mRotation += 360;
        }

        return this;
    }

    private Rot() {}

    private double mRotation = 0;
}
