package se.marcuskarlberg.vertx_lab.company;

import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class CompanyRestApi {

  public static void attach(Router router, Pool db) {
    router.get("/companies").handler(new GetCompanyHandler(db));
    router.put("/companies").handler(new PutCompanyHandler(db));
    router.delete("/companies/:company").handler(new DeleteCompanyHandler(db));
  }
}
