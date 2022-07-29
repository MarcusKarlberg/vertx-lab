package se.marcuskarlberg.vertx_lab.db;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbResponse {
  private static final Logger LOG = LoggerFactory.getLogger(DbResponse.class);

  public static Handler<Throwable> errorHandler(final RoutingContext routingContext, String message) {
    return error -> {
      LOG.error("Failure: ", error);
      routingContext.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(new JsonObject()
          .put("message", message)
          .put("path", routingContext.normalizedPath())
          .toBuffer()
        );
    };
  }

  public static void notFoundResponse(RoutingContext routingContext, String message) {
    routingContext.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
      .end(new JsonObject()
        .put("message", message)
        .put("path", routingContext.normalizedPath())
        .toBuffer());
  }
}
