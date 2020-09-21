package org.sasdutta;

import java.util.regex.Pattern;

class DefaultNameSpaceCodec implements NameSpaceCodec {
  private static final String LABEL_SEPARATOR = "__";
  private static final String VERTEX_ID_SEPARATOR = "$$";
  private static final String EDGE_ID_SEPARATOR = "##";

  private final ClientOperationValidator operationValidator;

  public DefaultNameSpaceCodec(ClientOperationValidator operationValidator) {
    this.operationValidator = operationValidator;
  }

  @Override
  public String encodeVertexLabel(String clientApp, String businessLine, String label) {
    if (operationValidator.isClientScopedVertex(clientApp, businessLine, label)) {
      return String.join(LABEL_SEPARATOR, clientApp, businessLine, label);
    } else {
      return String.join(LABEL_SEPARATOR, businessLine, label);
    }
  }

  @Override
  public String encodeVertexId(String clientApp, String businessLine, String label, String id) {
    String encodedLabel = encodeVertexLabel(clientApp, businessLine, label);
    return String.join(VERTEX_ID_SEPARATOR, encodedLabel, id);
  }

  @Override
  public String decodeVertexId(String id) {
    String[] parts = id.split(Pattern.quote(VERTEX_ID_SEPARATOR));
    if (parts.length != 2)
      throw new IllegalArgumentException("Found malformed namespaced vertex: " + id);

    return parts[1];
  }

  @Override
  public String encodeEdgeLabel(String clientApp, String businessLine, String label) {
    if (operationValidator.isClientScopedEdge(clientApp, businessLine, label)) {
      return String.join(LABEL_SEPARATOR, clientApp, businessLine, label);
    } else {
      return String.join(LABEL_SEPARATOR, businessLine, label);
    }
  }

  @Override
  public String encodeEdgeId(String clientApp, String businessLine, String label,
                             String fromLabel, String fromId,
                             String toLabel, String toId) {

    String encodedLabel = encodeEdgeLabel(clientApp, businessLine, label);
    String encodedFromVertex = encodeVertexId(clientApp, businessLine, fromLabel, fromId);
    String encodedToVertex = encodeVertexId(clientApp, businessLine, toLabel, toId);

    return String.join(EDGE_ID_SEPARATOR, encodedLabel, encodedFromVertex, encodedToVertex);
  }

}
