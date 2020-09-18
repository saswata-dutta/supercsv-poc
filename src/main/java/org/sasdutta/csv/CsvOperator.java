package org.sasdutta.csv;

import java.util.Map;

public interface CsvOperator<T> {
  Map<String, String> expectedColumns();

  String[] neptuneColumns();

  Class<T> csvBean();

  String neptuneLine(T line);
}
