package de.onevision.Canvas;

import de.onevision.PdfParser.PdfInfo;
import de.onevision.PdfParser.PdfParser;
import de.onevision.Platform.Locations;
import de.onevision.Platform.Exceptions.Base;

import java.io.Serializable;
import java.nio.file.Path;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

@Named("Canvas")
@ViewScoped
public class CanvasBean implements Serializable {
    public CanvasBean() {
        PdfInfo pdfInfo = null;
        try {
            Path pathToInputXml = Locations.resources().resolve("inputPdf.xml");
            PdfParser pdfParser = PdfParser.parse(pathToInputXml);
            pdfInfo = PdfInfo.init();
            pdfInfo.append(pdfParser.root());
        } catch (Base e) {
            e.handle();
        }

        System.out.println("REACHED!");

        this.maxPageNum = pdfInfo.overallPageCount();
        this.pathToPdf = Locations.resources().resolve("inputPdf.pdf");
    }

    public int page() {
        return page;
    }

    public void page(int page) {
        if (page <= 0) {
            page = 1;
        }
        if (page > maxPageNum) {
            page = maxPageNum;
        }
        this.page = page;
    }

    public int maxPageNum() {
        return maxPageNum;
    }

    public String pathToPdf() {
        return pathToPdf.toString();
    }

    private Path pathToPdf = Path.of("WEB-INF/classes/inputPdf.pdf");
    private int page = 1;
    private int maxPageNum = 1;
    private static final long serialVersionUID = 1L;
}