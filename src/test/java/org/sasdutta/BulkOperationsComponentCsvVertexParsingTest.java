package org.sasdutta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BulkOperationsComponentCsvVertexParsingTest {

  final ClientOperationValidator operationValidator = new DummyOperationValidator();
  final NameSpaceCodec nameSpaceCodec = new DefaultNameSpaceCodec(operationValidator);
  final BulkOperationsComponent bulkOperationsComponent = new BulkOperationsComponent(operationValidator, nameSpaceCodec, null);

  final String clientApp = "cwb";
  final String businessLine = "aws";
  final String createdBy = "sasdutta";
  final long createdAt = 1600750765877L;


  @Test
  @DisplayName("Should transform input entities csv")
  void parseVertices() {
    String input =
        "Entity-id,Entity-type\n" +
            "123,customer\n" +
            "345,customer";

    String actual = bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input);

    String expected = "~id,~label,createdBy:String(single),createdAt:Long(single)\n" +
        "cwb__aws__customer$$123,cwb__aws__customer,sasdutta,1600750765877\n" +
        "cwb__aws__customer$$345,cwb__aws__customer,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input entities csv even if columns are unordered")
  void parseVertices_unordered_columns() {
    String input =
        "Entity-type,Entity-id\n" +
            "customer,123\n" +
            "customer,345";

    String actual = bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input);

    String expected = "~id,~label,createdBy:String(single),createdAt:Long(single)\n" +
        "cwb__aws__customer$$123,cwb__aws__customer,sasdutta,1600750765877\n" +
        "cwb__aws__customer$$345,cwb__aws__customer,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input entities csv even if extra columns are present")
  void parseVertices_extra_columns() {
    String input =
        "extra,Entity-type,pad,Entity-id,dummy\n" +
            "extra,customer,pad,123,dummy\n" +
            "extra,customer,pad,345,dummy";

    String actual = bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input);

    String expected = "~id,~label,createdBy:String(single),createdAt:Long(single)\n" +
        "cwb__aws__customer$$123,cwb__aws__customer,sasdutta,1600750765877\n" +
        "cwb__aws__customer$$345,cwb__aws__customer,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input entities csv even if extra columns are malformed")
  void parseVertices_extra_malformed_columns() {
    String input =
        "extra,Entity-type,pad,Entity-id,dummy\n" +
            ",customer,a b,123,$\n" +
            "   ,customer,%%,345,";

    String actual = bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input);

    String expected = "~id,~label,createdBy:String(single),createdAt:Long(single)\n" +
        "cwb__aws__customer$$123,cwb__aws__customer,sasdutta,1600750765877\n" +
        "cwb__aws__customer$$345,cwb__aws__customer,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should reject input entities csv with malformed data")
  void parseVertices_reject_malformed() {
    String input =
        "Entity-id,Entity-type\n" +
            "12 3,customer\n" +
            "345,customer";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous row is reported
    assertTrue(ex.getMessage().matches("(?s).*12 3,\\s?customer.*"));
  }

  @Test
  @DisplayName("Should reject input entities csv with missing data")
  void parseVertices_reject_missing_values() {
    String input =
        "Entity-id,Entity-type\n" +
            "123,customer\n" +
            "345,";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous row is reported
    assertTrue(ex.getMessage().matches("(?s).*345,.*"));
  }

  @Test
  @DisplayName("Should reject input entities csv with missing columns")
  void parseVertices_reject_missing_cols() {
    String input =
        "Entity-id,Entity-x\n" +
            "123,customer\n" +
            "345,customer";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous header is reported
    assertTrue(ex.getMessage().matches("(?s).*Entity-id,\\s?Entity-x.*"));
  }

  @Test
  @DisplayName("Should reject input entities csv which violates schema")
  void parseVertices_reject_schema_violations() {
    String input =
        "Entity-id,Entity-type\n" +
            "123,customer\n" +
            "345,account";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous column value is reported
    assertTrue(ex.getMessage().contains("'account'"));
  }

  @Test
  @DisplayName("Should reject input entities csv which has unequal header and data columns")
  void parseVertices_unequal_cols() {
    String input =
        "Entity-id,Entity-type,extra\n" +
            "123,customer\n" +
            "345,account";

    assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseVertices(clientApp, businessLine, createdBy, createdAt, input));
  }
}
