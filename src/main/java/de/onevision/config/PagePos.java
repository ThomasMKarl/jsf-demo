package de.onevision.config;

import java.util.Optional;

public class PagePos {
  public Format format;
  public Optional<Reconcile> reconcile = Optional.empty();
  public boolean useMediaBox = false;
  public Rotation rotationAroundSelf = Rotation.up;
  public Alignment alignment = Alignment.bottomLeft;
  public Offset offset;
  public Scale scale;
  public double rotationAroundAngle = 0.0;
  public double bleedCut = 0.0;
  public double bleedBack = 0.0;
  public double bleedTop = 0.0;
  public double bleedBottom = 0.0;
}
