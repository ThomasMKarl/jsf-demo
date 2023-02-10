package de.onevision.marks.in.calculated;

import java.util.Optional;

import de.onevision.color.SpotColor;
import de.onevision.marks.in.LineAppearance;

public class MarginMarks {
  public LineAppearance lineAppearance;
  public boolean activateCrosses = false;
  public SpotColor color;
  public Optional<Double> knockoutThickness = Optional.empty();
}
