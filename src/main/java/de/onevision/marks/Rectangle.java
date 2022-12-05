package de.onevision.marks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.math.Point;
import de.onevision.math.TransMat;
import de.onevision.color.ColorSetting;

public final class Rectangle implements Mark {
    public Point size = new Point();
    public ColorSetting colorSetting = new ColorSetting();
    private TransMat TM = TransMat.identity();

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element fillElem = (Element)elem.appendChild(doc.createElement("fill"));
        colorSetting.appendAttributes(fillElem);

        Point first = new Point();
        first = first.mult(TM);
        Point second = new Point(size.x(), size.y());
        second = second.mult(TM);
        Element rectElem = (Element)fillElem.appendChild(doc.createElement("rect"));
        rectElem.setAttribute("points", Double.toString(first.x()) + " " + Double.toString(first.y()) + " " + Double.toString(second.x()) + " " + Double.toString(second.y()));
    
        return elem;
    }

    @Override
    public void flatten() {
    }
}
