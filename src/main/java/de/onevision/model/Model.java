package de.onevision.model;

import java.util.ArrayList;

public class Model {
  public boolean foldingProcess = true;
  public boolean singleSided = false;
  public AssemblingType assemblingType = AssemblingType.gathering;
  public BindingType bindingType = BindingType.perfectBound;
  public boolean reversePages = false;
  public RemainderPackage remainderPackage = RemainderPackage.automatic;
  public RemainderSheet remainderSheet = RemainderSheet.automatic;
  public Optimization optimization = Optimization.combineSheets;
  public boolean blankPagesFront = false;
  public ArrayList<Layout> layouts = new ArrayList<Layout>();
}
