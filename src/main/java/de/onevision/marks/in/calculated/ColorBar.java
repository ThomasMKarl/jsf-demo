package de.onevision.marks.in.calculated;

import java.util.ArrayList;
import java.util.Optional;

import de.onevision.config.Alignment;
import de.onevision.config.Margin;
import de.onevision.config.Offset;

public class ColorBar {
  public boolean horizontal = false;
  public double size = 0.0;
  public double space = 0.0;
  public Alignment alignment = Alignment.bottomLeft;
  public Offset offset;
  public Margin margin;
  public double intensity = 1.0;
  public Optional<Double> knockoutThickness = Optional.empty();
  public ArrayList<IncludeColor> includeColors = new ArrayList<IncludeColor>();
  public ArrayList<String> excludeColors = new ArrayList<String>();
}
