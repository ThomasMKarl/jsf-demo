package de.onevision.color;

import de.onevision.math.FloatCompare;
import org.w3c.dom.Element;

public class ColorSetting {
    public ColorSetting() {
    }

    public ColorSetting(CMYK color, String colorant, double shading, boolean overprint) {
        this.color = color;
        this.colorant = colorant;
        this.shading = shading;
        this.overprint = overprint;
    }

    public boolean equals(ColorSetting rhs) {
        if (!this.color.equals(rhs.color)) {
            return false;
        }
        if (!this.colorant.equals(rhs.colorant)) {
            return false;
        }
        if (FloatCompare.neq(this.shading, rhs.shading)) {
            return false;
        }
        if (this.overprint != rhs.overprint) {
            return false;
        }
        return true;
    }

    public Element appendAttributes(Element elem) {
        elem = color.appendAttributes(elem);
        elem.setAttribute("colorant", colorant);
        elem.setAttribute("tint", Double.toString(shading));
        if (overprint) {
            elem.setAttribute("overprint", "true");
        }
        else {
            elem.setAttribute("overprint", "false");
        }
        return elem;
    }

    public CMYK color = CMYK.All;
    public String colorant = "All";
    public double shading = 1;
    public boolean overprint = false;
}
