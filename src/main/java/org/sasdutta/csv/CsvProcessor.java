package org.sasdutta.csv;

import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.StringReader;
import java.util.Map;

public class CsvProcessor {
  private static final CellProcessor nonSpaceWord = new StrRegEx("[-\\w]+");

  /**
   * Transform client csv into Neptune Bulk loader format
   *
   * @param data     the client csv
   * @param operator the strategy to generate the corresponding Neptune format for bulk loading
   * @return lines of csv to be uploaded to neptune via the bulk loader
   */
  public static <T> String transform(String data, CsvOperator<T> operator) {

    StringBuilder result = new StringBuilder();
    try (ICsvBeanReader beanReader = new CsvBeanReader(new StringReader(data), CsvPreference.STANDARD_PREFERENCE)) {

      final String[] headerColumns = beanReader.getHeader(true);
      result.append(String.join(",", operator.neptuneColumns()));
      result.append("\n");

      final CellProcessor[] cellProcessors = selectExpectedColumns(headerColumns, operator.expectedColumns());

      T line;
      while ((line = beanReader.read(operator.csvBean(), headerColumns, cellProcessors)) != null) {
        result.append(operator.neptuneLine(line));
        result.append("\n");
      }
    } catch (SuperCsvException ex) {
      String message = String.format("Failed to parse csv: %s\n at:\n%s", ex.getMessage(), ex.getCsvContext());
      throw new IllegalArgumentException(message, ex);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Failed to process csv", ex);
    }

    return result.toString();
  }

  /**
   * Identifies the columns to process from client csv,
   * nulls out extra ones so error in those shouldn't affect processing
   *
   * @param inputColumns
   * @param expectedColumns
   * @return
   */
  private static CellProcessor[] selectExpectedColumns(String[] inputColumns, Map<String, String> expectedColumns) {
    final String[] originalCols = inputColumns.clone();
    final CellProcessor[] cellProcessors = new CellProcessor[inputColumns.length];

    int matchCount = 0;
    for (int i = 0; i < inputColumns.length; i++) {

      if (expectedColumns.containsKey(inputColumns[i])) {
        cellProcessors[i] = nonSpaceWord;
        inputColumns[i] = expectedColumns.get(inputColumns[i]);
        ++matchCount;
      } else {
        // ignoring surplus cols
        cellProcessors[i] = null;
        inputColumns[i] = null;
      }
    }

    if (matchCount != expectedColumns.size())
      throw new IllegalArgumentException("Header must contain columns : " + expectedColumns.keySet() +
          "\t but found : " + String.join(",", originalCols));

    return cellProcessors;
  }
}
