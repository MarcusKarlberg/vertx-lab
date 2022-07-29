package se.marcuskarlberg.vertx_lab;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marcuskarlberg.vertx_lab.config.ConfigLoader;
import se.marcuskarlberg.vertx_lab.db.migration.FlywayMigration;

import java.util.function.Function;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> LOG.error("Unhandled: ", error));
    vertx.deployVerticle(new MainVerticle())
      .onFailure(e -> LOG.error("Failed to deploy main verticle: ", e))
      .onSuccess(id -> {
        LOG.info("Deployed {}, with id {}.", MainVerticle.class.getSimpleName(), id);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    migrateDatabase(startPromise);
    vertx.deployVerticle(new WebVerticle())
      .onFailure(e -> LOG.error("Failed to deploy {}: ", WebVerticle.class.getSimpleName(), e))
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", WebVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  private Function<String, Future<String>> deployWebVerticle(Promise<Void> startPromise) {
    return next -> vertx.deployVerticle(new WebVerticle())
      .onFailure(e -> LOG.error("Failed to deploy {}: ", WebVerticle.class.getSimpleName(), e))
      .onSuccess(id -> {
        LOG.info("Deployed {} with id {}", WebVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  private Future<Void> migrateDatabase(Promise<Void> startPromise) {
    return ConfigLoader.load(vertx)
      .compose(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig());
      })
      .onFailure(startPromise::fail)
      .onSuccess(id -> LOG.info("db migrated."));
  }
}
