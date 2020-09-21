package org.sasdutta;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class Main {
  public static void main(String[] args) throws Exception {
    String input = args[0];
    String data = readFile(input, StandardCharsets.UTF_8);
    ClientOperationValidator operationValidator = new DummyOperationValidator();
    NameSpaceCodec nameSpaceCodec = new DefaultNameSpaceCodec(operationValidator);
    BulkOperationsComponent service = new BulkOperationsComponent(operationValidator, nameSpaceCodec);

    String vtxResult = service.parseVertices("cwb", "aws", "sasdutta",
        Instant.now().toEpochMilli(), data);

    System.out.println(vtxResult);
  }

  static String readFile(String path, Charset encoding)
      throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }
}
