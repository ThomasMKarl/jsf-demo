package de.onevision.Platform;

import java.nio.file.Path;
import java.util.ArrayList;

public class ExportConfig {
  public Path tempFolder;
  public boolean exportTarget = false;
  public boolean exportModel = false;
  public ArrayList<Integer> exportDeviceParamsIndexList = new ArrayList<Integer>();
  public ArrayList<Integer> exportMarksIndexList = new ArrayList<Integer>();
}
