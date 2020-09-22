package org.sasdutta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DummyOperationValidator implements ClientOperationValidator {
  private static final String allowedClient = "cwb";
  private static final String allowedBusiness = "aws";

  private static final List<String> readableVertices = Collections.unmodifiableList(Arrays.asList("account", "customer"));
  private static final List<String> writableVertices = Collections.unmodifiableList(Arrays.asList("customer"));
  private static final List<String> clientScopedVertices = writableVertices;

  private static final List<String> readableEdges = Collections.unmodifiableList(Arrays.asList("has_payer_account", "has_customer"));
  private static final List<String> writableEdges = Collections.unmodifiableList(Arrays.asList("has_customer"));
  private static final List<String> clientScopedEdges = writableEdges;

  @Override
  public boolean canWriteVertex(String clientApp, String businessLine, String vertex) {
    return allowedClient.equals(clientApp) &&
        allowedBusiness.equals(businessLine) &&
        writableVertices.contains(vertex);
  }

  @Override
  public boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to) {
    return allowedClient.equals(clientApp) &&
        allowedBusiness.equals(businessLine) &&
        writableEdges.contains(edge);
  }

  @Override
  public boolean isClientScopedVertex(String clientApp, String businessLine, String vertex) {
    return allowedClient.equals(clientApp) &&
        allowedBusiness.equals(businessLine) &&
        clientScopedVertices.contains(vertex);
  }

  @Override
  public boolean isClientScopedEdge(String clientApp, String businessLine, String edge) {
    return allowedClient.equals(clientApp) &&
        allowedBusiness.equals(businessLine) &&
        clientScopedEdges.contains(edge);
  }
}
