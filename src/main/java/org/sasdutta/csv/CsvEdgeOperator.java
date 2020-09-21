package org.sasdutta.csv;

import org.sasdutta.ClientOperationValidator;
import org.sasdutta.NameSpaceCodec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsvEdgeOperator implements CsvOperator<RelationLine> {

  private final String clientApp;
  private final String businessLine;
  private final String createdBy;
  private final Long createdAt;
  private final ClientOperationValidator operationValidator;
  private final NameSpaceCodec nameSpaceCodec;

  public CsvEdgeOperator(String clientApp, String businessLine,
                         String createdBy, Long createdAt,
                         ClientOperationValidator operationValidator, NameSpaceCodec nameSpaceCodec) {
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
    map.put("Relation-type", "label");
    map.put("From-type", "fromLabel");
    map.put("From-id", "fromId");
    map.put("To-type", "toLabel");
    map.put("To-id", "toId");
    map.put("Confidence", "confidence");

    columnMap = Collections.unmodifiableMap(map);
  }

  @Override
  public Map<String, String> expectedColumns() {
    return columnMap;
  }

  private static final String[] neptuneHeader = {
      "~id",
      "~label",
      "~from",
      "~to",
      "Confidence:String(single)",
      "createdBy:String(single)",
      "createdAt:Long(single)"
  };

  @Override
  public String[] neptuneColumns() {
    return neptuneHeader;
  }

  @Override
  public Class<RelationLine> csvBean() {
    return RelationLine.class;
  }

  @Override
  public String neptuneLine(RelationLine line) {
    if (operationValidator.canWriteEdge(clientApp, businessLine, line.getLabel(), line.getFromLabel(), line.getToLabel())) {
      String label = nameSpaceCodec.encodeEdgeLabel(clientApp, businessLine, line.getLabel());
      String from = nameSpaceCodec.encodeVertexId(clientApp, businessLine, line.getFromLabel(), line.getFromId());
      String to = nameSpaceCodec.encodeVertexId(clientApp, businessLine, line.getToLabel(), line.getToId());
      String id = nameSpaceCodec.encodeEdgeId(clientApp, businessLine, line.getLabel(),
          line.getFromLabel(), from, line.getToLabel(), to);

      return String.join(",", id, label, from, to, line.getConfidence(), createdBy, createdAt.toString());
    } else {
      String message = String.format("Client application '%s' is not authorised to create edge '%s' " +
              "between vertices '%s' and '%s', in business line '%s'",
          clientApp, line.getLabel(), line.getFromLabel(), line.getToLabel(), businessLine);

      throw new IllegalArgumentException(message);
    }
  }
}
