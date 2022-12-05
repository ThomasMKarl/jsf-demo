package de.onevision.OutXml;

import de.onevision.math.Point;
import de.onevision.Platform.Locations;
import de.onevision.Platform.Exceptions.Error;
import de.onevision.marks.Mark;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.nio.file.Path;

public class OutXml {
    public static OutXml init() {
        return new OutXml();
    }

    public void newDoc(Path outputPath, Point sheetSize) throws Error {
        this.sheetSize = sheetSize;
        this.outputPath = outputPath;

        dbFactory = DocumentBuilderFactory.newInstance();
        try {
            doc = dbFactory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(errorMessage, "", "ParserConfigurationException");
        }

        rootElement = doc.createElement("ImposeSheets");
        doc.appendChild(rootElement);
        rootElement.setAttribute("width", Double.toString(sheetSize.x()));
        rootElement.setAttribute("height", Double.toString(sheetSize.y()));

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            transformer = transformerFactory.newTransformer();
        } catch (TransformerFactoryConfigurationError e) {
            throw new Error(errorMessage, "", "TransformerFactoryConfigurationError");
        } catch (TransformerConfigurationException e) {
            throw new Error(errorMessage, "", "TransformerConfigurationException");
        }

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    public void newSheet() {
        currentSheet = doc.createElement("Sheet");
        rootElement.appendChild(currentSheet);
    }

    public void newSheet(Point sheetSize) {
        currentSheet = doc.createElement("Sheet");
        rootElement.appendChild(currentSheet);
        if (sheetSize.equals(this.sheetSize)) {
            return;
        }

        currentSheet.setAttribute("width", Double.toString(sheetSize.x()));
        currentSheet.setAttribute("height", Double.toString(sheetSize.y()));
    }

    public void add(Mark mark) {
        mark.flatten();
        currentSheet = mark.generateXml(doc, currentSheet);
    }

    public void write() throws Error {
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputPath.toString()));
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new Error(errorMessage, "", "TransformerException");
        }
    }

    private OutXml() {
    }

    private Point sheetSize = new Point();
    private Path outputPath = Locations.storageBasePath();

    private static String errorMessage = "error writing output pdf";

    private DocumentBuilderFactory dbFactory;
    private Document doc;
    private Element rootElement;
    private Element currentSheet;
    private Transformer transformer;
}
