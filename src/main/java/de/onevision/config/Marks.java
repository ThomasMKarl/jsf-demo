package de.onevision.config;

import java.util.Optional;

import de.onevision.marks.in.calculated.CalcMarks;

public class Marks {
  public Optional<CalcMarks> calcMarks = Optional.empty();
  public Optional<PlateMarks> plateMarks = Optional.empty();
  public Optional<PrintedSheetMarks> printedSheetMarks = Optional.empty();
  public Optional<SheetMarks> sheetMarks = Optional.empty();
  public Optional<PageMarks> pageMarks = Optional.empty();
}
