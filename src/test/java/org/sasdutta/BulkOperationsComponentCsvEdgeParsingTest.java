package org.sasdutta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BulkOperationsComponentCsvEdgeParsingTest {

  final ClientOperationValidator operationValidator = new DummyOperationValidator();
  final NameSpaceCodec nameSpaceCodec = new DefaultNameSpaceCodec(operationValidator);
  final BulkOperationsComponent bulkOperationsComponent = new BulkOperationsComponent(operationValidator, nameSpaceCodec, null);

  final String clientApp = "cwb";
  final String businessLine = "aws";
  final String createdBy = "sasdutta";
  final long createdAt = 1600750765877L;


  @Test
  @DisplayName("Should transform input relations csv")
  void parseEdges() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,MANUAL\n";

    String actual = bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input);

    String expected =
        "~id,~label,~from,~to,Confidence:String(single),createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__has_customer##aws__account$$123##cwb__aws__customer$$c1,cwb__aws__has_customer,aws__account$$123,cwb__aws__customer$$c1,MANUAL,sasdutta,1600750765877\n" +
            "cwb__aws__has_customer##cwb__aws__customer$$c1##cwb__aws__customer$$c2,cwb__aws__has_customer,cwb__aws__customer$$c1,cwb__aws__customer$$c2,MANUAL,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input relations csv even if columns are unordered")
  void parseEdges_unordered_columns() {
    String input =
        "From-type,From-id,To-type,Confidence,Relation-type,To-id\n" +
            "account,123,customer,MANUAL,has_customer,c1\n" +
            "customer,c1,customer,MANUAL,has_customer,c2\n";

    String actual = bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input);

    String expected =
        "~id,~label,~from,~to,Confidence:String(single),createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__has_customer##aws__account$$123##cwb__aws__customer$$c1,cwb__aws__has_customer,aws__account$$123,cwb__aws__customer$$c1,MANUAL,sasdutta,1600750765877\n" +
            "cwb__aws__has_customer##cwb__aws__customer$$c1##cwb__aws__customer$$c2,cwb__aws__has_customer,cwb__aws__customer$$c1,cwb__aws__customer$$c2,MANUAL,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input relations csv even if extra columns are present")
  void parseEdges_extra_columns() {
    String input =
        "extra,From-type,From-id,To-type,foo,Confidence,Relation-type,To-id,dummy\n" +
            "extra,account,123,customer,foo,MANUAL,has_customer,c1,dummy\n" +
            "extra,customer,c1,customer,foo,MANUAL,has_customer,c2,dummy\n";

    String actual = bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input);

    String expected =
        "~id,~label,~from,~to,Confidence:String(single),createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__has_customer##aws__account$$123##cwb__aws__customer$$c1,cwb__aws__has_customer,aws__account$$123,cwb__aws__customer$$c1,MANUAL,sasdutta,1600750765877\n" +
            "cwb__aws__has_customer##cwb__aws__customer$$c1##cwb__aws__customer$$c2,cwb__aws__has_customer,cwb__aws__customer$$c1,cwb__aws__customer$$c2,MANUAL,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should transform input relations csv even if extra columns are malformed")
  void parseEdges_extra_malformed_columns() {
    String input =
        "extra,From-type,From-id,To-type,foo,Confidence,Relation-type,To-id,dummy\n" +
            "  extra,account,123,customer, ,MANUAL,has_customer,c1,dum%%my\n" +
            "extra  ,customer,c1,customer,,MANUAL,has_customer,c2,\n";

    String actual = bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input);

    String expected =
        "~id,~label,~from,~to,Confidence:String(single),createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__has_customer##aws__account$$123##cwb__aws__customer$$c1,cwb__aws__has_customer,aws__account$$123,cwb__aws__customer$$c1,MANUAL,sasdutta,1600750765877\n" +
            "cwb__aws__has_customer##cwb__aws__customer$$c1##cwb__aws__customer$$c2,cwb__aws__has_customer,cwb__aws__customer$$c1,cwb__aws__customer$$c2,MANUAL,sasdutta,1600750765877\n";

    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Should reject input relations csv with malformed data")
  void parseEdges_reject_malformed() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c 1,customer,c2,MANUAL\n";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous row is reported
    assertTrue(ex.getMessage().matches("(?s).*has_customer,\\s?customer,\\s?c 1,\\s?customer,\\s?c2,\\s?MANUAL.*"));
  }

  @Test
  @DisplayName("Should reject input relations csv with missing data")
  void parseEdges_reject_missing_values() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,\n";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous row is reported
    assertTrue(ex.getMessage().matches("(?s).*has_customer,\\s?customer,\\s?c1,\\s?customer,\\s?c2,.*"));
  }

  @Test
  @DisplayName("Should reject input relations csv with missing columns")
  void parseEdges_reject_missing_cols() {
    String input =
        "Relation,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,MANUAL\n";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous header is reported
    assertTrue(ex.getMessage().matches("(?s).*Relation,\\s?From-type,\\s?From-id,\\s?To-type,\\s?To-id,\\s?Confidence.*"));
  }

  @Test
  @DisplayName("Should reject input relations csv which violates schema")
  void parseEdges_reject_schema_violations() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_payer_account,account,123,account,345,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,MANUAL\n";

    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));

    // check the erroneous column value is reported
    assertTrue(ex.getMessage().contains("'has_payer_account'"));
  }

  @Test
  @DisplayName("Should reject input relations csv which has invalid confidence value")
  void parseEdges_invalid_confidence() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,UNKNOWN\n";

    assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));
  }

  @Test
  @DisplayName("Should reject input relations csv which has unequal header and data columns")
  void parseEdges_unequal_cols() {
    String input =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence,extra\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,MANUAL\n";

    assertThrows(IllegalArgumentException.class,
        () -> bulkOperationsComponent.parseEdges(clientApp, businessLine, createdBy, createdAt, input));
  }
}
