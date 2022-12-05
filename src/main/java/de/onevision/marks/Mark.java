package de.onevision.marks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.math.TransMat;

public interface Mark {
    public void transform(TransMat TM);

    public void flatten();

    public Element generateXml(Document doc, Element elem);
}