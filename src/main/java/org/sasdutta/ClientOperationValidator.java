package org.sasdutta;

public interface ClientOperationValidator {
  boolean canWriteVertex(String clientApp, String businessLine, String vertex);

  boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to);

  boolean isClientScopedVertex(String clientApp, String businessLine, String vertex);

  boolean isClientScopedEdge(String clientApp, String businessLine, String edge);
}

class TruthyClientOperationValidator implements ClientOperationValidator {
  @Override
  public boolean canWriteVertex(String clientApp, String businessLine, String vertex) {
    return true;
  }

  @Override
  public boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to) {
    return true;
  }

  @Override
  public boolean isClientScopedVertex(String clientApp, String businessLine, String vertex) {
    return false;
  }

  @Override
  public boolean isClientScopedEdge(String clientApp, String businessLine, String edge) {
    return false;
  }
}

class FalsyClientOperationValidator implements ClientOperationValidator {
  @Override
  public boolean canWriteVertex(String clientApp, String businessLine, String vertex) {
    return false;
  }

  @Override
  public boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to) {
    return false;
  }

  @Override
  public boolean isClientScopedVertex(String clientApp, String businessLine, String vertex) {
    return false;
  }

  @Override
  public boolean isClientScopedEdge(String clientApp, String businessLine, String edge) {
    return false;
  }
}
