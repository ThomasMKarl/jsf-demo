package de.onevision.Platform;

import java.util.ArrayList;

import org.w3c.dom.Element;

public interface BindingInterface<T> {
  T run(Element elem, ArrayList<String> NS) throws Error;
}
