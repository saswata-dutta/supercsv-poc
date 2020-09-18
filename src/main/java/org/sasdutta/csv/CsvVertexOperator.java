package org.sasdutta.csv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CsvVertexOperator implements CsvOperator<EntityLine> {

  private final String clientApp;
  private final String businessLine;
  private final String createdBy;
  private final Long createdAt;

  public CsvVertexOperator(String clientApp, String businessLine, String createdBy, long createdAt) {
    this.clientApp = clientApp;
    this.businessLine = businessLine;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
  }

  private static final Map<String, String> columnMap;

  static {
    Map<String, String> map = new HashMap<>();
    // input col -> bean field
    map.put("Entity-id", "id");
    map.put("Entity-type", "label");

    columnMap = Collections.unmodifiableMap(map);
  }

  @Override
  public Map<String, String> expectedColumns() {
    return columnMap;
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

  @Override
  public String neptuneLine(EntityLine line) {
    // todo validate and namesapce
    return String.join(",", line.getId(), line.getLabel(), createdBy, createdAt.toString());
  }
}
