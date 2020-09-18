package org.sasdutta.csv;

public class RelationLine {
  private String label;
  private String fromLabel;
  private String fromId;
  private String toLabel;
  private String toId;
  private String confidence;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getFromLabel() {
    return fromLabel;
  }

  public void setFromLabel(String fromLabel) {
    this.fromLabel = fromLabel;
  }

  public String getFromId() {
    return fromId;
  }

  public void setFromId(String fromId) {
    this.fromId = fromId;
  }

  public String getToLabel() {
    return toLabel;
  }

  public void setToLabel(String toLabel) {
    this.toLabel = toLabel;
  }

  public String getToId() {
    return toId;
  }

  public void setToId(String toId) {
    this.toId = toId;
  }

  public String getConfidence() {
    return confidence;
  }

  private static final String[] confidenceValues = {
      "GROUND_TRUTH", "AUTO", "MANUAL",
      "HEURISTIC", "VERIFIED", "REJECTED"
  };

  public void setConfidence(String confidence) {
    for (String allowedValue : confidenceValues) {
      if (allowedValue.equals(confidence)) {
        this.confidence = confidence;
        return;
      }
    }

    throw new IllegalArgumentException("Bad Confidence value: " + confidence);
  }
}
