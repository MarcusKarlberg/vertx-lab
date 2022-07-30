package se.marcuskarlberg.vertx_lab.company;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marcuskarlberg.vertx_lab.AbstractRestApiTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestCompanyRestAPI extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(TestCompanyRestAPI.class);

  private static final String PATH = "/companies";

  @Test
  void createCompanyTest(Vertx vertx, VertxTestContext vertxTestContext) {
    var client = WebClient
      .create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));

    client.put(PATH)
      .sendJsonObject(new JsonObject(company.toJsonObject().toBuffer()))
      .onComplete(vertxTestContext.succeeding(response -> {
        assertEquals(200, response.statusCode());
        vertxTestContext.completeNow();
      }));
  }

  @Test
  void getCompaniesTest(Vertx vertx, VertxTestContext vertxTestContext) {
    var client = WebClient
      .create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));

    client.get(PATH).send()
      .onComplete(vertxTestContext.succeeding(response -> {
        assertEquals(200, response.statusCode());
        var result = response.bodyAsJsonArray();
        LOG.info("Response {}", result);
        assertEquals("{\"company\":\"TestCompany999\"}", company.toJsonObject().toString());
        vertxTestContext.completeNow();
      }));
  }

  @Test
  void deleteCompaniesTest(Vertx vertx, VertxTestContext vertxTestContext) {
    var client = WebClient
      .create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    final String path = PATH.concat("/").concat(company.getCompany());

    client.delete(path).send()
      .onComplete(vertxTestContext.succeeding(response -> {
        assertEquals(204, response.statusCode());
        vertxTestContext.completeNow();
      }));
  }
}
