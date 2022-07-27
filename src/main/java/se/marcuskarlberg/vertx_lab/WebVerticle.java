package se.marcuskarlberg.vertx_lab;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marcuskarlberg.vertx_lab.config.Config;
import se.marcuskarlberg.vertx_lab.config.ConfigLoader;

public class WebVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(WebVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(brokerConfig -> {
        LOG.info("Retrieved Configuration: {}", brokerConfig);
        startWebServer(startPromise, brokerConfig);
      });
  }

  private void startWebServer(Promise<Void> startPromise, Config config) {
    vertx.createHttpServer()
      .requestHandler(setRouters())
      .exceptionHandler(error -> LOG.error("HTTP Server Error ", error))
      .listen(config.getServerPort(), httpResult -> {
        if(httpResult.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", config.getServerPort());
        } else {
          startPromise.fail(httpResult.cause());
        }
      });
  }

  private Handler<HttpServerRequest> setRouters() {
    LOG.debug("Establishing Routers.");
    final Router router = Router.router(vertx);
    router.route()
      .handler(BodyHandler.create())
      .failureHandler(failureHandler());

    final JsonArray response = new JsonArray();
    response.add("Testing vertx!");

    router.get("/test").handler(routingContext -> {
      LOG.info("Path {} responds with {}", routingContext.normalizedPath(), response.encode());
      routingContext.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(response.toBuffer());
    });

    return router;
  }

  private Handler<RoutingContext> failureHandler() {
    return errorCtx -> {
      if (errorCtx.response().ended()) {
        return;
      }
      LOG.error("Route Error:", errorCtx.failure());
      errorCtx.response()
        .setStatusCode(500)
        .end(new JsonObject()
          .put("message", "Something went wrong: ").toBuffer());
    };
  }
}
