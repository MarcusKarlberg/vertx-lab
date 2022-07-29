package se.marcuskarlberg.vertx_lab.company;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static se.marcuskarlberg.vertx_lab.db.DbResponse.errorHandler;

public class DeleteCompanyHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(DeleteCompanyHandler.class);

  private Pool db;

  public DeleteCompanyHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var company = routingContext.pathParam("company");
    final HashMap<String, Object> parameters = new HashMap<>();
    parameters.put("company", company);
    LOG.debug("Trying to delete {}", company);

    SqlTemplate.forUpdate(db,
      "DELETE FROM lab.companies WHERE company=#{company}")
      .execute(parameters)
      .onFailure(errorHandler(routingContext, "Failed to delete company with name: " + company))
      .onSuccess(result -> {
        LOG.info("Successfully deleted company: {}", company);
        routingContext.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end();
      });
  }
}
