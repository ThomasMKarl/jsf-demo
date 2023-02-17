package de.onevision.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.onevision.Platform.ExportConfig;
import de.onevision.Platform.Locations;
import de.onevision.Platform.TarUtil;
import de.onevision.Platform.XmlWriter;
import de.onevision.Platform.Exceptions.Error;
import de.onevision.model.Model;

public class ImposeConfig {
  public Output output;
  public PagePos pagePos;
  public Optional<StepAndRepeat> stepAndRepeat = Optional.empty();
  public Model model;
  public ArrayList<Flat> flats = new ArrayList<Flat>();


  public int majorVersion() {
    return majorVersion;
  }

  public int minorVersion() {
    return minorVersion;
  }

  public String version() {
    return "i" + ((Integer)majorVersion).toString() + "." + ((Integer)minorVersion).toString();
  }

  public void export(ExportConfig config, Path outputFile) throws Error {
    var filename = Locations.removeFileExtension(outputFile, false);

    var outPaths = new ArrayList<Path>();

    if (config.exportTarget) {
      var xmlWriter = new XmlWriter("ic:imposeConfigTarget");

      xmlWriter.setVersion(majorVersion, minorVersion);
      xmlWriter.addNS("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      xmlWriter.addSchemaLocation("https://www.onevision.com/ImposeConfigTarget", Path.of("ImposeConfigTarget.xsd"));
      xmlWriter.addNS("ic", "https://www.onevision.com/ImposeConfigTarget");
      xmlWriter.addNS("ipt", "https://www.onevision.com/ImposePhysicalTypes");
      xmlWriter.addNS("ict", "https://www.onevision.com/ImposeCommonTypes");

      xmlWriter.setNS(new ArrayList<String>(List.of("ic", "ipt", "ict")));
      xmlWriter.write(this.output, "output");
      xmlWriter.write(this.pagePos, "pagePos");
      if (this.stepAndRepeat.isPresent()) {
        xmlWriter.write(this.stepAndRepeat.get(), "stepAndRepeat");
      }
      var path = config.tempFolder.resolve(filename + "_Target.xml");
      xmlWriter.writeToFile(path);
      outPaths.add(path);
    }

    if (config.exportModel) {
      var xmlWriter = new XmlWriter("ic:imposeConfigModel");

      xmlWriter.setVersion(majorVersion, minorVersion);
      xmlWriter.addNS("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      xmlWriter.addSchemaLocation("https://www.onevision.com/ImposeConfigModel", Path.of("ImposeConfigModel.xsd"));
      xmlWriter.addNS("ic", "https://www.onevision.com/ImposeConfigModel");
      xmlWriter.addNS("imo", "https://www.onevision.com/ImposeModel");
      xmlWriter.addNS("ict", "https://www.onevision.com/ImposeCommonTypes");

      xmlWriter.setNS(new ArrayList<String>(List.of("ic", "imo", "ict")));
      xmlWriter.write(this.model, "model");
      var path = config.tempFolder.resolve(filename + "_Model.xml");
      xmlWriter.writeToFile(path);
      outPaths.add(path);
    }

    int runningIndex = 0;
    for (Integer index : config.exportDeviceParamsIndexList) {
      if(index >= flats.size()) {
        continue;
      }

      Flat flat = this.flats.get(index);

      var xmlWriter = new XmlWriter("ic:imposeConfigDeviceParams");

      xmlWriter.setVersion(majorVersion, minorVersion);
      xmlWriter.addNS("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      xmlWriter.addSchemaLocation("https://www.onevision.com/ImposeConfigDeviceParams", Path.of("ImposeConfigDeviceParams.xsd"));
      xmlWriter.addNS("ic", "https://www.onevision.com/ImposeConfigDeviceParams");
      xmlWriter.addNS("ipt", "https://www.onevision.com/ImposePhysicalTypes");
      xmlWriter.addNS("ict", "https://www.onevision.com/ImposeCommonTypes");

      xmlWriter.setNS(new ArrayList<String>(List.of("ic", "ipt", "ict")));
      xmlWriter.write(flat.deviceParameters, "model");
      var path = config.tempFolder.resolve(filename + "_DeviceParams_" + ((Integer)runningIndex).toString() + ".xml");
      xmlWriter.writeToFile(path);
      outPaths.add(path);

      if (config.exportMarksIndexList.contains(index)) {
        var xmlWriter_ = new XmlWriter("ic:imposeConfigMarks");

        xmlWriter_.setVersion(majorVersion, minorVersion);
        xmlWriter_.addNS("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlWriter_.addSchemaLocation("https://www.onevision.com/ImposeConfigMarks", Path.of("ImposeConfigMarks.xsd"));
        xmlWriter_.addNS("ic", "https://www.onevision.com/ImposeConfigMarks");
        xmlWriter_.addNS("ima", "https://www.onevision.com/ImposeMarks");
        xmlWriter_.addNS("ict", "https://www.onevision.com/ImposeCommonTypes");

        xmlWriter_.setNS(new ArrayList<String>(List.of("ic", "imo", "ict")));
        xmlWriter_.write(flat.marks, "marks");
        var path_ = config.tempFolder.resolve(filename + "_Marks_" + ((Integer)runningIndex).toString() + ".xml");
        xmlWriter_.writeToFile(path_);
        outPaths.add(path_);
      }
    }
  
    TarUtil.packTar(outPaths, outputFile);
  }

  public String exportAsString() throws Error {
    var xmlWriter = new XmlWriter("ic:imposeConfig");

    xmlWriter.setVersion(majorVersion, minorVersion);
    xmlWriter.addNS("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    xmlWriter.addSchemaLocation("https://www.onevision.com/ImposeConfig", Path.of("ImposeConfigFull.xsd"));
    xmlWriter.addNS("ic", "https://www.onevision.com/ImposeConfig");
    xmlWriter.addNS("ict", "https://www.onevision.com/ImposeCommonTypes");
    xmlWriter.addNS("ipt", "https://www.onevision.com/ImposePhysicalTypes");
    xmlWriter.addNS("imo", "https://www.onevision.com/ImposeModel");
    xmlWriter.addNS("ima", "https://www.onevision.com/ImposeMarks");

    xmlWriter.setNS(new ArrayList<String>(List.of("ic", "ict")));
    xmlWriter.write(this.pagePos, "output");
    xmlWriter.write(this.pagePos, "pagePos");
    if (this.stepAndRepeat.isPresent()) {
      xmlWriter.write(this.stepAndRepeat.get(), "stepAndRepeat");
    }

    xmlWriter.setNS(new ArrayList<String>(List.of("ic", "imo", "ict")));
    xmlWriter.write(this.model, "model");

    for (Flat flat : this.flats) {
      var flatXmlWriter = new XmlWriter("ic:flat", xmlWriter);
      flatXmlWriter.setNS(new ArrayList<String>(List.of("ic", "ipt", "ict")));
      flatXmlWriter.write(flat.deviceParameters, "deviceParameters");
      flatXmlWriter.setNS(new ArrayList<String>(List.of("ic", "ima", "ict")));
      flatXmlWriter.write(flat.marks, "marks");
    }

    System.out.println(xmlWriter.writeToString());

    return xmlWriter.writeToString();
  }

  public final int majorVersion = 31;
  public final int minorVersion = 1;
}
