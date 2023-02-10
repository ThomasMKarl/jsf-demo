package de.onevision.marks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.Platform.Locations;
import de.onevision.color.ReplaceColor;
import de.onevision.color.SpotColor;
import de.onevision.config.Box;
import de.onevision.math.Point;
import de.onevision.math.TransMat;

public final class Image implements Mark {
    public Image() {
    }

    public Image(Path pathToFile, Point size) {
        this.pathToFile = pathToFile;
        this.clip = new Box();
        clip.width = size.x();
        clip.height = size.y();
        this.knockoutThickness = Optional.empty();
        parse();
    }

    public Optional<Double> knockoutThickness = Optional.empty();
    public Box clip = new Box();

    private Path pathToFile = Locations.storageBasePath();
    private Point size = new Point();
    private TransMat TM = TransMat.identity();
    private ArrayList<SpotColor> spotColors = new ArrayList<SpotColor>();
    private ArrayList<ReplaceColor> replaceColors = new ArrayList<ReplaceColor>();

    final public Path pathToFile() {
        return pathToFile;
    }

    public void pathToFile(Path path) {
        pathToFile = path;
        parse();
    }

    public void addReplaceColor(ReplaceColor color) {
        replaceColors.add(color);
    }

    final public ArrayList<ReplaceColor> replaceColors() {
        return replaceColors;
    }

    @Override
    public void transform(TransMat TM) {
        TM = this.TM.mult(TM);
    }

    @Override
    public Element generateXml(Document doc, Element elem) {
        Element markElem = (Element) elem.appendChild(doc.createElement("mark"));
        markElem.setAttribute("file", pathToFile.toString());
        markElem = TM.appendAttributes(markElem);
        markElem.setAttribute("clip", "0 0 " + Double.toString(clip.width) + " " + Double.toString(clip.height));
        if (!knockoutThickness.isEmpty()) {
            markElem.setAttribute("knockout", knockoutThickness.get().toString());
        }
        else {
            markElem.setAttribute("knockout", "0");
        }

        for (SpotColor spotColor : spotColors) {
            Element replaceElem = (Element) elem.appendChild(doc.createElement("replace"));
            replaceElem = spotColor.appendAttributes(replaceElem);
        }

        return elem;
    }

    @Override
    public void flatten() {
    }

    public void applyReplacement() {
        ArrayList<SpotColor> newSpots = new ArrayList<>();
        newSpots.ensureCapacity(spotColors.size());

        for (ReplaceColor replaceColor : replaceColors) {
            if (replaceColor.toReplace.isPresent()) {
                SpotColor repl = new SpotColor();
                repl.name = replaceColor.toReplace.get();
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

        spotColors = newSpots;
    }

    private void parse() {
        // STUB
    }
}
