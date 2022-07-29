package se.marcuskarlberg.vertx_lab.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.marcuskarlberg.vertx_lab.company.Company;
import se.marcuskarlberg.vertx_lab.subscriber.Subscriber;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
  private UUID id;
  private Subscriber subscriber;
  private Company company;
}
