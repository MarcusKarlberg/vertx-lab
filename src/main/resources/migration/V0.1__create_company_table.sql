CREATE TABLE companies
(
  id         SERIAL PRIMARY KEY,
  company        VARCHAR UNIQUE
);

CREATE TABLE subscribers
(
  id         SERIAL PRIMARY KEY,
  username        VARCHAR
);

CREATE TABLE subscriptions
(
  subscriber_id        int NOT NULL,
  company_id           int NOT NULL,
  PRIMARY KEY (subscriber_id, company_id),
  FOREIGN KEY (subscriber_id) REFERENCES subscribers(id) ON UPDATE CASCADE,
  FOREIGN KEY (company_id) REFERENCES companies(id) ON UPDATE CASCADE
);
