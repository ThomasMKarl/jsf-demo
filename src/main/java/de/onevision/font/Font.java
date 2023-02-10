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

    public String name = new String();
    public String style = new String();
    public double size = 10.0;
}
