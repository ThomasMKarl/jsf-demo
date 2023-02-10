package de.onevision.font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.color.SpotColor;

public class Segment {
    public Segment() {
    }

    public String text = new String();
    public Font font = new Font();
    public SpotColor spotColor = new SpotColor();

    public Element generateXml(Document doc, Element elem) {
        Element textElem = (Element) elem.appendChild(doc.createElement("text"));
        textElem.setAttribute("insets", "0 0 0 0");
        textElem = spotColor.appendAttributes(textElem);
        textElem = font.appendAttributes(textElem);
        textElem.setTextContent(text);

        return elem;
    }
}
