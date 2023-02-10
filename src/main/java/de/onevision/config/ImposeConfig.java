package de.onevision.config;

import java.util.ArrayList;
import java.util.Optional;

import de.onevision.model.Model;

public class ImposeConfig {
  public Output output;
  public PagePos pagePos;
  public Optional<StepAndRepeat> stepAndRepeat = Optional.empty();
  public Model model;
  public ArrayList<Flat> flats = new ArrayList<Flat>();;
}
