package org.sasdutta.csv;

import java.util.Arrays;
import java.util.List;

public final class CsvVertexOperator implements CsvOperator<EntityLine> {
  private static final List<String> inputHeader = Arrays.asList(
      "id",
      "type"
  );

  @Override
  public List<String> expectedColumns() {
    return inputHeader;
  }

  private static final String[] neptuneHeader = {
      "~id",
      "~label",
      "createdBy:String(single)",
      "createdAt:Long(single)"
  };

  @Override
  public String[] neptuneColumns() {
    return neptuneHeader;
  }

  @Override
  public Class<EntityLine> csvBean() {
    return EntityLine.class;
  }
}
