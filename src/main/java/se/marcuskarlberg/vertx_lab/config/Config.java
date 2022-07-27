package se.marcuskarlberg.vertx_lab.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class Config {
  int serverPort;

  public static Config get(final JsonObject config) {
    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT);

    if (Objects.isNull(serverPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
    }

    return Config.builder()
      .serverPort(serverPort)
      .build();
  }
}
