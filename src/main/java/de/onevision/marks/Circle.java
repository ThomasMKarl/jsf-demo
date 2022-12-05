package de.onevision.marks;

import de.onevision.math.TransMat;
import de.onevision.color.ColorSetting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class Circle implements Mark {
    public double thickness = 0;
    public double radius = 0;
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

        Element circleElement = (Element) strokeElem.appendChild(doc.createElement("circle"));
        circleElement.setAttribute("points", Double.toString(TM.element(TransMat.Index.i13)) + " "
                + Double.toString(TM.element(TransMat.Index.i23)) + " "
                + Double.toString(radius));
        return elem;
    }

    @Override
    public void flatten() {
    }
}
