package de.onevision.Platform;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class XmlWriter {

  public XmlWriter() throws ParserConfigurationException {
    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
    doc = documentBuilder.newDocument();
  }
  public void add(String name, Object type, boolean optional) {
    XmlWriterElement elem = new XmlWriterElement(name, type, optional);
    elements.add(elem);
  }

  public void write(Node node, Object value) {
    Element child = doc.createElement(elements.get(currentIndex).name);

    if (value.getClass() == elements.get(currentIndex).type) {
      int x = 5;
    }
    child.appendChild(toNode(value));

    node.appendChild(child);
    ++currentIndex;
  }

  private int currentIndex = 0;
  private ArrayList<XmlWriterElement> elements = new ArrayList<XmlWriterElement>();

  private <T> Node toNode(Object value) {
    if (value.getClass() == Integer.class) {
      return doc.createTextNode(((Integer)value).toString());
    }
    else return doc.createTextNode("");
  }

  private Document doc = null;
}
