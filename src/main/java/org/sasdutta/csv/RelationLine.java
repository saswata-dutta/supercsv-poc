package org.sasdutta.csv;

public class RelationLine {
  private String type;
  private String fromType;
  private String fromId;
  private String toType;
  private String toId;
  private String confidence;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFromType() {
    return fromType;
  }

  public void setFromType(String fromType) {
    this.fromType = fromType;
  }

  public String getFromId() {
    return fromId;
  }

  public void setFromId(String fromId) {
    this.fromId = fromId;
  }

  public String getToType() {
    return toType;
  }

  public void setToType(String toType) {
    this.toType = toType;
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

    throw new IllegalArgumentException("Bad Confidence value " + confidence);
  }
}
