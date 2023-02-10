package de.onevision.marks.out;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.marks.Mark;
import de.onevision.math.TransMat;

public final class Group implements Mark {
    public void add(Mark mark) {
        elements.add(mark);
    }

    public void flatten() {
        if (flat) {
            return;
        }

        flat = true;

        for (Mark mark : elements) {
            mark.transform(TM);
            mark.flatten();
        }

        TM = TransMat.identity();
    }

    public final ArrayList<Mark> elements() {
        return elements;
    }

    public boolean isFlat() {
        return flat;
    }

    public int numberOfElements() {
        return elements.size();
    }

    private boolean flat = false;
    private ArrayList<Mark> elements = new ArrayList<Mark>();
    private TransMat TM = TransMat.identity();

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        if (!isFlat()) {
            return elem;
        }

        for (Mark mark : elements) {
            elem = mark.generateXml(doc, elem);
        }

        return elem;
    }
}
