package de.onevision.color;

import org.w3c.dom.Element;

public class SpotColor {
    public boolean equals(SpotColor rhs) {
        return this.colorant.equals(rhs.colorant);
    }

    public Element appendAttributes(Element elem) {
        repr.appendAttributes(elem);
        elem.setAttribute("colorant", colorant);

        return elem;
    }

    public CMYK repr = CMYK.All;
    public String colorant = "All";
}