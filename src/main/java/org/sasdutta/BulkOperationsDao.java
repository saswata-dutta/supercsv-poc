package org.sasdutta;

import com.amazonaws.services.s3.AmazonS3;

public class BulkOperationsDao {
  private static final String bucket = "???"; // TODO read from config
  private static final String prefix = "bulk-csv/client-pushed";
  private static final String dependencyJsonTemplate = "[\"%s\",\"%s\"]";

  private final AmazonS3 s3Client;

  public BulkOperationsDao(AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  public void bulkUpload(String path, String vertices, String edges) {
    String verticesKey = String.join("/", prefix, path, "vertices.csv");
    String edgesKey = String.join("/", prefix, path, "edges.csv");

    String ingestionOrderKey = String.join("/", prefix, path, "order.json");
    String ingestionOrder = String.format(dependencyJsonTemplate, verticesKey, edgesKey);

    s3Client.putObject(bucket, verticesKey, vertices);
    s3Client.putObject(bucket, edgesKey, edges);
    s3Client.putObject(bucket, ingestionOrderKey, ingestionOrder);
  }
}
