package de.onevision.App;

import java.nio.file.Path;
import java.util.ArrayList;

import de.onevision.OutXml.OutXml;
import de.onevision.PdfParser.*;
import de.onevision.Platform.Application;
import de.onevision.Platform.Locations;
import de.onevision.Platform.Exceptions.*;
import de.onevision.Platform.Exceptions.Error;
import de.onevision.marks.*;
import de.onevision.math.Point;

public class App {
    public static void main(String[] args) {
        try {
            impl();
            impl2();
        } catch (Base e) {
            e.handle();
        }
    }

    private static void impl2() throws Error {
        Application app = Application.init(Path.of("C:/Windows/System32/where"));
        ArrayList<String> args = new ArrayList<>();
        args.add("where");
        app.execute(args, true);
    }

    private static void impl() throws Error, NotImpl {
        Path pathToInputXml = Locations.resources().resolve("inputPdf.xml");
        PdfParser pdfParser = PdfParser.parse(pathToInputXml);
        PdfInfo pdfInfo = PdfInfo.init();
        pdfInfo.append(pdfParser.root());

        Circle circle = new Circle();
        Image image = new Image(Locations.resources().resolve("someMark.pdf"), new Point(100, 200), 3);
        Line line = new Line();
        Rectangle rect = new Rectangle();
        Text text = new Text();

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
}
