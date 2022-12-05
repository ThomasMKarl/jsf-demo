package de.onevision.marks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.math.Point;
import de.onevision.math.TransMat;
import de.onevision.color.ColorSetting;

public final class Line implements Mark {
    public double length = 0.0;
    public double thickness = 0.0;
    public ColorSetting colorSetting = new ColorSetting();
    private TransMat TM = TransMat.identity();

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element strokeElem = (Element) elem.appendChild(doc.createElement("stroke"));
        strokeElem = colorSetting.appendAttributes(strokeElem);
        strokeElem.setAttribute("with", Double.toString(thickness));

        Point first = new Point();
        first = first.mult(TM);
        Point second = new Point(0, length);
        second = second.mult(TM);
        Element lineElem = (Element) strokeElem.appendChild(doc.createElement("line"));
        lineElem.setAttribute("points", Double.toString(first.x()) + " "
                + Double.toString(first.y()) + " "
                + Double.toString(second.x()) + " "
                + Double.toString(second.y()));

        return elem;
    }

    @Override
    public void flatten() {
    }
}
