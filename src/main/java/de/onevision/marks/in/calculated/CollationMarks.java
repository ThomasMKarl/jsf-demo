package de.onevision.marks.in.calculated;

import java.util.Optional;

import de.onevision.color.SpotColor;
import de.onevision.marks.in.Shading;

public class CollationMarks {
  public double length = 0.0;
  public double thickness = 0.0;
  public Position position = new Position();
  public Direction direction = Direction.backFace;
  public Shading shading = new Shading();
  public SpotColor color = new SpotColor();
  public Optional<Double> knockoutThickness = Optional.empty();
  public Optional<Numbering> numbering = Optional.empty();
}
