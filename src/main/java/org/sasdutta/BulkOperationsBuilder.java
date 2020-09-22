package org.sasdutta;

import org.sasdutta.csv.IdGenerator;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class BulkOperationsBuilder {
  private final IdGenerator idGenerator;
  private final BulkOperationsDao bulkOperationsDao;

  public BulkOperationsBuilder(IdGenerator idGenerator, BulkOperationsDao bulkOperationsDao) {
    this.idGenerator = idGenerator;
    this.bulkOperationsDao = bulkOperationsDao;
  }

  public void bulkUpload(String clientApp, String businessLine,
                         String createdBy, Instant createdAt,
                         String vertices, String edges) {
    // adding a random id to prevent parallel calls from getting clobbered
    String id = idGenerator.generate();
    String path = fileNameGenerator(clientApp, businessLine, createdAt, createdBy, id);

    bulkOperationsDao.bulkUpload(path, vertices, edges);
  }

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  private static String fileNameGenerator(String clientApp, String businessLine,
                                          Instant createdAt, String createdBy, String id) {

    OffsetDateTime odt = createdAt.atOffset(ZoneOffset.UTC);
    String date = odt.format(formatter);

    return String.join("/", clientApp, businessLine, date, createdBy, id);
  }
}
