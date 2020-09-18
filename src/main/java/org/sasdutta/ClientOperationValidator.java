package org.sasdutta;

public interface ClientOperationValidator {
  boolean canWriteVertex(String clientApp, String businessLine, String vertex);

  boolean canWriteEdge(String clientApp, String businessLine, String edge, String from, String to);
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
}
