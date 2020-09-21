package org.sasdutta;

public interface ClientOperationValidator {
  boolean canWriteVertex(String clientApp, String businessLine, String vertex);

  boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to);

  boolean isClientScopedVertex(String clientApp, String businessLine, String vertex);

  boolean isClientScopedEdge(String clientApp, String businessLine, String edge);
}

