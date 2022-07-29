package se.marcuskarlberg.vertx_lab.company;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
  private String company;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}
