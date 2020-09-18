package org.sasdutta.csv;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class EntityLine {
  @NonNull
  private String id;
  @NonNull
  private String label;
}
