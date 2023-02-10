package de.onevision.marks.in.calculated;

import java.util.Optional;

import de.onevision.color.SpotColor;
import de.onevision.marks.in.LineAppearance;

public class BleedMarks {
  public LineAppearance lineAppearance;
  public boolean printInner = false;
  public SpotColor color;
  public Optional<Double> knockoutThickness = Optional.empty();
}
