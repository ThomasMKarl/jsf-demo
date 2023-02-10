package de.onevision.marks.in.calculated;

import java.util.ArrayList;
import java.util.Optional;

public class CalcMarks {
  public Optional<TrimMarks> trimMarks = Optional.empty();
  public Optional<BleedMarks> bleedMarks = Optional.empty();
  public Optional<CollationMarks> collationMarks = Optional.empty();
  public Optional<MarginMarks> marginMarks = Optional.empty();
  public Optional<FoldMarks> foldMarks = Optional.empty();
  public Optional<CutMarks> cutMarks = Optional.empty();
  public ArrayList<ColorBar> colorBars = new ArrayList<ColorBar>();
  public ArrayList<ColorControl> colorControls = new ArrayList<ColorControl>();
}
