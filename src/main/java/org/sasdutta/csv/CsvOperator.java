package org.sasdutta.csv;

import java.util.List;
import java.util.Set;

public interface CsvOperator<T> {
  List<String> expectedColumns();
  String[] neptuneColumns();
  Class<T> csvBean();
}
