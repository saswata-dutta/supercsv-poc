package org.sasdutta;

import org.sasdutta.csv.CsvOperator;
import org.sasdutta.csv.CsvProcessor;
import org.sasdutta.csv.CsvVertexOperator;
import org.sasdutta.csv.EntityLine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) throws Exception {
    String input = args[0];
    String data = readFile(input, StandardCharsets.UTF_8);
    CsvOperator<EntityLine> vertexOp = new CsvVertexOperator();
    System.out.println(CsvProcessor.transform(data, vertexOp));
  }

  static String readFile(String path, Charset encoding)
      throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }
}
