package de.onevision.PdfParser;

import de.onevision.Platform.Exceptions.Error;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PdfParser {
    static public PdfParser parse(Path path) throws Error {
        PdfParser parser = new PdfParser();
        try {
            File inputFile = path.toFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            parser.doc = dBuilder.parse(inputFile);
            parser.doc.getDocumentElement().normalize();
            parser.root = parser.doc.getDocumentElement();
            if(!parser.root.getNodeName().equals("PDFInfo")) {
                throw new Error("error parsing file " + path, "", "root element name is " + parser.root.getNodeName());
            }
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

        return parser;
    }

    public final Element root() {
        return root;
    }

    private PdfParser() {
    }

    private Document doc;
    private Element root;
}
