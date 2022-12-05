package de.onevision.font;

import org.w3c.dom.Element;

public class Font {
    public Font() {
    }

    public Font(String name, String style, double size) {
        this.name = name;
        this.style = style;
        this.size = size;
    }

    public Element appendAttributes(Element elem) {
        elem.setAttribute("name", name);
        elem.setAttribute("style", style);
        elem.setAttribute("size", Double.toString(size));

        return elem;
    }

    public String name() {
        return name;
    }

    public String style() {
        return style;
    }

    public double size() {
        return size;
    }

    public void name(String name) {
        this.name = name;
    }

    public void style(String style) {
        this.style = style;
    }

    public void size(double size) {
        this.size = size;
    }

    private String name = new String();
    private String style = new String();
    private double size = 0;
}
