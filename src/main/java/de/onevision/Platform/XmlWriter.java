package de.onevision.Platform;

import de.onevision.Platform.Exceptions.Error;
import de.onevision.color.CMYK;
import de.onevision.math.TransMat;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;

public class XmlWriter {
  public XmlWriter(String rootName, XmlWriter appendTo) throws Error {
    doc = appendTo.doc;
    rootNode = doc.createElement(rootName);
    appendTo.rootNode.appendChild(rootNode);
    transformer = appendTo.transformer;
  }

  public XmlWriter(String rootName) throws Error {
    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder;
    try {
      documentBuilder = documentFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new Error("internal error", "", "ParserConfigurationException");
    }
    doc = documentBuilder.newDocument();
    rootNode = doc.createElement(rootName);
    doc.appendChild(rootNode);

    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", 2);
      transformer = transformerFactory.newTransformer();
    } catch (TransformerFactoryConfigurationError e) {
      throw new Error("internal error", "", "TransformerFactoryConfigurationError");
    } catch (TransformerConfigurationException e) {
      throw new Error("inernal error", "", "TransformerConfigurationException");
    }

    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
  }

  public void setVersion(int majorVersion, int minorVersion) {
    rootNode.setAttribute("ic:majorVersion", ((Integer)majorVersion).toString());
    rootNode.setAttribute("ic:minorVersion", ((Integer)minorVersion).toString());
  }

  public void addNS(String shortName, String url) {
    rootNode.setAttribute("xmlns:" + shortName, url);
  }

  public void addSchemaLocation(String url, Path path) {
    rootNode.setAttribute("xsi:schemaLocation", url + " " + path.toString());
  }

  public <T> Node write(T value, String name) {
    return write(value, name, null, 0);
  }

  @SuppressWarnings("unchecked")
  private <T> Node write(T value, String name, Node toAppend, int level) {
    if (!NS.isEmpty()) {
      if(level < NS.size()) {
        name = NS.get(level) + ":" + name;
      }
      else {
        name = NS.get(NS.size() - 1) + ":" + name;
      }
    }

    Element child = doc.createElement(name);

    Class<?> type = value.getClass();

    Node valueNode = null;
    if (type.equals(String.class)) {
      valueNode = doc.createTextNode((String)value);
    } else if (type.equals(Double.class)) {
      valueNode = doc.createTextNode(((Double)value).toString());
    } else if (type.equals(Float.class)) {
      valueNode = doc.createTextNode(((Float)value).toString());
    } else if (type.equals(Byte.class)) {
      valueNode = doc.createTextNode(((Byte)value).toString());
    } else if (type.equals(Short.class)) {
      valueNode = doc.createTextNode(((Short)value).toString());
    } else if (type.equals(Integer.class)) {
      valueNode = doc.createTextNode(((Integer)value).toString());
    } else if (type.equals(Boolean.class)) {
        if ((Boolean)value) {
          valueNode = doc.createTextNode("true");
        }
        valueNode = doc.createTextNode("false");
    } else if (type.isEnum()) {
      valueNode = doc.createTextNode(value.toString());
    } else if (type.equals(CMYK.class)) {
      valueNode = write(((CMYK)value).cyan(), "cyan", child, level + 1);
      valueNode = write(((CMYK)value).magenta(), "magenta", child, level + 1);
      valueNode = write(((CMYK)value).yellow(), "yellow", child, level + 1);
      valueNode = write(((CMYK)value).black(), "black", child, level + 1);
    } else if (type.equals(TransMat.class)) {
      child.setAttribute("ic:value", ((TransMat)value).toString());
      valueNode = doc.createTextNode("");
    } else {
      Field[] allFields = type.getDeclaredFields();
      for (Field field : allFields) {
        try {
          if (field.getType().equals(Optional.class)) {
            if (((Optional<Object>)field.get(value)).isPresent()) {
              Class<?> inner = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
              valueNode = write(inner.cast(((Optional<Object>)field.get(value)).get()), field.getName(), child, level);
            }
          } else if (field.getType().equals(ArrayList.class)) {
            Class<?> inner = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
            for (Object elem : (ArrayList<Object>)field.get(value)) {
              String pluralName = field.getName(); // ignore last 's'
              valueNode = write(inner.cast(elem), pluralName.substring(0, pluralName.length() - 1), child, level);
            }
          } else if (field.getType().equals(TransMat.class)) {
            field.setAccessible(true);
            valueNode = write((TransMat)field.get(value), "matrix", child, level + 1);
          } else if (field.getName().equals("pathToFile")) {
            field.setAccessible(true);
            valueNode = write(((Path)field.get(value)).toString(), "path", child, level + 1);
          } else {
            valueNode = write((T)field.get(value), field.getName(), child, level + 1);
          }
        }
        catch (IllegalArgumentException e) {}
        catch (IllegalAccessException e) {}
      }
    }

    if (valueNode != null) 
    {
      child.appendChild(valueNode);
      if (toAppend == null) {
        rootNode.appendChild(child);
      }
      else {
        toAppend.appendChild(child);
      }
    }

    return valueNode;
  }

  public void writeToFile(Path outputPath) throws Error {
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File(outputPath.toString()));
    try {
        transformer.transform(source, result);
    } catch (TransformerException e) {
        throw new Error("internal error", "", "TransformerException");
    }
  }

  public String writeToString() throws Error {
    DOMSource source = new DOMSource(doc);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    try {
        transformer.transform(source, result);
    } catch (TransformerException e) {
        throw new Error("internal error", "", "TransformerException");
    }
    return writer.getBuffer().toString();
  }

  public void setNS(ArrayList<String> NS_) {
    NS = NS_;
  }

  public Element rootNode() {
    return rootNode;
  }

  public Document document() {
    return doc;
  }

  public void appendChild(Element childNode) {
    rootNode.appendChild(childNode);
  }

  private ArrayList<String> NS = new ArrayList<String>();
  private Document doc = null;
  private Element rootNode = null;
  private Transformer transformer = null;
}
