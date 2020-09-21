package org.sasdutta;

import java.util.regex.Pattern;

public interface NameSpaceCodec {
  String encodeVertexLabel(String clientApp, String businessLine, String label);

  String encodeVertexId(String clientApp, String businessLine, String label, String id);

  String decodeVertexId(String id);

  String encodeEdgeLabel(String clientApp, String businessLine, String label);

  String encodeEdgeId(String clientApp, String businessLine, String label,
                      String fromLabel, String fromId,
                      String toLabel, String toId);
}

