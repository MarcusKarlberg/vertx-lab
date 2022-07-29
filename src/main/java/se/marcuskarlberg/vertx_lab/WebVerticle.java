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
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.marcuskarlberg.vertx_lab.company.CompanyRestApi;
import se.marcuskarlberg.vertx_lab.company.GetCompanyHandler;
import se.marcuskarlberg.vertx_lab.config.Config;
import se.marcuskarlberg.vertx_lab.config.ConfigLoader;

public class WebVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(WebVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        LOG.info("Retrieved Configuration: {}", config);
        startWebServer(startPromise, config);
      });
  }

  private void startWebServer(Promise<Void> startPromise, Config config) {

    vertx.createHttpServer()
      .requestHandler(setRouters(config))
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

  private Handler<HttpServerRequest> setRouters(Config config) {
    LOG.debug("Establishing Routers.");
    final Pool db = createDbPool(config);
    final Router router = Router.router(vertx);

    router.route()
      .handler(BodyHandler.create())
      .failureHandler(failureHandler());
    CompanyRestApi.attach(router, db);

    final JsonArray response = new JsonArray();
    response.add("Testing vertx!");

    CompanyRestApi.attach(router, db);

    return router;
  }

  private PgPool createDbPool(Config config) {
    //Create DB Pool
    final var connectionOptions = new PgConnectOptions()
      .setHost(config.getDbConfig().getHost())
      .setPort(config.getDbConfig().getPort())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions()
      .setMaxSize(4);

    return PgPool.pool(vertx, connectionOptions, poolOptions);
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
