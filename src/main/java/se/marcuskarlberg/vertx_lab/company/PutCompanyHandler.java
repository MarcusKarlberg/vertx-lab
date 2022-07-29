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

public class PutCompanyHandler implements Handler<RoutingContext>  {
  private static final Logger LOG = LoggerFactory.getLogger(PutCompanyHandler.class);

  private final Pool db;

  public PutCompanyHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    var json = routingContext.getBodyAsJson();
    var company = json.mapTo(Company.class);
    final HashMap<String, Object> parameters = new HashMap<>();
    parameters.put("company", company.getCompany());

    SqlTemplate.forUpdate(db,
      "INSERT INTO lab.companies (id, company) VALUES (DEFAULT, #{company})")
      .execute(parameters)
      .onFailure(errorHandler(routingContext, "Failed to insert company named: " + company.getCompany()))
      .onSuccess(result -> {
        routingContext.response()
          .setStatusCode(HttpResponseStatus.OK.code())
          .end();
      });
  }
}
