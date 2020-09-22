package org.sasdutta;

public class BulkOperationsDao {
  private static final String bucket = "???"; // TODO from config
  private static final String prefix = "bulk-csv/client-pushed";

  // TODO inject s3Client

  public void bulkUpload(String path, String vertices, String edges) {
    String verticesKey = String.join("/", prefix, path, "vertices.csv");
    String edgesKey = String.join("/", prefix, path, "edges.csv");

    String ingestionOrderKey = String.join("/", prefix, path, "order.json");
    String ingestionOrder = String.format("[ \"%s\", \"%s\" ]", verticesKey, edgesKey);

//    s3Client.putObject(bucket, verticesKey, vertices);
//    s3Client.putObject(bucket, edgesKey, edges);
//    s3Client.putObject(bucket, ingestionOrderKey, ingestionOrder);
  }
}
