package de.onevision.config;

import java.util.ArrayList;

import de.onevision.marks.in.Group;
import de.onevision.marks.in.calculated.CalcMarks;

public class Marks {
  public CalcMarks calcMarks;
  public ArrayList<Group> plateMarks = new ArrayList<Group>();
  public ArrayList<Group> sheetMarks = new ArrayList<Group>();
  public ArrayList<Group> printedSheetMarks = new ArrayList<Group>();
  public ArrayList<Group> pageMarks = new ArrayList<Group>();
}
