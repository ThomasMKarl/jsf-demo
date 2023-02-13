package de.onevision.Platform;

public class XmlWriterElement {
  public XmlWriterElement(String name_, Object type_, boolean optional_) {
    name_ = name;
    optional_ = optional;
    type_ = type;
  }
  public String name = "";
  public boolean optional = false;
  public Object type;
}
