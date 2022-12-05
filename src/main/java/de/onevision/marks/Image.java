package de.onevision.marks;

import java.nio.file.Path;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.Platform.Locations;
import de.onevision.color.ReplaceColor;
import de.onevision.color.SpotColor;
import de.onevision.math.Point;
import de.onevision.math.TransMat;

public final class Image implements Mark {
    public Image(Path pathToFile, Point size, double knockoutStrength) {
        this.pathToFile = pathToFile;
        this.size = size;
        this.knockoutStrength = knockoutStrength;
        parse();
    }

    private double knockoutStrength = 0;
    private Point size = new Point();
    private Path pathToFile = Locations.storageBasePath();
    private TransMat TM = TransMat.identity();

    private ArrayList<SpotColor> spotColors = new ArrayList<SpotColor>();
    private ArrayList<ReplaceColor> replaceColors = new ArrayList<ReplaceColor>();

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element markElem = (Element) elem.appendChild(doc.createElement("mark"));
        markElem.setAttribute("file", pathToFile.toString());
        markElem = TM.appendAttributes(markElem);
        markElem.setAttribute("clip", "0 0 " + Double.toString(size.x()) + " " + Double.toString(size.y()));
        markElem.setAttribute("knockout", Double.toString(knockoutStrength));

        for (ReplaceColor replaceColor : replaceColors) {
            Element replaceElem = (Element) elem.appendChild(doc.createElement("replace"));
            replaceElem = replaceColor.replacement.appendAttributes(replaceElem);
        }

        return elem;
    }

    @Override
    public void flatten() {
    }

    public void replaceColors() {
        ArrayList<SpotColor> newSpots = new ArrayList<>();
        newSpots.ensureCapacity(spotColors.size());

        for (ReplaceColor replaceColor : replaceColors) {
            if (replaceColor.toReplace.isPresent()) {
                SpotColor repl = new SpotColor();
                repl.colorant = replaceColor.toReplace.get();
                int index = spotColors.lastIndexOf(repl);
                if (index >= 0) {
                    newSpots.add(index, replaceColor.replacement);
                }
            }
        }

        replaceColors.removeIf(r -> r.toReplace.isPresent());

        for (int index = 0; index < spotColors.size(); ++index) {
            SpotColor replace = spotColors.get(index);
            if (index < replaceColors.size()) {
                replace = replaceColors.get(index).replacement;
            }
            if(newSpots.get(index) == null) {
                newSpots.add(index, replace);
            }
        }
    }

    private void parse() {
        // STUB
    }
}
