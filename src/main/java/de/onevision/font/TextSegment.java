package de.onevision.font;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.color.ColorSetting;

public class TextSegment {
    public TextSegment() {
    }

    public String text = new String();
    public Font font = new Font();
    public ColorSetting colorSetting = new ColorSetting();

    public Element generateXml(Document doc, Element elem) {
        Element textElem = (Element) elem.appendChild(doc.createElement("text"));
        textElem.setAttribute("insets", "0 0 0 0");
        textElem = colorSetting.appendAttributes(textElem);
        textElem = font.appendAttributes(textElem);
        textElem.setTextContent(text);

        return elem;
    }
}
