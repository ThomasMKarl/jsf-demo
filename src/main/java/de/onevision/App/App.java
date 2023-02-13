package de.onevision.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.onevision.OutXml.OutXml;
import de.onevision.PdfParser.*;
import de.onevision.Platform.Application;
import de.onevision.Platform.Locations;
import de.onevision.Platform.XmlWriter;
import de.onevision.Platform.Exceptions.*;
import de.onevision.Platform.Exceptions.Error;
import de.onevision.config.ImposeConfig;
import de.onevision.imposeConfigParser.ImposeConfigParser;
import de.onevision.marks.Circle;
import de.onevision.marks.Image;
import de.onevision.marks.Label;
import de.onevision.marks.Line;
import de.onevision.marks.Rectangle;
import de.onevision.marks.out.*;
import de.onevision.math.Point;

public class App {
    public static void main(String[] args) {
        try {
            //impl();
            //impl2();
            impl3();
            
        } catch (Base e) {
            e.handle();
        }
        catch (Exception e) {
            logger.error("internal error");
            logger.debug("unexpected exception");
            e.printStackTrace();
        }
        finally {
            cleanup();
        }
    }

    public static void extractTar(Path pathFrom, Path pathTo) throws Error {
        TarArchiveInputStream tarFile;
        try {
            tarFile = new TarArchiveInputStream(new FileInputStream(new File(pathFrom.toString())));
        }
        catch(FileNotFoundException e) {
            throw new Error("cannot read " + pathFrom);
        }

        try {
            Files.createDirectories(pathTo);
        }
        catch(IOException e) {
            throw new Error("cannot create temporary directory");
        }

        logger.info("reading file: " + pathFrom);
        TarArchiveEntry entry;
        try {
            while ((entry = tarFile.getNextTarEntry()) != null) {
                String filename = entry.getName();
                byte[] content = new byte[(int) entry.getSize()];
                int offset = 0;
                tarFile.read(content, offset, content.length - offset);
                if(entry.isDirectory()) {
                    Files.createDirectory(pathTo.resolve(filename));
                }
                else {
                    FileOutputStream outputFile = new FileOutputStream(new File(pathTo.toString() + "/" + filename));
                    IOUtils.write(content, outputFile);              
                    outputFile.close();
                }
            }               
        
            tarFile.close();
        }
        catch(IOException e) {
            throw new Error("cannot read " + pathFrom);
        }
    }

    public static void getPdfInfo(Path input, Path output) throws Error {
        Application ovpdfInfoHandle = Application.init(Locations.ovpdfInfoPath());
        ArrayList<String> args = new ArrayList<String>();
        args.add(input.toString());
        args.add(output.toString());
        ovpdfInfoHandle.execute(args, false);
    }

    private static void impl3() throws Error, ParserConfigurationException {
        Path inputFile = Locations.resources().resolve("3CrossFold.ic");
        String filename = Locations.removeFileExtension(inputFile, false);
        extractTar(inputFile, appTempFolder);
        ImposeConfigParser imposeConfigParser = new ImposeConfigParser(appTempFolder, filename);
        ImposeConfig imposeConfig = imposeConfigParser.createImposeConfig();

        XmlWriter xmlWriter = new XmlWriter();
        xmlWriter.add("test", 0, false);
        xmlWriter.write(null, 1);
    }

    private static void cleanup() {
        if (!Files.exists(appTempFolder))
            return;
        try {
            FileUtils.deleteDirectory(appTempFolder.toFile());
        }
        catch (IOException e) {
        }
    }

    private static void impl2() throws Error {
        Application app = Application.init(Path.of("C:/Windows/System32/where"));
        ArrayList<String> args = new ArrayList<>();
        args.add("where");
        app.execute(args, true);
    }

    private static void impl() throws Error, NotImpl {
        Path pathToInputPdf = Locations.resources().resolve("inputPdf.pdf");
        Path pathToInputXml = Locations.resources().resolve("inputPdf.xml");
        getPdfInfo(pathToInputPdf, pathToInputXml);

        PdfParser pdfParser = PdfParser.parse(pathToInputXml);
        PdfInfo pdfInfo = PdfInfo.init();
        pdfInfo.append(pdfParser.root());

        Circle circle = new Circle();
        Image image = new Image(Locations.resources().resolve("someMark.pdf"), new Point(100, 200));
        Line line = new Line();
        Rectangle rect = new Rectangle();
        Label text = new Label();

        Group mainGroup = new Group();
        Group group1 = new Group();
        Group subGroup1 = new Group();
        Group subsubgGroup1 = new Group();
        Group subsubgGroup2 = new Group();
        Group subGroup2 = new Group();
        Group group2 = new Group();

        subsubgGroup1.add(circle);

        subsubgGroup2.add(line);

        subGroup1.add(subsubgGroup1);
        subGroup1.add(subsubgGroup2);
        subGroup1.add(rect);

        subGroup2.add(text);

        group1.add(subGroup1);
        group1.add(subGroup2);

        group2.add(image);

        mainGroup.add(group1);
        mainGroup.add(group2);

        Path pathToOutputXml = Locations.resources().resolve("outputMarks.xml");
        OutXml outXml = OutXml.init();
        outXml.newDoc(pathToOutputXml, new Point(2000, 1000));

        outXml.newSheet();
        outXml.add(group1);

        outXml.newSheet(new Point(2300, 100));
        outXml.add(group2);

        outXml.write();
    }

    private static Path appTempFolder = Locations.userUniqueTempDir();

    private static Logger logger = LogManager.getLogger(App.class.getName());
}
