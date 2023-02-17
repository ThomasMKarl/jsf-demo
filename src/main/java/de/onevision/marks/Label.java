package de.onevision.marks;

import de.onevision.math.TransMat;
import de.onevision.font.Segment;

import java.util.ArrayList;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class Label implements Mark {
    public Optional<Double> knockoutThickness = Optional.empty();
    public ArrayList<Segment> segments = new ArrayList<Segment>();
    private TransMat TM = TransMat.identity();

    public Label() {
    }

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public final TransMat getTM() {
        return TM;
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element containerElem = (Element)elem.appendChild(doc.createElement("container"));
        containerElem = TM.appendAttributes(containerElem);
        containerElem.setAttribute("layout", "flow");
        if (!knockoutThickness.isEmpty()) {
            containerElem.setAttribute("knockout", knockoutThickness.get().toString());
        }
        else {
            containerElem.setAttribute("knockout", "0");
        }

        for(Segment segment : segments) {
            containerElem = segment.generateXml(doc, containerElem);
        }

        return elem;
    }

    @Override
    public void flatten() {
    }
}
