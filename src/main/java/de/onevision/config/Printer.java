package de.onevision.config;

import java.util.ArrayList;

public class Printer {
  public String name = "";
  public boolean digital = false;
  public Format format;
  public double paperEdge;
  public double gripperMargin;
  public ArrayList<Plate> plates = new ArrayList<Plate>();
}
