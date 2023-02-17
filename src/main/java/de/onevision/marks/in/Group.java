package de.onevision.marks.in;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.config.Size;
import de.onevision.config.Alignment;
import de.onevision.marks.Mark;
import de.onevision.math.TransMat;

public final class Group implements Mark {
    public String name = "";
    public Size size;
    public de.onevision.config.Alignment alignment = Alignment.bottomLeft;
    public Mirror mirror;
    public FormSelect formSelect;
    public ArrayList<Mark> marks = new ArrayList<Mark>();
    private TransMat TM = TransMat.identity();

    public void flatten() {
        return;
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
        return elem;
    }
}
