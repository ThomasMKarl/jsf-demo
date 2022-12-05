package de.onevision.Platform;

import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.onevision.Platform.Exceptions.Error;

public class XmlUtil {
    static public void checkNumAttributes(Element elem, int count) throws Error {
        NamedNodeMap attr = elem.getAttributes();
        if (attr.getLength() != count) {
            throw new Error(errorMessage, "",
                    "got  " + Integer.toString(attr.getLength()) + " elements, needed " + Integer.toString(count));
        }
    }

    static public void checkNumAttributes(Element elem, int min, int max) throws Error {
        NamedNodeMap attr = elem.getAttributes();
        if (attr.getLength() < min || attr.getLength() > max) {
            throw new Error(errorMessage, "",
                    "got  " + Integer.toString(attr.getLength()) + " elements, needed minimim " + Integer.toString(min) + ", maximum " + Integer.toString(max));
        }
    }

    static public void checkName(Element elem, String name) throws Error {
        if (elem == null) {
            throw new Error(errorMessage, "", "no element");
        }

        if (!elem.getNodeName().equals(name)) {
            throw new Error(errorMessage, "", "element name is: " + elem.getNodeName() + ", needed: " + name);
        }
    }

    static public Object parseAttribute(Element elem, String name, Object type) throws Error {
        String attr = elem.getAttribute(name);
        if (attr.isEmpty()) {
            throw new Error(errorMessage, "", "no element named " + name);
        }

        return toType(attr, type);
    }

    static public Optional<Object> parseAttributeOpt(Element elem, String name, Object type) throws Error {
        String attr = elem.getAttribute(name);
        if (attr.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toType(attr, type));
    }

    static public Object parseElem(Element elem, String name, Object type) throws Error {
        NodeList nodes = elem.getElementsByTagName(name);
        if (nodes.getLength() != 1) {
            throw new Error(errorMessage, "", "no element (or many) named " + name);
        }

        Node node = nodes.item(0);
        return toType(node.getTextContent(), type);
    }

    static public Optional<Object> parseElemOpt(Element elem, String name, Object type) throws Error {
        NodeList nodes = elem.getElementsByTagName(name);
        if (nodes.getLength() != 1) {
            type = Optional.empty();
        }

        Node node = nodes.item(0);
        return Optional.of(toType(node.getTextContent(), type));
    }

    static private Object toType(String in, Object type) throws Error {
        if (in == null || in.isEmpty()) {
            throw new Error(errorMessage, "", "input string empty or null");
        }

        try {
            if (type.getClass().equals(Double.class)) {
                return Double.parseDouble(in);
            } else if (type.getClass().equals(Float.class)) {
                return Float.parseFloat(in);
            } else if (type.getClass().equals(Byte.class)) {
                return Byte.parseByte(in);
            } else if (type.getClass().equals(Short.class)) {
                return Short.parseShort(in);
            } else if (type.getClass().equals(Integer.class)) {
                return Integer.parseInt(in);
            } else if (type.getClass().equals(String.class)) {
                return in;
            } else if (type.getClass().equals(Boolean.class)) {
                if (in.equals("true")) {
                    return true;
                }
                return false;
            }
        } catch (NumberFormatException e) {
            throw new Error(errorMessage, "", "no parsing option defined for type " + type.getClass().getName());
        }

        throw new Error(errorMessage, "", "no parsing option defined for type " + type.getClass().getName());
    }

    static private String errorMessage = "parsing error";
}
