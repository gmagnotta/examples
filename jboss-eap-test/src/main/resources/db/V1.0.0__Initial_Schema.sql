CREATE TABLE item
(
  id integer NOT NULL,
  description character varying(255),
  price numeric(19,2),
  CONSTRAINT item_pkey PRIMARY KEY (id)
);


CREATE TABLE orders
(
  id integer NOT NULL,
  amount numeric(19,2),
  creation_date timestamp without time zone,
  externalorderid character varying(255),
  order_withdrawal timestamp without time zone,
  CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE line_item
(
  id integer NOT NULL,
  price numeric(19,2),
  quantity integer NOT NULL,
  item integer,
  orders integer,
  CONSTRAINT line_item_pkey PRIMARY KEY (id),
  CONSTRAINT fkakmvwmt6j9vqycubuyo95lo07 FOREIGN KEY (item)
      REFERENCES item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkjtif4tvu8sx84idapb2hk5obv FOREIGN KEY (orders)
      REFERENCES orders (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE SEQUENCE hibernate_sequence
  START 1;
  
INSERT INTO item(id, description, price) VALUES (0, 'item0', 0.5);
INSERT INTO item(id, description, price) VALUES (1, 'item1', 1.0);
INSERT INTO item(id, description, price) VALUES (2, 'item2', 2.0);
INSERT INTO item(id, description, price) VALUES (3, 'item3', 3.0);
INSERT INTO item(id, description, price) VALUES (4, 'item4', 4.0);
INSERT INTO item(id, description, price) VALUES (5, 'item5', 5.0);