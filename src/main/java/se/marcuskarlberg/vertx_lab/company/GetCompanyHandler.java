package se.marcuskarlberg.vertx_lab.company;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static se.marcuskarlberg.vertx_lab.db.DbResponse.errorHandler;
import static se.marcuskarlberg.vertx_lab.db.DbResponse.notFoundResponse;

public class GetCompanyHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetCompanyHandler.class);

  private final Pool db;

  public GetCompanyHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    SqlTemplate.forQuery(db, "SELECT c.company FROM lab.companies c")
      .mapTo(Company.class)
      .execute(Collections.singletonMap("company", Company.class))
      .onFailure(errorHandler(routingContext, "Failed to get all companies"))
      .onSuccess(companies -> onSuccessHandler(routingContext, companies));
  }

  private void onSuccessHandler(RoutingContext routingContext, RowSet<Company> companies) {
    if(isEmpty(companies)) {
      notFoundResponse(routingContext, "No companies found...");
      return;
    }
    var response = new JsonArray();
    companies.forEach(response::add);
    LOG.info("Path {} response with {}", routingContext.normalizedPath(), response.encode());
    sendResponse(routingContext, response);
  }

  private void sendResponse(RoutingContext routingContext, JsonArray response) {
    routingContext.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer());
  }

  private boolean isEmpty(RowSet<Company> companies) {
    return !companies.iterator().hasNext();
  }
}
