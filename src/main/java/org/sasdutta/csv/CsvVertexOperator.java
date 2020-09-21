package org.sasdutta.csv;

import org.sasdutta.ClientOperationValidator;
import org.sasdutta.NameSpaceCodec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CsvVertexOperator implements CsvOperator<EntityLine> {

  private final String clientApp;
  private final String businessLine;
  private final String createdBy;
  private final Long createdAt;
  private final ClientOperationValidator operationValidator;
  private final NameSpaceCodec nameSpaceCodec;

  public CsvVertexOperator(String clientApp, String businessLine,
                           String createdBy, Long createdAt,
                           ClientOperationValidator operationValidator,
                           NameSpaceCodec nameSpaceCodec
  ) {
    this.clientApp = clientApp;
    this.businessLine = businessLine;
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.operationValidator = operationValidator;
    this.nameSpaceCodec = nameSpaceCodec;
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
    if (operationValidator.canWriteVertex(clientApp, businessLine, line.getLabel())) {
      String id = nameSpaceCodec.encodeVertexId(clientApp, businessLine, line.getLabel(), line.getId());
      String label = nameSpaceCodec.encodeVertexLabel(clientApp, businessLine, line.getLabel());

      return String.join(",", id, label, createdBy, createdAt.toString());
    } else {
      String message = String.format("Client application '%s' is not authorised to create vertex with label '%s' in business line '%s'",
          clientApp, line.getLabel(), businessLine);

      throw new IllegalArgumentException(message);
    }
  }
}
