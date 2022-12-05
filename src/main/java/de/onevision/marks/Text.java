package de.onevision.marks;

import de.onevision.math.TransMat;
import de.onevision.font.TextSegment;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class Text implements Mark {
    private double knockoutStrength = 0;
    private TransMat TM = TransMat.identity();

    public Text() {
    }

    public void add(TextSegment seg) {
        segments.add(seg);
    }

    public int numberOfElements() {
        return segments.size();
    }

    public final ArrayList<TextSegment> segments() {
        return segments;
    }

    private ArrayList<TextSegment> segments = new ArrayList<TextSegment>();

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element containerElem = (Element)elem.appendChild(doc.createElement("container"));
        containerElem = TM.appendAttributes(containerElem);
        containerElem.setAttribute("layout", "flow");
        containerElem.setAttribute("knockout", Double.toString(knockoutStrength));

        for(TextSegment segment : segments) {
            containerElem = segment.generateXml(doc, containerElem);
        }

        return elem;
    }

    @Override
    public void flatten() {
    }
}
