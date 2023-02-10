package de.onevision.Platform;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    static public Element getChild(Element elem, String name) throws Error {
        NodeList nodes = elem.getElementsByTagName(name);
        if (nodes.getLength() != 1) {
            throw new Error(errorMessage, "", "no element (or many) named " + name + " in node " + elem.getNodeName());
        }

        return (Element)nodes.item(0);
    }

    static public Object parseAttribute(Element elem, String name, Object type) throws Error {
        String attr = elem.getAttribute(name);
        if (attr.isEmpty()) {
            throw new Error(errorMessage, "", "no element named " + name + " in node " + elem.getNodeName());
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
            throw new Error(errorMessage, "", "no element (or many) named " + name + " in node " + elem.getNodeName());
        }

        Node node = nodes.item(0);
        return toType(node.getTextContent(), type);
    }

    static public ArrayList<Element> getChildren(Element elem, String name) throws Error {
        ArrayList<Element> list = new ArrayList<Element>();
        NodeList nodes = elem.getElementsByTagName(name);
        for (int index = 0; index < nodes.getLength(); ++index) {
            list.add((Element)nodes.item(index));
        }
        return list;
    }

    static public ArrayList<Object> parseElems(Element elem, String name, Object type) throws Error {
        ArrayList<Object> list = new ArrayList<Object>();
        NodeList nodes = elem.getElementsByTagName(name);
        for (int index = 0; index < nodes.getLength(); ++index) {
            Node node = nodes.item(index);
            list.add(toType(node.getTextContent(), type));
        }
        return list;
    }

    static public Optional<Object> parseElemOpt(Element elem, String name, Object type) throws Error {
        NodeList nodes = elem.getElementsByTagName(name);
        if (nodes.getLength() != 1) {
            type = Optional.empty();
        }

        Node node = nodes.item(0);
        return Optional.of(toType(node.getTextContent(), type));
    }

    static public void checkName(Element elem, String name, ArrayList<String> NS) throws Error {
        if (elem == null) {
            throw new Error(errorMessage, "", "no element");
        }

        if (NS == null || NS.isEmpty()) {
            checkName(elem, name);
            return;
        }

        boolean found = false;
        for (String prefix : NS) {
            if (elem.getNodeName().equals(prefix + ":" + name)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Error(errorMessage, "", "element name is: " + elem.getNodeName() + ", needed: " + name);
        }
    }

    static public Element getChild(Element elem, String name, ArrayList<String> NS) throws Error {
        if (NS == null || NS.isEmpty()) {
            return getChild(elem, name);
        }

        for (String prefix : NS) {
            NodeList nodes = elem.getElementsByTagName(prefix + ":" + name);
            if (nodes.getLength() == 1) {
                return (Element)nodes.item(0);
            }
        }

        throw new Error(errorMessage, "", "no element (or many) named " + name + " in node " + elem.getNodeName());
    }

    static public Object parseAttribute(Element elem, String name, ArrayList<String> NS, Object type) throws Error {
        if (NS == null || NS.isEmpty()) {
            return parseAttribute(elem, name, type);
        }

        boolean found = false;
        String attr = "";
        for (String prefix : NS) {
            attr = elem.getAttribute(prefix + ":" + name);
            if (!attr.isEmpty()) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Error(errorMessage, "", "no attribute named " + name + " in node " + elem.getNodeName());
        }

        return toType(attr, type);
    }

    static public Optional<Object> parseAttributeOpt(Element elem, String name, ArrayList<String> NS, Object type) throws Error {
        if (NS == null || NS.isEmpty()) {
            return parseAttributeOpt(elem, name, type);
        }

        boolean found = false;
        String attr = "";
        for (String prefix : NS) {
            attr = elem.getAttribute(prefix + ":" + name);
            if (!attr.isEmpty()) {
                found = true;
                break;
            }
        }
        if (!found) {
            return Optional.empty();
        }

        return Optional.of(toType(attr, type));
    }

    static public Object parseElem(Element elem, String name, ArrayList<String> NS, Object type) throws Error {
        if (NS == null || NS.isEmpty()) {
            return parseElem(elem, name, type);
        }

        boolean found = false;
        String content = "";
        for (String prefix : NS) {
            NodeList nodes = elem.getElementsByTagName(prefix + ":" + name);
            if (nodes.getLength() == 1) {
                found = true;
                content = nodes.item(0).getTextContent();
                break;
            }
        }

        if (!found) {
            throw new Error(errorMessage, "", "no element (or many) named " + name + " in node " + elem.getNodeName());
        }

        return toType(content, type);
    }

    static public ArrayList<Element> getChildren(Element elem, String name, ArrayList<String> NS) throws Error {
        if (NS == null || NS.isEmpty()) {
            return getChildren(elem, name);
        }

        ArrayList<Element> list = new ArrayList<Element>();
        for (String prefix : NS) {
            NodeList nodes = elem.getElementsByTagName(prefix + ":" + name);
            for (int index = 0; index < nodes.getLength(); ++index) {
                list.add((Element)nodes.item(index));
            }
        }
        return list;
    }
    
    static public ArrayList<Object> parseElems(Element elem, String name, ArrayList<String> NS, Object type) throws Error {
        if (NS == null || NS.isEmpty()) {
            return parseElems(elem, name, type);
        }

        ArrayList<Object> list = new ArrayList<Object>();
        for (String prefix : NS) {
            NodeList nodes = elem.getElementsByTagName(prefix + ":" + name);
            for (int index = 0; index < nodes.getLength(); ++index) {
                Node node = nodes.item(index);
                list.add(toType(node.getTextContent(), type));
            }
        }
        return list;
    }

    static public Optional<Object> parseElemOpt(Element elem, String name, ArrayList<String> NS, Object type) throws Error {
        if (NS == null || NS.isEmpty()) {
            return parseElemOpt(elem, name, type);
        }

        boolean found = false;
        String content = "";
        for (String prefix : NS) {
            NodeList nodes = elem.getElementsByTagName(prefix + ":" + name);
            if (nodes.getLength() == 1) {
                found = true;
                content = nodes.item(0).getTextContent();
                break;
            }
        }
        if (!found) {
            return Optional.empty();
        }
        
        return Optional.of(toType(content, type));
    }

    public static <T> Optional<T> parseComplexElemOpt(Element elem, String name, ArrayList<String> NS, Object type, BiFunction<Element,ArrayList<String>, T> func) {
      Element child;
      try {
          child = XmlUtil.getChild(elem, name, NS);
      }
      catch (Error e) {
          return Optional.empty();
      }

      try {
          return Optional.of(func.apply(child, NS));
      }
      catch (Exception e) {
          return Optional.empty();
      }
    }

    public static <E,T> ArrayList<T> mapAll(ArrayList<E> list, Function<E, T> func) { 
        return list.stream().map(func).collect(Collectors.toCollection(ArrayList::new)); 
    }

    static private Object toType(String in, Object type) throws Error {
        if (in == null) {
            throw new Error(errorMessage, "", "input string null");
        }

        if (type.getClass().equals(String.class)) {
            return in;
        }
        
        if(in.isEmpty()) {
            throw new Error(errorMessage, "", "input string empty");
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
