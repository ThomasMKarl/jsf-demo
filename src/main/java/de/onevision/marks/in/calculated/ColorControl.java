package de.onevision.marks.in.calculated;

import java.util.ArrayList;
import java.util.Optional;

import de.onevision.config.Alignment;
import de.onevision.config.Margin;
import de.onevision.config.Offset;
import de.onevision.config.Size;
import de.onevision.config.Space;
import de.onevision.marks.in.Shading;

public class ColorControl {
  public boolean horizontal = false;
  public Size size;
  public Space space;
  public Alignment alignment = Alignment.bottomLeft;
  public Offset offset;
  public Margin margin;
  public Shading shading;
  public Optional<Double> knockoutThickness = Optional.empty();
  public ArrayList<IncludeColor> includeColors = new ArrayList<IncludeColor>();
  public ArrayList<String> excludeColors = new ArrayList<String>();
}
