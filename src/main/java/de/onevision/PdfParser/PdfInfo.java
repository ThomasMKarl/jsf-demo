package de.onevision.PdfParser;

import de.onevision.Platform.Exceptions.Error;
import de.onevision.Platform.Exceptions.NotImpl;
import de.onevision.Platform.Helper;
import de.onevision.Platform.XmlUtil;
import de.onevision.color.CMYK;
import de.onevision.color.ColorInfo;
import de.onevision.color.SpotColor;
import de.onevision.math.Box;
import de.onevision.math.Point;
import de.onevision.math.Rot;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PdfInfo {
    public static PdfInfo init() {
        return new PdfInfo();
    }

    public void append(Element root) throws Error, NotImpl {
        fileInfos.add(new FileInfo());
        FileInfo current = fileInfo(fileInfos.size() - 1);
        String fileFormat = new String();
        boolean preSeparated = false;

        XmlUtil.checkNumAttributes(root, 5);
        current.filename = (String)XmlUtil.parseAttribute(root, "FileName", current.filename);
        fileFormat = (String)XmlUtil.parseAttribute(root, "FileFormat", fileFormat);
        current.version = (Double)XmlUtil.parseAttribute(root, "PDFVersion", current.version);
        current.pageCount = (Integer)XmlUtil.parseAttribute(root, "PageCount", current.pageCount);
        preSeparated = (Boolean)XmlUtil.parseAttribute(root, "PreSeparated", preSeparated);

        if(!fileFormat.equals("PDF")) {
            throw new Error("error when parsing input pdf", "", "file format: " + fileFormat);
        }
        if (preSeparated) {
            throw new NotImpl("pre-separated pdfs not supported");
        }

        appendPageInfos(root);
    }

    private void appendPageInfos(Element root) throws Error {
        FileInfo current = fileInfo(fileInfos.size() - 1);

        NodeList nodes = root.getElementsByTagName("PageInfo");
        if (current.pageCount != nodes.getLength()) {
            throw new Error("error when parsing input pdf", "", "number of page info nodes: " + Integer.toString(nodes.getLength()) + ", local page count: " + Integer.toString(current.pageCount));
        }

        for (int index = 0; index < nodes.getLength(); ++index) {
            Node node = nodes.item(index);
            pageInfos.add(computePageInfo((Element)node));
        }

    }

    private PageInfo computePageInfo(Element elem) throws Error {
        PageInfo pageInfo = new PageInfo();

        XmlUtil.checkNumAttributes(elem, 1, 2);

        pageInfo.globalIndex = overallPageCount++;

        pageInfo.localIndex = (Integer)XmlUtil.parseAttribute(elem, "PageIndex", pageInfo.localIndex);
        Double rotation = 0.0;
        pageInfo.rot = new Rot((Double)Helper.valueOr(XmlUtil.parseAttributeOpt(elem, "Rotation", rotation), rotation));

        appendBoxes(pageInfo, elem);

        NodeList nodes = elem.getElementsByTagName("ColorInfo");
        if(nodes.getLength() != 1) {
            throw new Error("error when parsing input pdf", "", "needs exactly one ColorInfo node");
        }
        Node node = nodes.item(0);
        pageInfo.colorInfo = computeColorInfo((Element)node);

        return pageInfo;
    }

    private void appendBoxes(PageInfo pageInfo, Element elem) throws Error {
        NodeList nodes = elem.getElementsByTagName("PageSize");
        if(nodes.getLength() == 0) {
            throw new Error("error when parsing input pdf", "", "no page size element");
        }
        Element pageSizeElem = (Element)nodes.item(0);

        Double width = 0.0;
        width = (Double)XmlUtil.parseElem(pageSizeElem, "Width", width);
        Double height = 0.0;
        height = (Double)XmlUtil.parseElem(pageSizeElem, "Height", height);

        Point pageSize = new Point(width, height);

        nodes = elem.getElementsByTagName("TrimBox");
        if(nodes.getLength() > 0) {
            pageInfo.box(Type.trim, computeBox((Element)nodes.item(0)));
        }

        nodes = elem.getElementsByTagName("CropBox");
        Box crop = new Box();
        if(nodes.getLength() > 0) {
            crop = computeBox((Element)nodes.item(0));
        }

        nodes = elem.getElementsByTagName("MediaBox");
        if(nodes.getLength() > 0) {
            pageInfo.box(Type.media, computeBox((Element)nodes.item(0)));
        }

        nodes = elem.getElementsByTagName("BleedBox");
        if(nodes.getLength() > 0) {
            pageInfo.box(Type.bleed, computeBox((Element)nodes.item(0)));
        }

        nodes = elem.getElementsByTagName("ArtBox");
        if(nodes.getLength() > 0) {
            pageInfo.box(Type.art, computeBox((Element)nodes.item(0)));
        }

        if(!pageInfo.box(Type.media).exists()) {
            pageInfo.box(Type.media, new Box(new Point(), pageSize));
        }

        if(!pageInfo.box(Type.trim).exists()) {
            pageInfo.box(Type.trim, crop);
            if(!pageInfo.box(Type.trim).exists()) {
                pageInfo.box(Type.trim, pageInfo.box(Type.media));
            }
        }
    }

    private Box computeBox(Element elem) throws Error {
        Double x0 = 0.0;
        x0 = (Double)XmlUtil.parseElem(elem, "X0", x0);
        Double y0 = 0.0;
        y0 = (Double)XmlUtil.parseElem(elem, "Y0", y0);
        Double x1 = 0.0;
        x1 = (Double)XmlUtil.parseElem(elem, "X1", x1);
        Double y1 = 0.0;
        y1 = (Double)XmlUtil.parseElem(elem, "Y1", y1);
        return new Box(new Point(x0, y0), new Point(x1, y1));
    }

    private ColorInfo computeColorInfo(Element elem) throws Error {
        ColorInfo colorInfo = new ColorInfo();

        XmlUtil.checkNumAttributes(elem, 4);
        Boolean cyan = false;
        cyan = (Boolean)XmlUtil.parseAttribute(elem, "Cyan", cyan);
        Boolean magenta = false;
        magenta = (Boolean)XmlUtil.parseAttribute(elem, "Magenta", magenta);
        Boolean yellow = false;
        yellow = (Boolean)XmlUtil.parseAttribute(elem, "Yellow", yellow);
        Boolean black = false;
        black = (Boolean)XmlUtil.parseAttribute(elem, "Black", black);

        colorInfo.cyan(cyan);
        colorInfo.cyan(magenta);
        colorInfo.cyan(yellow);
        colorInfo.cyan(black);

        NodeList nodes = elem.getElementsByTagName("CustomColor");
        for (int index = 0; index < nodes.getLength(); ++index) {
            Node subnode = nodes.item(index);
            colorInfo.add(computeSpotColor((Element)subnode));
        }

        return colorInfo;
    }

    private SpotColor computeSpotColor(Element elem) throws Error {
        String colorant = new String();
        Double cyan = 0.0;
        Double magenta = 0.0;
        Double yellow = 0.0;
        Double black = 0.0;

        colorant = (String)XmlUtil.parseAttribute(elem, "Name", colorant);
        cyan = (Double)XmlUtil.parseElem(elem, "Cyan", cyan);
        magenta = (Double)XmlUtil.parseElem(elem, "Magenta", magenta);
        yellow = (Double)XmlUtil.parseElem(elem, "Yellow", yellow);
        black = (Double)XmlUtil.parseElem(elem, "Black", black);

        SpotColor spotColor = new SpotColor();
        spotColor.color = new CMYK(cyan, magenta, yellow, black);
        spotColor.name = colorant;
        return spotColor;
    }

    public final FileInfo fileInfo(int fileIndex) {
        return fileInfos.get(fileIndex);
    }

    public final PageInfo pageInfo(int pageIndex) {
        return pageInfos.get(pageIndex);
    }

    public final ArrayList<FileInfo> fileInfos() {
        return fileInfos;
    }

    public final ArrayList<PageInfo> pageInfos() {
        return pageInfos;
    }

    public ColorInfo cumulate(int pageIndex, int size) {
        if(pageIndex < 0) {
            pageIndex = 0;
        }
        if(size < 0) {
            size = 0;
        }

        pageIndex = Math.min(pageIndex, pageInfos.size());
        int end = pageIndex + size;
        end = Math.min(pageIndex, pageInfos.size());

        ColorInfo ret = new ColorInfo();
        for (int index = pageIndex; index < end; ++index) {
            ret.append(pageInfos.get(index).colorInfo);
        }
        return ret;
    }

    public int overallPageCount() {
        return overallPageCount;
    }

    private PdfInfo() {}

    private int overallPageCount = 0;
    private ArrayList<FileInfo> fileInfos = new ArrayList<FileInfo>();
    private ArrayList<PageInfo> pageInfos = new ArrayList<PageInfo>();
}
