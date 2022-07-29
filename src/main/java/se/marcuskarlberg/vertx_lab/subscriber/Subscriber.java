package se.marcuskarlberg.vertx_lab.subscriber;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {
  private UUID id;
  private String username;
}
