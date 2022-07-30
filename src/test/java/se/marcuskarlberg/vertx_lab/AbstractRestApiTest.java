package se.marcuskarlberg.vertx_lab;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marcuskarlberg.vertx_lab.company.Company;
import se.marcuskarlberg.vertx_lab.config.ConfigLoader;

public class AbstractRestApiTest {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractRestApiTest.class);
  public static final int TEST_SERVER_PORT = 9000;

  protected static Company company;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    company = new Company("TestCompany999");

    System.setProperty(ConfigLoader.SERVER_PORT, String.valueOf(TEST_SERVER_PORT));
    System.setProperty(ConfigLoader.DB_HOST, "localhost");
    System.setProperty(ConfigLoader.DB_PORT, "5432");
    System.setProperty(ConfigLoader.DB_DATABASE, "vertx-lab");
    System.setProperty(ConfigLoader.DB_USER, "postgres");
    System.setProperty(ConfigLoader.DB_PASSWORD, "secret");
    LOG.warn("!!! Test are using local database !!!");
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }
}
