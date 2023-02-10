package de.onevision.imposeConfigParser;

import de.onevision.Platform.XmlUtil;
import de.onevision.Platform.Exceptions.Error;
import de.onevision.color.CMYK;
import de.onevision.color.SpotColor;
import de.onevision.config.*;
import de.onevision.font.Font;
import de.onevision.font.Segment;
import de.onevision.marks.in.calculated.*;
import de.onevision.marks.in.*;
import de.onevision.marks.*;
import de.onevision.math.Point;
import de.onevision.math.TransMat;
import de.onevision.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ImposeConfigParser {
  public ImposeConfigParser(Path inputFolder, String filename) throws Error {
    NS.add("ic");
    NS.add("ict");
    NS.add("ipt");
    NS.add("ima");
    NS.add("imo");

    Path targetPath = inputFolder.resolve(filename + "_Target.xml");
    if (Files.exists(targetPath)) {
      targetRoot = parse(targetPath);
    }

    Path modelPath = inputFolder.resolve(filename + "_Model.xml");
    if (Files.exists(modelPath)) {
      modelRoot = parse(modelPath);
    }

    Integer flat = 0;
    while (true) {
      Path deviceParamsPath = inputFolder.resolve(filename + "_DeviceParams_" + flat.toString() + ".xml");
      if (Files.exists(deviceParamsPath)) {
        deviceParamsRoots.add(parse(deviceParamsPath));
      }
      else {
        break;
      }

      Path marksPath = inputFolder.resolve(filename + "_Marks_" + flat.toString() + ".xml");
      if (Files.exists(marksPath)) {
        marksRoots.add(parse(marksPath));
      }
      else {
        marksRoots.add(null);
      }

      ++flat;
    }
  }

  private Element targetRoot;
  private Element modelRoot;
  private ArrayList<Element> deviceParamsRoots = new ArrayList<Element>();
  private ArrayList<Element> marksRoots = new ArrayList<Element>();

  private ArrayList<String> NS = new ArrayList<String>();
  
  public ImposeConfig createImposeConfig() throws Error {
    ImposeConfig imposeConfig = new ImposeConfig();

    imposeConfig.pagePos = getPagePos(XmlUtil.getChild(targetRoot, "pagePos", NS), NS);
    imposeConfig.output = getOutput(XmlUtil.getChild(targetRoot, "output", NS), NS);
    try {
      imposeConfig.stepAndRepeat = Optional.of(getStepAndRepeat(XmlUtil.getChild(targetRoot, "stepAndRepeat", NS), NS));
    }
    catch (Error e) {
      imposeConfig.stepAndRepeat = Optional.empty();
    }

    imposeConfig.model = getModel(XmlUtil.getChild(modelRoot, "model", NS), NS);

    int index = 0;
    for(Element deviceParamsRoot : deviceParamsRoots) {
      Flat flat = new Flat();
      flat.referenceLayout = Optional.of(index);
      flat.deviceParameters = getDeviceParams(XmlUtil.getChild(deviceParamsRoot, "deviceParameters", NS), NS);
      
      Element marksRoot = null;
      try {
        marksRoot = marksRoots.get(index);
      }
      catch (IndexOutOfBoundsException e) {}

      if(marksRoot != null) {
        flat.marks = getMarks(XmlUtil.getChild(marksRoot, "marks", NS), NS);
      }
  
      imposeConfig.flats.add(flat);
    }

    return imposeConfig;
  }

  public static Size getSize(Element node, ArrayList<String> NS) throws Error {
    Size size = new Size();
    //XmlUtil.checkName(node, "size", NS);
    //size.height = (Double)XmlUtil.parseElem(node, "height", NS, size.height);
    //size.width = (Double)XmlUtil.parseElem(node, "width", NS, size.width);
    return size;
  }

  public static Box getBox(Element node, ArrayList<String> NS) throws Error {
    Box box = new Box();
    XmlUtil.checkName(node, "box", NS);
    box.height = (Double)XmlUtil.parseElem(node, "height", NS, box.height);
    box.width = (Double)XmlUtil.parseElem(node, "width", NS, box.width);
    return box;
  }

  public static Scale getScale(Element node, ArrayList<String> NS) throws Error {
    Scale scale = new Scale();
    XmlUtil.checkName(node, "scale", NS);
    scale.height = (Double)XmlUtil.parseElem(node, "height", NS, scale.height);
    scale.width = (Double)XmlUtil.parseElem(node, "width", NS, scale.width);
    return scale;
  }

  public static Offset getOffset(Element node, ArrayList<String> NS) throws Error {
    Offset offset = new Offset();
    offset.height = (Double)XmlUtil.parseElem(node, "height", NS, offset.height);
    offset.width = (Double)XmlUtil.parseElem(node, "width", NS, offset.width);
    return offset;
  }

  public static Margin getMargin(Element node, ArrayList<String> NS) throws Error {
    Margin margin = new Margin();
    margin.x = (Double)XmlUtil.parseElem(node, "x", NS, margin.x);
    margin.y = (Double)XmlUtil.parseElem(node, "y", NS, margin.y);
    return margin;
  }

  public static Space getSpace(Element node, ArrayList<String> NS) throws Error {
    Space space = new Space();
    space.x = (Double)XmlUtil.parseElem(node, "x", NS, space.x);
    space.y = (Double)XmlUtil.parseElem(node, "y", NS, space.y);
    return space;
  }

  public static Format getFormat(Element node, ArrayList<String> NS) throws Error {
    Format format = new Format();
    XmlUtil.checkName(node, "format", NS);
    format.name = (String)XmlUtil.parseElem(node, "name", NS, format.name);
    format.size = getSize(XmlUtil.getChild(node, "size", NS), NS);
    return format;
  }

  public static Alignment getAlignment(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "alignment", NS);
  
    String value = node.getTextContent();

    if (value.equals("topLeft")) {
      return Alignment.topLeft;
    }
    if (value.equals("topCenter")) {
      return Alignment.topCenter;
    }
    if (value.equals("topRight")) {
      return Alignment.topRight;
    }

    if (value.equals("centerLeft")) {
      return Alignment.centerLeft;
    }
    if (value.equals("centerCenter")) {
      return Alignment.centerCenter;
    }
    if (value.equals("centerRight")) {
      return Alignment.centerRight;
    }

    if (value.equals("bottomLeft")) {
      return Alignment.bottomLeft;
    }
    if (value.equals("bottomCenter")) {
      return Alignment.bottomCenter;
    }
    if (value.equals("bottomRight")) {
      return Alignment.bottomRight;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"alignment\"");
  }

  public static Corner getCorner(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "corner", NS);
  
    String value = node.getTextContent();

    if (value.equals("lowerLeft")) {
      return Corner.lowerLeft;
    }
    if (value.equals("upperLeft")) {
      return Corner.upperLeft;
    }
    if (value.equals("upperRight")) {
      return Corner.upperRight;
    }
    if (value.equals("lowerRight")) {
      return Corner.lowerRight;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"corner\"");
  }

  public static Rotation getRotation(Element node, ArrayList<String> NS) throws Error {
    String value = node.getTextContent();

    if (value.equals("up")) {
      return Rotation.up;
    }
    if (value.equals("cw")) {
      return Rotation.cw;
    }
    if (value.equals("down")) {
      return Rotation.down;
    }
    if (value.equals("ccw")) {
      return Rotation.ccw;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"rotation\"");
  }

  public static TransMat getMatrix(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "matrix", NS);
    String values = "";
    values = (String)XmlUtil.parseAttribute(node, null, NS, values);
    return TransMat.fromString(values);
  }

  public static CMYK getCmyk(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "cmyk", NS);

    Double cyan = 1.0;
    cyan = (Double)XmlUtil.parseElem(node, "cyan", NS, cyan);
    Double magenta = 1.0;
    magenta = (Double)XmlUtil.parseElem(node, "magenta", NS, magenta);
    Double yellow = 1.0;
    yellow = (Double)XmlUtil.parseElem(node, "yellow", NS, yellow);
    Double black = 1.0;
    black = (Double)XmlUtil.parseElem(node, "black", NS, black);

    return new CMYK(cyan, magenta, yellow, black);
  }

  public static SpotColor getSpotColor(Element node, ArrayList<String> NS) throws Error {
    SpotColor spotColor = new SpotColor();
    XmlUtil.checkName(node, "color", NS);
    spotColor.intensity = (Double)XmlUtil.parseElem(node, "intensity", NS, spotColor.intensity);
    spotColor.name = (String)XmlUtil.parseElem(node, "name", NS, spotColor.name);
    spotColor.overprint = (Boolean)XmlUtil.parseElem(node, "overprint", NS, spotColor.overprint);
    spotColor.color = getCmyk(XmlUtil.getChild(node, "cmyk", NS), NS);
    return spotColor;
  }

  public static Shading getShading(Element node, ArrayList<String> NS) throws Error {
    Shading shading = new Shading();
    XmlUtil.checkName(node, "shading", NS);
    shading.begin = (Double)XmlUtil.parseElem(node, "begin", NS, shading.begin);
    shading.step = (Double)XmlUtil.parseElem(node, "step", NS, shading.step);
    shading.end = (Double)XmlUtil.parseElem(node, "end", NS, shading.end);
    return shading;
  }

  public static Font getFont(Element node, ArrayList<String> NS) throws Error {
    Font font = new Font();
    XmlUtil.checkName(node, "font", NS);
    font.name = (String)XmlUtil.parseElem(node, "name", NS, font.name);
    font.style = (String)XmlUtil.parseElem(node, "style", NS, font.style);
    font.size = (Double)XmlUtil.parseElem(node, "size", NS, font.size);
    return font;
  }

  public static Segment getSegment(Element node, ArrayList<String> NS) throws Error {
    Segment segment = new Segment();
    XmlUtil.checkName(node, "segment", NS);
    segment.text = (String)XmlUtil.parseElem(node, "text", NS, segment.text);
    segment.spotColor = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    segment.font = getFont(XmlUtil.getChild(node, "font", NS), NS);
    return segment;
  }

  public static Sheet getSheet(Element node, ArrayList<String> NS) throws Error {
    Sheet sheet = new Sheet();
    XmlUtil.checkName(node, "sheet", NS);
    sheet.fibreHorizontal = (Boolean)XmlUtil.parseElem(node, "fibreHorizontal", NS, sheet.fibreHorizontal);
    sheet.turn = (Boolean)XmlUtil.parseElem(node, "turn", NS, sheet.turn);
    sheet.volume = (Double)XmlUtil.parseElem(node, "volume", NS, sheet.volume);
    sheet.weight = (Double)XmlUtil.parseElem(node, "volume", NS, sheet.weight);
    sheet.format = getFormat(XmlUtil.getChild(node, "format", NS), NS);
    return sheet;
  }

  public static Plate getPlate(Element node, ArrayList<String> NS) throws Error {
    Plate plate = new Plate();
    XmlUtil.checkName(node, "plate", NS);
    plate.name = (String)XmlUtil.parseElem(node, "name", NS, plate.name);
    plate.offset = getOffset(XmlUtil.getChild(node, "offset", NS), NS);
    plate.rotation = (Double)XmlUtil.parseElem(node, "rotation", NS, plate.rotation);
    return plate;
  }

  public static Printer getPrinter(Element node, ArrayList<String> NS) throws Error {
    Printer printer = new Printer();
    XmlUtil.checkName(node, "printer", NS);
    printer.digital = (Boolean)XmlUtil.parseElem(node, "digital", NS, printer.digital);
    printer.format = getFormat(XmlUtil.getChild(node, "format", NS), NS);
    printer.gripperMargin = (Double)XmlUtil.parseElem(node, "gripperMargin", NS, printer.gripperMargin);
    printer.name = (String)XmlUtil.parseElem(node, "name", NS, printer.name);
    printer.paperEdge = (Double)XmlUtil.parseElem(node, "paperEdge", NS, printer.paperEdge);
    ArrayList<Element> children = XmlUtil.getChildren(node, "plate", NS);
    for(Element child : children) {
      printer.plates.add(getPlate(child, NS));
    }
    return printer;
  }

  public static PagePos getPagePos(Element node, ArrayList<String> NS) throws Error {
    PagePos pagePos = new PagePos();
    XmlUtil.checkName(node, "pagePos", NS);
    pagePos.alignment = getAlignment(XmlUtil.getChild(node, "alignment", NS), NS);
    pagePos.format = getFormat(XmlUtil.getChild(node, "format", NS), NS);
    pagePos.offset = getOffset(XmlUtil.getChild(node, "offset", NS), NS);
    pagePos.rotationAroundAngle = (Double)XmlUtil.parseElem(node, "rotationAroundAngle", NS, pagePos.rotationAroundAngle);
    pagePos.rotationAroundSelf = getRotation(XmlUtil.getChild(node, "rotationAroundSelf", NS), NS);
    pagePos.scale = getScale(XmlUtil.getChild(node, "scale", NS), NS);
    pagePos.useMediaBox = (Boolean)XmlUtil.parseElem(node, "useMediaBox", NS, pagePos.useMediaBox);
    pagePos.bleedBack = (Double)XmlUtil.parseElem(node, "bleedBack", NS, pagePos.bleedBack);
    pagePos.bleedBottom = (Double)XmlUtil.parseElem(node, "bleedBottom", NS, pagePos.bleedBottom);
    pagePos.bleedCut = (Double)XmlUtil.parseElem(node, "bleedCut", NS, pagePos.bleedCut);
    pagePos.bleedTop = (Double)XmlUtil.parseElem(node, "bleedTop", NS, pagePos.bleedTop);
    return pagePos;
  }

  public static Collect getCollect(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "collectForms", NS);
  
    String value = node.getTextContent();

    if (value.equals("all")) {
      return Collect.all;
    }
    if (value.equals("frontBack")) {
      return Collect.frontBack;
    }
    if (value.equals("none")) {
      return Collect.none;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"collect\"");
  }

  public static Output getOutput(Element node, ArrayList<String> NS) throws Error {
    Output output = new Output();
    XmlUtil.checkName(node, "output", NS);
    output.ascending = (Boolean)XmlUtil.parseElem(node, "ascending", NS, output.ascending);
    output.collectPlates = (Boolean)XmlUtil.parseElem(node, "collectPlates", NS, output.collectPlates);
    output.collectForms = getCollect(XmlUtil.getChild(node, "collectForms", NS), NS);
    output.layoutMode = (Boolean)XmlUtil.parseElem(node, "layoutMode", NS, output.layoutMode);
    output.tilingMode = (Boolean)XmlUtil.parseElem(node, "tilingMode", NS, output.tilingMode);
    output.namestyle = (String)XmlUtil.parseElem(node, "namestyle", NS, output.namestyle);
    output.title = (String)XmlUtil.parseElem(node, "title", NS, output.title);
    return output;
  }

  public static Gutter getGutter(Element node, ArrayList<String> NS) throws Error {
    Gutter gutter = new Gutter();
    XmlUtil.checkName(node, "gutter", NS);
    gutter.back = (Double)XmlUtil.parseElem(node, "back", NS, gutter.back);
    gutter.bottomFromPage = (Double)XmlUtil.parseElem(node, "bottomFromPage", NS, gutter.bottomFromPage);
    gutter.cut = (Double)XmlUtil.parseElem(node, "cut", NS, gutter.cut);
    gutter.topFromPage = (Double)XmlUtil.parseElem(node, "topFromPage", NS, gutter.topFromPage);
    gutter.minSheetDistBottom = (Double)XmlUtil.parseElem(node, "minSheetDistBottom", NS, gutter.minSheetDistBottom);
    gutter.minSheetDistLeft = (Double)XmlUtil.parseElem(node, "minSheetDistLeft", NS, gutter.minSheetDistLeft);
    gutter.minSheetDistRight = (Double)XmlUtil.parseElem(node, "minSheetDistRight", NS, gutter.minSheetDistRight);
    gutter.minSheetDistTop = (Double)XmlUtil.parseElem(node, "minSheetDistTop", NS, gutter.minSheetDistTop);
    return gutter;
  }

  public static StepAndRepeat getStepAndRepeat(Element node, ArrayList<String> NS) throws Error {
    StepAndRepeat stepAndRepeat = new StepAndRepeat();
    XmlUtil.checkName(node, "stepAndRepeat", NS);
    stepAndRepeat.allowRotation = (Boolean)XmlUtil.parseElem(node, "allowRotation", NS, stepAndRepeat.allowRotation);
    stepAndRepeat.continuousPages = (Boolean)XmlUtil.parseElem(node, "continuousPages", NS, stepAndRepeat.continuousPages);
    stepAndRepeat.minimumCoverage = (Double)XmlUtil.parseElem(node, "minimumCoverage", NS, stepAndRepeat.minimumCoverage);
    return stepAndRepeat;
  }

  public static DeviceParams getDeviceParams(Element node, ArrayList<String> NS) throws Error {
    DeviceParams deviceParams = new DeviceParams();
    XmlUtil.checkName(node, "deviceParameters", NS);
    deviceParams.gutter = getGutter(XmlUtil.getChild(node, "gutter", NS), NS);
    deviceParams.printer = getPrinter(XmlUtil.getChild(node, "printer", NS), NS);
    deviceParams.sheet = getSheet(XmlUtil.getChild(node, "sheet", NS), NS);
    return deviceParams;
  }

  public static RemainderPackage getRemainderPackage(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "remainderPackage", NS);

    String value = node.getTextContent();

    if (value.equals("automatic")) {
      return RemainderPackage.automatic;
    }
    if (value.equals("first")) {
      return RemainderPackage.first;
    }
    if (value.equals("middle")) {
      return RemainderPackage.middle;
    }
    if (value.equals("last")) {
      return RemainderPackage.last;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"remainderPackage\"");
  }

  public static RemainderSheet getRemainderSheet(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "remainderSheet", NS);

    String value = node.getTextContent();

    if (value.equals("automatic")) {
      return RemainderSheet.automatic;
    }
    if (value.equals("first")) {
      return RemainderSheet.first;
    }
    if (value.equals("beforeLast")) {
      return RemainderSheet.beforeLast;
    }
    if (value.equals("last")) {
      return RemainderSheet.last;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"remainderSheet\"");
  }

  public static Optimization getOptimization(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "optimization", NS);

    String value = node.getTextContent();

    if (value.equals("none")) {
      return Optimization.none;
    }
    if (value.equals("combineSame")) {
      return Optimization.combineSame;
    }
    if (value.equals("combineSheets")) {
      return Optimization.combineSheets;
    }
    if (value.equals("combineCopies")) {
      return Optimization.combineCopies;
    }
    if (value.equals("workAndTT")) {
      return Optimization.workAndTT;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"optimization\"");
  }

  public static AssemblingType getAssemblingType(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "assemblingType", NS);

    String value = node.getTextContent();

    if (value.equals("none")) {
      return AssemblingType.cutAndStack;
    }
    if (value.equals("gathering")) {
      return AssemblingType.gathering;
    }
    if (value.equals("insertAll")) {
      return AssemblingType.insertAll;
    }
    if (value.equals("inserting")) {
      return AssemblingType.inserting;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"assemblingType\"");
  }

  public static BindingType getBindingType(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "bindingType", NS);

    String value = node.getTextContent();

    if (value.equals("threadStitching")) {
      return BindingType.threadStitching;
    }
    if (value.equals("saddleStitching")) {
      return BindingType.saddleStitching;
    }
    if (value.equals("perfectBound")) {
      return BindingType.perfectBound;
    }
    if (value.equals("looseLeaf")) {
      return BindingType.looseLeaf;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"bindingType\"");
  }

  public static Cell getCell(Element node, ArrayList<String> NS) throws Error {
    Cell cell = new Cell();
    XmlUtil.checkName(node, "cell", NS);
    cell.index = (Integer)XmlUtil.parseElem(node, "index", NS, cell.index);
    cell.rotated = (Boolean)XmlUtil.parseElem(node, "rotated", NS, cell.rotated);
    return cell;
  }

  public static Row getRow(Element node, ArrayList<String> NS) throws Error {
    Row row = new Row();
    XmlUtil.checkName(node, "row", NS);
    ArrayList<Element> children = XmlUtil.getChildren(node, "cell", NS);
    for (Element child : children) {
      row.cells.add(getCell(child, NS));
    }
    return row;
  }

  public static Layout getLayout(Element node, ArrayList<String> NS) throws Error {
    Layout layout = new Layout();
    XmlUtil.checkName(node, "layout", NS);
    layout.active = (Boolean)XmlUtil.parseElem(node, "active", NS, layout.active);
    layout.horizontal = (Boolean)XmlUtil.parseElem(node, "horizontal", NS, layout.horizontal);
    layout.name = (String)XmlUtil.parseElem(node, "name", NS, layout.name);
    ArrayList<Element> children = XmlUtil.getChildren(node, "row", NS);
    for (Element child : children) {
      layout.rows.add(getRow(child, NS));
    }
    return layout;
  }

  public static Model getModel(Element node, ArrayList<String> NS) throws Error {
    Model model = new Model();
    XmlUtil.checkName(node, "model", NS);
    model.assemblingType = getAssemblingType(XmlUtil.getChild(node, "assemblingType", NS), NS);
    model.bindingType = getBindingType(XmlUtil.getChild(node, "bindingType", NS), NS);
    model.blankPagesFront = (Boolean)XmlUtil.parseElem(node, "blankPagesFront", NS, model.blankPagesFront);
    model.foldingProcess = (Boolean)XmlUtil.parseElem(node, "foldingProcess", NS, model.foldingProcess);
    model.optimization = getOptimization(XmlUtil.getChild(node, "optimization", NS), NS);
    model.remainderPackage = getRemainderPackage(XmlUtil.getChild(node, "remainderPackage", NS), NS);
    model.remainderSheet = getRemainderSheet(XmlUtil.getChild(node, "remainderSheet", NS), NS);
    model.reversePages = (Boolean)XmlUtil.parseElem(node, "reversePages", NS, model.reversePages);
    model.singleSided = (Boolean)XmlUtil.parseElem(node, "singleSided", NS, model.singleSided);
    ArrayList<Element> children = XmlUtil.getChildren(node, "layout", NS);
    for (Element child : children) {
      model.layouts.add(getLayout(child, NS));
    }
    return model;
  }

  public static FrontBack getFrontBack(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "frontBack", NS);

    String value = node.getTextContent();

    if (value.equals("front")) {
      return FrontBack.front;
    }
    if (value.equals("back")) {
      return FrontBack.back;
    }
    if (value.equals("frontAndBack")) {
      return FrontBack.frontAndBack;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"frontBack\"");
  }

  public static FormSelect getFormSelect(Element node, ArrayList<String> NS) throws Error {
    FormSelect formSelect = new FormSelect();
    XmlUtil.checkName(node, "formSelect", NS);
    formSelect.frontBack = getFrontBack(XmlUtil.getChild(node, "frontBack", NS), NS);
    formSelect.mirrorBack = (Boolean)XmlUtil.parseElem(node, "mirrorBack", NS, formSelect.mirrorBack);
    formSelect.formSelector = (String)XmlUtil.parseElem(node, "formSelector", NS, formSelect.formSelector);
    formSelect.mediaSelector = (String)XmlUtil.parseElem(node, "formSelector", NS, formSelect.mediaSelector);
    formSelect.pageSelector = (String)XmlUtil.parseElem(node, "formSelector", NS, formSelect.pageSelector);
    formSelect.sheetSelector = (String)XmlUtil.parseElem(node, "formSelector", NS, formSelect.sheetSelector);
    return formSelect;
  }
  
  public static Mirror getMirror(Element node, ArrayList<String> NS) throws Error {
    Mirror mirror = new Mirror();
    XmlUtil.checkName(node, "mirror", NS);
    mirror.horizontal = (Boolean)XmlUtil.parseElem(node, "horizontal", NS, mirror.horizontal);
    mirror.point = (Boolean)XmlUtil.parseElem(node, "point", NS, mirror.horizontal);
    mirror.vertical = (Boolean)XmlUtil.parseElem(node, "vertical", NS, mirror.horizontal);
    return mirror;
  }
  
  public static LineAppearance getLineAppearance(Element node, ArrayList<String> NS) throws Error {
    LineAppearance lineAppearance = new LineAppearance();
    XmlUtil.checkName(node, "lineAppearance", NS);
    lineAppearance.distance = (Double)XmlUtil.parseElem(node, "distance", NS, lineAppearance.distance);
    lineAppearance.thickness = (Double)XmlUtil.parseElem(node, "thickness", NS, lineAppearance.thickness);
    lineAppearance.length = (Double)XmlUtil.parseElem(node, "length", NS, lineAppearance.length);
    return lineAppearance;
  }
  
  public static Origin getOrigin(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "origin", NS);

    String value = node.getTextContent();

    if (value.equals("head")) {
      return Origin.head;
    }
    if (value.equals("foot")) {
      return Origin.foot;
    }
    if (value.equals("back")) {
      return Origin.back;
    }
    if (value.equals("face")) {
      return Origin.face;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"origin\"");
  }
  
  public static Direction getDirection(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "direction", NS);

    String value = node.getTextContent();

    if (value.equals("headFoot")) {
      return Direction.headFoot;
    }
    if (value.equals("footHead")) {
      return Direction.footHead;
    }
    if (value.equals("backFace")) {
      return Direction.backFace;
    }
    if (value.equals("faceBack")) {
      return Direction.faceBack;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"direction\"");
  }
  
  public static Position getPosition(Element node, ArrayList<String> NS) throws Error {
    Position position = new Position();
    XmlUtil.checkName(node, "position", NS);
    position.offsetNormal = (Double)XmlUtil.parseElem(node, "offsetNormal", NS, position.offsetNormal);
    position.offsetParallel = (Double)XmlUtil.parseElem(node, "offsetParallel", NS, position.offsetParallel);
    position.origin = getOrigin(XmlUtil.getChild(node, "origin", NS), NS);
    return position;
  }
  
  public static IncludeColor getIncludeColor(Element node, ArrayList<String> NS) throws Error {
    IncludeColor includeColor = new IncludeColor();
    XmlUtil.checkName(node, "includeColor", NS);
    includeColor.name = (String)XmlUtil.parseElem(node, "name", NS, includeColor.name);
    includeColor.cmyk = getCmyk(XmlUtil.getChild(node, "cmyk", NS), NS);
    return includeColor;
  }
  
  public static Numbering getNumbering(Element node, ArrayList<String> NS) throws Error {
    Numbering numbering = new Numbering();
    XmlUtil.checkName(node, "numbering", NS);
    numbering.name = (String)XmlUtil.parseElem(node, "name", NS, numbering.name);
    numbering.style = (String)XmlUtil.parseElem(node, "name", NS, numbering.style);
    numbering.rotation = getRotation(XmlUtil.getChild(node, "rotation", NS), NS);
    return numbering;
  }
  
  public static Line getLine(Element node, ArrayList<String> NS) throws Error {
    Line line = new Line();
    XmlUtil.checkName(node, "line", NS);
    line.length = (Double)XmlUtil.parseElem(node, "length", NS, line.length);
    line.thickness = (Double)XmlUtil.parseElem(node, "thickness", NS, line.length);
    line.spotColor = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      line.knockoutThickness = help.map(v -> (Double) v);
    }
    line.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return line;
  }
  
  public static Circle getCircle(Element node, ArrayList<String> NS) throws Error {
    Circle circle = new Circle();
    XmlUtil.checkName(node, "circle", NS);
    circle.radius = (Double)XmlUtil.parseAttribute(node, "radius", circle.radius);
    circle.thickness = (Double)XmlUtil.parseAttribute(node, "thickness", circle.thickness);
    circle.spotColor = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);;
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      circle.knockoutThickness = help.map(v -> (Double) v);
    }
    circle.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return circle;
  }
  
  public static Rectangle getRectangle(Element node, ArrayList<String> NS) throws Error {
    Rectangle rectangle = new Rectangle();
    XmlUtil.checkName(node, "rectangle", NS);
    rectangle.size = getSize(XmlUtil.getChild(node, "size", NS), NS);
    rectangle.spotColor = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      rectangle.knockoutThickness = help.map(v -> (Double) v);
    }
    rectangle.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return rectangle;
  }
  
  public static Label getLabel(Element node, ArrayList<String> NS) throws Error {
    Label label = new Label();
    XmlUtil.checkName(node, "label", NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      label.knockoutThickness = help.map(v -> (Double) v);
    }
    ArrayList<Element> children = XmlUtil.getChildren(node, "segment", NS);
    for (Element child : children) {
      label.segments.add(getSegment(child, NS));
    }
    label.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return label;
  }
  
  public static Image getImage(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "image", NS);
    Point point = new Point((Double)XmlUtil.parseElem(node, "width", NS, 0.0), 
                            (Double)XmlUtil.parseElem(node, "height", NS, 0.0));
    Image image = new Image(Path.of((String)XmlUtil.parseElem(node, "path", new String())), point);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      image.knockoutThickness = help.map(v -> (Double) v);
    }
    image.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    // TBI replace colors //
    return image;
  }
  
  public static BarcodeStyle getBarcodeStyle(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "barcodeStyle", NS);

    String value = node.getTextContent();

    if(value.equals("interleaved")) {
      return BarcodeStyle.interleaved;
    }
    if(value.equals("code128")) {
      return BarcodeStyle.code128;
    }
    if(value.equals("dataMatrix")) {
      return BarcodeStyle.dataMatrix;
    }
    if(value.equals("dataMatrixRect")) {
      return BarcodeStyle.dataMatrixRect;
    }
    if(value.equals("qr")) {
      return BarcodeStyle.qr;
    }
    if(value.equals("qrMicro")) {
      return BarcodeStyle.qrMicro;
    }

    throw new Error("enumeration \"" + value + "\" cannot be matched with \"barcodeStyle\"");
  }
  
  public static BarcodeSize getBarcodeSize(Element node, ArrayList<String> NS) throws Error {
    BarcodeSize barcodeSize = new BarcodeSize();
    XmlUtil.checkName(node, "barcodeSize", NS);
    barcodeSize.elementSize = (Double)XmlUtil.parseElem(node, "elementSize", NS, barcodeSize.elementSize);
    barcodeSize.height = (Double)XmlUtil.parseElem(node, "height", NS, barcodeSize.height);
    barcodeSize.quietZone = (Double)XmlUtil.parseElem(node, "quietZone", NS, barcodeSize.quietZone);
    barcodeSize.wideToNarrow = (Double)XmlUtil.parseElem(node, "wideToNarrow", NS, barcodeSize.wideToNarrow);
    return barcodeSize;
  }
  
  public static BarcodeCopies getBarcodeCopies(Element node, ArrayList<String> NS) throws Error {
    BarcodeCopies barcodeCopies = new BarcodeCopies();
    XmlUtil.checkName(node, "barcodeCopies", NS);
    barcodeCopies.maxNum = (Integer)XmlUtil.parseElem(node, "maxNum", NS, barcodeCopies.maxNum);
    barcodeCopies.spacing = (Double)XmlUtil.parseElem(node, "spacing", NS, barcodeCopies.spacing);
    return barcodeCopies;
  }
  
  public static Barcode getBarcode(Element node, ArrayList<String> NS) throws Error {
    Barcode barcode = new Barcode();
    XmlUtil.checkName(node, "Barcode", NS);
    barcode.barcodeCopies = getBarcodeCopies(XmlUtil.getChild(node, "barcodeCopies", NS), NS);
    barcode.barcodeSize = getBarcodeSize(XmlUtil.getChild(node, "barcodeSize", NS), NS);
    barcode.barcodeStyle = getBarcodeStyle(XmlUtil.getChild(node, "barcodeStyle", NS), NS);
    barcode.displayText = (Boolean)XmlUtil.parseElem(node, "displayText", NS, barcode.displayText);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      barcode.knockoutThickness = help.map(v -> (Double) v);
    }
    barcode.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return barcode;
  }
  
  public static Mark getMark(Element node, ArrayList<String> NS) throws Error {
    XmlUtil.checkName(node, "mark", NS);

    Node child = node.getFirstChild();
    if (child == null) {
      throw new Error("empty \"mark\" element");
    }

    ////TBI
    String type = child.
    String[] split = type.split(":");
    type = split[split.length - 1];

  
    if (type.equals("line")) {
      return getLine(XmlUtil.getChild(node, "line", NS), NS);
    }
    if (type.equals("circle")) {
      return getCircle(XmlUtil.getChild(node, "circle", NS), NS);
    }
    if (type.equals("rectangle")) {
      return getRectangle(XmlUtil.getChild(node, "rectangle", NS), NS);
    }
    if (type.equals("image")) {
      return getImage(XmlUtil.getChild(node, "image", NS), NS);
    }
    if (type.equals("label")) {
      return getLabel(XmlUtil.getChild(node, "label", NS), NS);
    }
    if (type.equals("barcode")) {
      return getBarcode(XmlUtil.getChild(node, "barcode", NS), NS);
    }
    if (type.equals("markGroup")) {
      return getGroup(XmlUtil.getChild(node, "markGroup", NS), NS);
    }

    throw new Error("mark type \"" + type + "\" cannot be matched with \"mark\"");
  }
  
  public static Group getGroup(Element node, ArrayList<String> NS) throws Error {
    Group group = new Group();
    XmlUtil.checkName(node, "markGroup", NS);
    group.alignment = getAlignment(XmlUtil.getChild(node, "alignment", NS), NS);
    group.formSelect = getFormSelect(XmlUtil.getChild(node, "formSelect", NS), NS);
    group.mirror = getMirror(XmlUtil.getChild(node, "mirror", NS), NS);
    group.name = (String)XmlUtil.parseElem(node, "name", NS, group.name);
    group.size = getSize(XmlUtil.getChild(node, "size", NS), NS);
    ArrayList<Element> children = XmlUtil.getChildren(node, "mark", NS);
    for(Element child : children) {
      group.marks.add(getMark(child, NS));
    }
    group.transform(getMatrix(XmlUtil.getChild(node, "matrix", NS), NS));
    return group;
  }
  
  public static TrimMarks getTrimMarks(Element node, ArrayList<String> NS) throws Error {
    TrimMarks trimMarks = new TrimMarks();
    XmlUtil.checkName(node, "trimMarks", NS);
    trimMarks.printInner = (Boolean)XmlUtil.parseElem(node, "printInner", NS, trimMarks.printInner);
    trimMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    trimMarks.lineAppearance = getLineAppearance(XmlUtil.getChild(node, "lineAppearance", NS), NS); 
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      trimMarks.knockoutThickness = help.map(v -> (Double) v);
    }

    return trimMarks;
  }
  
  public static BleedMarks getBleedMarks(Element node, ArrayList<String> NS) throws Error {
    BleedMarks bleedMarks = new BleedMarks();
    XmlUtil.checkName(node, "bleedMarks", NS);
    bleedMarks.printInner = (Boolean)XmlUtil.parseElem(node, "printInner", NS, bleedMarks.printInner);
    bleedMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    bleedMarks.lineAppearance = getLineAppearance(XmlUtil.getChild(node, "lineAppearance", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      bleedMarks.knockoutThickness = help.map(v -> (Double) v);
    }
    return bleedMarks;
  }
  
  public static CollationMarks getCollationMarks(Element node, ArrayList<String> NS) throws Error {
    CollationMarks collationMarks = new CollationMarks();
    XmlUtil.checkName(node, "collationMarks", NS);
    collationMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    collationMarks.direction = getDirection(XmlUtil.getChild(node, "direction", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      collationMarks.knockoutThickness = help.map(v -> (Double) v);
    }
    collationMarks.length = (Double)XmlUtil.parseElem(node, "length", NS, collationMarks.length);
    try {
      Element child = XmlUtil.getChild(node, "numbering", NS);
      collationMarks.numbering = Optional.of(getNumbering(child, NS));
    }
    catch (Error e) {}
    collationMarks.position = getPosition(XmlUtil.getChild(node, "position", NS), NS);
    collationMarks.thickness = (Double)XmlUtil.parseElem(node, "thickness", NS, collationMarks.thickness);
    return collationMarks;
  }
  
  public static CutMarks getCutMarks(Element node, ArrayList<String> NS) throws Error {
    CutMarks cutMarks = new CutMarks();
    XmlUtil.checkName(node, "cutMarks", NS);
    cutMarks.activateCrosses = (Boolean)XmlUtil.parseElem(node, "activateCrosses", NS, cutMarks.activateCrosses);
    cutMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      cutMarks.knockoutThickness = help.map(v -> (Double) v);
    }
    cutMarks.lineAppearance = getLineAppearance(XmlUtil.getChild(node, "lineAppearance", NS), NS);
    return cutMarks;
  }
  
  public static FoldMarks getFoldMarks(Element node, ArrayList<String> NS) throws Error {
    FoldMarks foldMarks = new FoldMarks();
    XmlUtil.checkName(node, "foldMarks", NS);
    foldMarks.activateCrosses = (Boolean)XmlUtil.parseElem(node, "activateCrosses", NS, foldMarks.activateCrosses);
    foldMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      foldMarks.knockoutThickness = help.map(v -> (Double) v);
    }
    foldMarks.lineAppearance = getLineAppearance(XmlUtil.getChild(node, "lineAppearance", NS), NS);
    return foldMarks;
  }
  
  public static MarginMarks getMarginMarks(Element node, ArrayList<String> NS) throws Error {
    MarginMarks marginMarks = new MarginMarks();
    XmlUtil.checkName(node, "marginMarks", NS);
    marginMarks.activateCrosses = (Boolean)XmlUtil.parseElem(node, "activateCrosses", NS, marginMarks.activateCrosses);
    marginMarks.color = getSpotColor(XmlUtil.getChild(node, "color", NS), NS);
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      marginMarks.knockoutThickness = help.map(v -> (Double) v);
    }
    marginMarks.lineAppearance = getLineAppearance(XmlUtil.getChild(node, "lineAppearance", NS), NS);
    return marginMarks;
  }

  public static ColorControl getColorControl(Element node, ArrayList<String> NS) throws Error {
    ColorControl colorControl = new ColorControl();
    XmlUtil.checkName(node, "colorControl", NS);
    colorControl.alignment = getAlignment(XmlUtil.getChild(node, "alignment", NS), NS);
    colorControl.excludeColors = XmlUtil.mapAll(XmlUtil.parseElems(node, "excludeColors", NS, new String()), v -> (String)v);
    colorControl.horizontal = (Boolean)XmlUtil.parseElem(node, "horizontal", NS, colorControl.horizontal);
    ArrayList<Element> children = XmlUtil.getChildren(node, "includeColor", NS);
    for (Element child : children) {
      colorControl.includeColors.add(getIncludeColor(child, NS));
    }
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      colorControl.knockoutThickness = help.map(v -> (Double) v);
    }
    colorControl.margin = getMargin(XmlUtil.getChild(node, "margin", NS), NS);
    colorControl.offset = getOffset(XmlUtil.getChild(node, "offset", NS), NS);
    colorControl.space = getSpace(XmlUtil.getChild(node, "space", NS), NS);
    colorControl.shading = getShading(XmlUtil.getChild(node, "shading", NS), NS);
    colorControl.size = getSize(XmlUtil.getChild(node, "size", NS), NS);
    return colorControl;
  }
  
  public static ColorBar getColorBar(Element node, ArrayList<String> NS) throws Error {
    ColorBar colorBar = new ColorBar();
    XmlUtil.checkName(node, "colorBar", NS);
    colorBar.alignment = getAlignment(XmlUtil.getChild(node, "alignment", NS), NS);
    colorBar.excludeColors = XmlUtil.mapAll(XmlUtil.parseElems(node, "excludeColors", NS, new String()), v -> (String)v);
    colorBar.horizontal = (Boolean)XmlUtil.parseElem(node, "horizontal", NS, colorBar.horizontal);
    ArrayList<Element> children = XmlUtil.getChildren(node, "includeColor", NS);
    for (Element child : children) {
      colorBar.includeColors.add(getIncludeColor(child, NS));
    }
    Optional<Object> help = XmlUtil.parseElemOpt(node, "knockoutThickness", NS, 0.0);
    if(help.isPresent()) {
      colorBar.knockoutThickness = help.map(v -> (Double) v);
    }
    colorBar.margin = getMargin(XmlUtil.getChild(node, "margin", NS), NS);
    colorBar.offset = getOffset(XmlUtil.getChild(node, "offset", NS), NS);
    colorBar.intensity = (Double)XmlUtil.parseElem(node, "intensity", NS, colorBar.intensity);
    colorBar.space = (Double)XmlUtil.parseElem(node, "space", NS, colorBar.space);
    colorBar.size = (Double)XmlUtil.parseElem(node, "size", NS, colorBar.size);
    return colorBar;
  }
  
  public static CalcMarks getCalcMarks(Element node, ArrayList<String> NS) throws Error {
    CalcMarks calcMarks = new CalcMarks();
    XmlUtil.checkName(node, "calculatedMarks", NS);

    Element trimMarksNode = null;
    Element bleedMarksNode = null;
    Element collationMarksNode = null;
    Element cutMarksNode = null;
    Element foldMarksNode  = null;
    Element marginMarksNode = null;
    try {
      trimMarksNode = XmlUtil.getChild(node, "trimMarks", NS);
    }
    catch(Error e) {}
    try {
      bleedMarksNode = XmlUtil.getChild(node, "bleedMarks", NS);
    }
    catch(Error e) {}
    try {
      collationMarksNode = XmlUtil.getChild(node, "collationMarks", NS);
    }
    catch(Error e) {}
    try {
      cutMarksNode = XmlUtil.getChild(node, "cutMarks", NS);
    }
    catch(Error e) {}
    try {
      foldMarksNode = XmlUtil.getChild(node, "foldMarks", NS);
    }
    catch(Error e) {}
    try {
      marginMarksNode = XmlUtil.getChild(node, "marginMarks", NS);
    }
    catch(Error e) {}

    if (trimMarksNode != null) {
      calcMarks.trimMarks = Optional.of(getTrimMarks(trimMarksNode, NS));
    }
    if (trimMarksNode != null) {
      calcMarks.bleedMarks = Optional.of(getBleedMarks(bleedMarksNode, NS));
    }
    if (trimMarksNode != null) {
      calcMarks.collationMarks = Optional.of(getCollationMarks(collationMarksNode, NS));
    }
    if (trimMarksNode != null) {
      calcMarks.cutMarks = Optional.of(getCutMarks(cutMarksNode, NS));
    }
    if (trimMarksNode != null) {
      calcMarks.foldMarks = Optional.of(getFoldMarks(foldMarksNode, NS));
    }
    if (trimMarksNode != null) {
      calcMarks.marginMarks = Optional.of(getMarginMarks(marginMarksNode, NS));
    }

    ArrayList<Element> children = XmlUtil.getChildren(node, "colorBar", NS);
    for (Element child : children) {
      calcMarks.colorBars.add(getColorBar(child, NS));
    }

    children = XmlUtil.getChildren(node, "colorControl", NS);
    for (Element child : children) {
      calcMarks.colorControls.add(getColorControl(child, NS));
    }

    return calcMarks;
  }
  
  public static Marks getMarks(Element node, ArrayList<String> NS) throws Error {
    Marks marks = new Marks();
    XmlUtil.checkName(node, "marks", NS);
    marks.calcMarks = getCalcMarks(XmlUtil.getChild(node, "calculatedMarks", NS), NS);
    
    ArrayList<Element> children = XmlUtil.getChildren(XmlUtil.getChild(node, "plateMarks", NS), "markGroup", NS);
    for (Element child : children) {
      marks.plateMarks.add(getGroup(child, NS));
    }
    children = XmlUtil.getChildren(XmlUtil.getChild(node, "sheetMarks", NS), "markGroup", NS);
    for (Element child : children) {
      marks.sheetMarks.add(getGroup(child, NS));
    }
    children = XmlUtil.getChildren(XmlUtil.getChild(node, "printedSheetMarks", NS), "markGroup", NS);
    for (Element child : children) {
      marks.printedSheetMarks.add(getGroup(child, NS));
    }
    children = XmlUtil.getChildren(XmlUtil.getChild(node, "pageMarks", NS), "markGroup", NS);
    for (Element child : children) {
      marks.plateMarks.add(getGroup(child, NS));
    }

    return marks;
  }
  
  public static Flat getFlat(Element node, ArrayList<String> NS) throws Error {
    Flat flat = new Flat();
    XmlUtil.checkName(node, "flat", NS);
    flat.deviceParameters = getDeviceParams(XmlUtil.getChild(node, "deviceParameters", NS), NS);
    flat.referenceLayout = XmlUtil.parseElemOpt(node, "referenceLayout", NS, flat).map(v -> (Integer) v);
    flat.marks = getMarks(XmlUtil.getChild(node, "marks", NS), NS);
    return flat;
  }
  
  private static Element parse(Path path) throws Error {
      try {
          File inputFile = path.toFile();
          DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
          dbFactory.setNamespaceAware(true);
          DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

          Document doc = dBuilder.parse(inputFile);
          doc.getDocumentElement().normalize();
          return doc.getDocumentElement();
      }
      catch (ParserConfigurationException e) {
          throw new Error("error parsing file " + path, "", "ParserConfigurationException");
      }
      catch (SAXException e) {
          throw new Error("error parsing file " + path, "", "SAXException");
      }
      catch (IOException e) {
          throw new Error("error parsing file " + path, "", "IOException");
      }
  }
}
