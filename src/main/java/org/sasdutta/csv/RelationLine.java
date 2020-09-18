package org.sasdutta.csv;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class RelationLine {
  @NonNull
  private String label;
  @NonNull
  private String fromLabel;
  @NonNull
  private String fromId;
  @NonNull
  private String toLabel;
  @NonNull
  private String toId;
  @NonNull
  private String confidence;

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
