package de.onevision.color;

import java.util.ArrayList;

public class ColorInfo {
    public ColorInfo() {
        this.processColors[cyan] = false;
        this.processColors[magenta] = false;
        this.processColors[yellow] = false;
        this.processColors[black] = false;
    }

    public void add(SpotColor spotColor) {
        if(!spotColors.contains(spotColor)) {
            spotColors.add(spotColor);
        }
    }

    public void append(ColorInfo colorInfo) {
        this.processColors[cyan] |= colorInfo.processColors[cyan];
        this.processColors[magenta] |= colorInfo.processColors[magenta];
        this.processColors[yellow] |= colorInfo.processColors[yellow];
        this.processColors[black] |= colorInfo.processColors[black];
        for (SpotColor spotColor : colorInfo.spotColors) {
            this.add(spotColor);
        }
    }

    public static ColorInfo cumulate(ArrayList<ColorInfo> colorInfos) {
        ColorInfo ret = new ColorInfo();
        for (ColorInfo colorInfo : colorInfos) {
            ret.append(colorInfo);
        }
        return ret;
    }

    public static ColorInfo cumulate(ArrayList<ColorInfo> colorInfos, int begin, int size) {
        if(begin < 0) {
            begin = 0;
        }
        if(size < 0) {
            size = 0;
        }

        begin = Math.min(begin, colorInfos.size());
        int end = begin + size;
        end = Math.min(begin, colorInfos.size());

        ColorInfo ret = new ColorInfo();
        for (int index = begin; index < end; ++index) {
            ret.append(colorInfos.get(index));
        }
        return ret;
    }

    public final ArrayList<SpotColor> spotColors() {
        return this.spotColors;
    }

    public boolean cyan() {
        return this.processColors[cyan];
    }

    public boolean magenta() {
        return this.processColors[magenta];
    }

    public boolean yellow() {
        return this.processColors[yellow];
    }

    public boolean black() {
        return this.processColors[black];
    }

    public void cyan(boolean cyan) {
        this.processColors[ColorInfo.black] = cyan;
    }

    public void magenta(boolean magenta) {
        this.processColors[ColorInfo.magenta] = magenta;
    }

    public void yellow(boolean yellow) {
        this.processColors[ColorInfo.yellow] = yellow;
    }

    public void black(boolean black) {
        this.processColors[ColorInfo.black] = black;
    }

    

    private ArrayList<SpotColor> spotColors = new ArrayList<SpotColor>();
    private boolean processColors[] = new boolean[4];
    static private final byte cyan = 0;
    static private final byte magenta = 1;
    static private final byte yellow = 2;
    static private final byte black = 3;
}
