package org.sasdutta;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sasdutta.csv.IdGenerator;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class BulkOperationsComponentTest {

  private static final String clientApp = "cwb";
  private static final String businessLine = "aws";
  private static final String createdBy = "sasdutta";
  private static final Instant createdAt = Instant.ofEpochSecond(1600750765L);

  private static final ClientOperationValidator operationValidator = new DummyOperationValidator();
  private static final NameSpaceCodec nameSpaceCodec = new DefaultNameSpaceCodec(operationValidator);

  private AmazonS3 s3Client;
  private BulkOperationsDao bulkOperationsDao;

  private IdGenerator idGenerator;
  private BulkOperationsBuilder bulkOperationsBuilder;
  private BulkOperationsComponent bulkOperationsComponent;

  @BeforeEach
  void setUp() {
    s3Client = mock(AmazonS3.class);
    bulkOperationsDao = new BulkOperationsDao(s3Client);
    idGenerator = mock(IdGenerator.class);
    bulkOperationsBuilder = new BulkOperationsBuilder(idGenerator, bulkOperationsDao);

    bulkOperationsComponent = new BulkOperationsComponent(operationValidator, nameSpaceCodec, bulkOperationsBuilder);
  }

  @Test
  @DisplayName("Should transform and persist csv in S3")
  void bulkCreate() {
    // arrange
    String entities =
        "Entity-id,Entity-type\n" +
            "123,customer\n" +
            "345,customer";

    String relations =
        "Relation-type,From-type,From-id,To-type,To-id,Confidence\n" +
            "has_customer,account,123,customer,c1,MANUAL\n" +
            "has_customer,customer,c1,customer,c2,MANUAL";

    when(idGenerator.generate()).thenReturn("_mocked_uuid_");
    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

    // act
    bulkOperationsComponent.bulkCreate(clientApp, businessLine, createdBy, createdAt, entities, relations);

    // assert
    verify(s3Client, times(3)).putObject(anyString(), keyCaptor.capture(), valueCaptor.capture());

    List<String> keys = keyCaptor.getAllValues();
    List<String> values = valueCaptor.getAllValues();

    assertEquals("bulk-csv/client-pushed/cwb/aws/2020/09/22/sasdutta/_mocked_uuid_/vertices.csv", keys.get(0));
    String vertices =
        "~id,~label,createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__customer$$123,cwb__aws__customer,sasdutta,1600750765\n" +
            "cwb__aws__customer$$345,cwb__aws__customer,sasdutta,1600750765\n";
    assertEquals(vertices, values.get(0));

    assertEquals("bulk-csv/client-pushed/cwb/aws/2020/09/22/sasdutta/_mocked_uuid_/edges.csv", keys.get(1));
    String edges =
        "~id,~label,~from,~to,Confidence:String(single),createdBy:String(single),createdAt:Long(single)\n" +
            "cwb__aws__has_customer##aws__account$$123##cwb__aws__customer$$c1,cwb__aws__has_customer,aws__account$$123,cwb__aws__customer$$c1,MANUAL,sasdutta,1600750765\n" +
            "cwb__aws__has_customer##cwb__aws__customer$$c1##cwb__aws__customer$$c2,cwb__aws__has_customer,cwb__aws__customer$$c1,cwb__aws__customer$$c2,MANUAL,sasdutta,1600750765\n";
    assertEquals(edges, values.get(1));

    assertEquals("bulk-csv/client-pushed/cwb/aws/2020/09/22/sasdutta/_mocked_uuid_/order.json", keys.get(2));
    String depenencyJson =
        "[\"bulk-csv/client-pushed/cwb/aws/2020/09/22/sasdutta/_mocked_uuid_/vertices.csv\"," +
            "\"bulk-csv/client-pushed/cwb/aws/2020/09/22/sasdutta/_mocked_uuid_/edges.csv\"]";
    assertEquals(depenencyJson, values.get(2));
  }

  @AfterEach
  void validate() {
    Mockito.validateMockitoUsage();
  }
}
