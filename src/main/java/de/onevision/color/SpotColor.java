package de.onevision.color;

import de.onevision.math.FloatCompare;
import org.w3c.dom.Element;

public class SpotColor {
    public SpotColor() {
    }

    public SpotColor(CMYK color, String colorant, double shading, boolean overprint) {
        this.color = color;
        this.name = colorant;
        this.intensity = shading;
        this.overprint = overprint;
    }

    public boolean equals(SpotColor rhs) {
        if (!this.color.equals(rhs.color)) {
            return false;
        }
        if (!this.name.equals(rhs.name)) {
            return false;
        }
        if (FloatCompare.neq(this.intensity, rhs.intensity)) {
            return false;
        }
        if (this.overprint != rhs.overprint) {
            return false;
        }
        return true;
    }

    public Element appendAttributes(Element elem) {
        elem = color.appendAttributes(elem);
        elem.setAttribute("colorant", name);
        elem.setAttribute("tint", Double.toString(intensity));
        if (overprint) {
            elem.setAttribute("overprint", "true");
        }
        else {
            elem.setAttribute("overprint", "false");
        }
        return elem;
    }

    public CMYK color = CMYK.All;
    public String name = "All";
    public double intensity = 1;
    public boolean overprint = false;
}
