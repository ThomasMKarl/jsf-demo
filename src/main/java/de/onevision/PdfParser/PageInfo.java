package de.onevision.PdfParser;

import de.onevision.color.ColorInfo;
import de.onevision.math.Box;
import de.onevision.math.Rot;

enum Type {
    trim, media, bleed, art
}

public class PageInfo {
    public PageInfo() {
        boxes[toIndex(Type.trim)] = new Box();
        boxes[toIndex(Type.media)] = new Box();
        boxes[toIndex(Type.bleed)] = new Box();
        boxes[toIndex(Type.art)] = new Box();
    }

    public int localIndex = 0;
    public int globalIndex = 0;
    public Rot rot = Rot.Up();
    public ColorInfo colorInfo = new ColorInfo();

    public Box box(Type type) {
        return boxes[toIndex(type)];
    }

    public void box(Type type, Box box) {
        boxes[toIndex(type)] = box;
    }

    static private final byte trim = 0;
    static private final byte media = 1;
    static private final byte bleed = 2;
    static private final byte art = 3;
    private byte toIndex(Type type) {
        switch(type){
            case trim:
                return trim;
            case media:
                return media;
            case bleed:
                return bleed;
            case art:
                return art;
            default:
                return -1;
        }
    }

    private Box boxes[] = new Box[4];
}
