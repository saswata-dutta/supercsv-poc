package org.sasdutta;

import org.sasdutta.csv.CsvEdgeOperator;
import org.sasdutta.csv.CsvOperator;
import org.sasdutta.csv.CsvProcessor;
import org.sasdutta.csv.CsvVertexOperator;
import org.sasdutta.csv.EntityLine;
import org.sasdutta.csv.RelationLine;

public class BulkOperationsComponent {

  private final ClientOperationValidator operationValidator;
  private final NameSpaceCodec nameSpaceCodec;

  public BulkOperationsComponent(ClientOperationValidator operationValidator, NameSpaceCodec nameSpaceCodec) {
    this.operationValidator = operationValidator;
    this.nameSpaceCodec = nameSpaceCodec;
  }

  public void bulkCreate(String clientApp, String businessLine,
                         String createdBy, Long createdAt,
                         String entities, String relations) {
    String parsedVertices = parseVertices(clientApp, businessLine, createdBy, createdAt, entities);
    String parsedEdges = parseEdges(clientApp, businessLine, createdBy, createdAt, relations);

    // call builder
  }

  String parseEdges(String clientApp, String businessLine,
                    String createdBy, Long createdAt,
                    String relations) {

    CsvOperator<RelationLine> edgeOp =
        new CsvEdgeOperator(clientApp, businessLine, createdBy, createdAt, operationValidator, nameSpaceCodec);

    return CsvProcessor.transform(relations, edgeOp);
  }

  String parseVertices(String clientApp, String businessLine,
                       String createdBy, Long createdAt,
                       String entities) {

    CsvOperator<EntityLine> vertexOp =
        new CsvVertexOperator(clientApp, businessLine, createdBy, createdAt, operationValidator, nameSpaceCodec);

    return CsvProcessor.transform(entities, vertexOp);
  }
}
