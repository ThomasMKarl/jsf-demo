package de.onevision.marks;

import java.util.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.onevision.font.Segment;
import de.onevision.math.TransMat;

public final class Barcode implements Mark {
  public BarcodeStyle barcodeStyle;
  public BarcodeSize barcodeSize;
  public Segment segment;
  public BarcodeCopies barcodeCopies;
  public boolean displayText = false;
  public Optional<Double> knockoutThickness = Optional.empty();
  private TransMat TM = TransMat.identity();

  @Override
  public void transform(TransMat TM) {
      TM = this.TM.mult(TM);
  }

  @Override
  public Element generateXml(Document doc, Element elem) {
      // TBI
      return elem;
  }

  @Override
  public void flatten() {
  }
}