package org.sasdutta.csv;

import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.StringReader;
import java.util.List;

public class CsvProcessor {
  private static final CellProcessor nonSpaceWord = new StrRegEx("[-\\w]+");

  /**
   * Transform client csv into Neptune Bulk loader format
   *
   * @param data
   * @return
   */
  public static String transform(String data, CsvOperator operator) {

    StringBuilder result = new StringBuilder();
    try (ICsvBeanReader beanReader = new CsvBeanReader(new StringReader(data), CsvPreference.STANDARD_PREFERENCE)) {

      final String[] headerColumns = beanReader.getHeader(true);
      result.append(String.join(",", operator.neptuneColumns()));
      result.append("\n");

      final CellProcessor[] cellProcessors = initCellProcessors(headerColumns, operator.expectedColumns());

      Object line;
      while ((line = beanReader.read(operator.csvBean(), headerColumns, cellProcessors)) != null) {
        result.append(line.toString());
        result.append("\n");
      }
    } catch (Exception ex) {
      throw new IllegalArgumentException("Failed to process input csv", ex);
    }

    return result.toString();
  }

  private static CellProcessor[] initCellProcessors(String[] inputColumns, List<String> expectedColumns) {
    final CellProcessor[] cellProcessors = new CellProcessor[inputColumns.length];

    int matchCount = 0;
    for (int i = 0; i < inputColumns.length; i++) {
      if (expectedColumns.contains(inputColumns[i])) {
        cellProcessors[i] = nonSpaceWord;
        ++matchCount;
      } else {
        cellProcessors[i] = null;
      }
    }

    if (matchCount != expectedColumns.size())
      throw new IllegalArgumentException("Header must contain columns : " + expectedColumns +
          "\t found : " + String.join(",", inputColumns));

    return cellProcessors;
  }
}
