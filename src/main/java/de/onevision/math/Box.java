package de.onevision.math;

public class Box {
    public Box() {
        points[ll] = new Point();
        points[ul] = new Point();
        points[ur] = new Point();
        points[lr] = new Point();
    }

    public Box(Point lowerLeft, Point upperRight) {
        points[ll] = lowerLeft;
        points[ul] = new Point(lowerLeft.x(), upperRight.y());
        points[ur] = upperRight;
        points[lr] = new Point(upperRight.x(), lowerLeft.y());
    }

    public boolean equals(Box rhs) {
        if (!points[ll].equals(rhs.points[ll])) {
            return false;
        }
        if (!points[ul].equals(rhs.points[ul])) {
            return false;
        }
        if (!points[ur].equals(rhs.points[ur])) {
            return false;
        }
        if (!points[lr].equals(rhs.points[lr])) {
            return false;
        }
        return true;
    }

    public boolean exists() {
        if (points[ll].equals(points[ur])) {
            return false;
        }
        if (points[ul].equals(points[lr])) {
            return false;
        }
        return true;
    }

    public Box transform(TransMat TM) {
        points[ll] = points[ll].mult(TM);
        points[ul] = points[ul].mult(TM);
        points[ur] = points[ur].mult(TM);
        points[lr] = points[lr].mult(TM);
        
        return this;
    }

    private Point points[] = new Point[4];
    static private final byte ll = 0;
    static private final byte ul = 1;
    static private final byte ur = 2;
    static private final byte lr = 3;
}
