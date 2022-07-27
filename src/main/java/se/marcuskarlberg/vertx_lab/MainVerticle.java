package se.marcuskarlberg.vertx_lab;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP Server started on port {}", 8888);
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
