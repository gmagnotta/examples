CREATE TABLE items
(
  id integer NOT NULL,
  description character varying(255) NOT NULL,
  price numeric(19,2) NOT NULL,
  CONSTRAINT item_pkey PRIMARY KEY (id)
);


CREATE TABLE orders
(
  id integer NOT NULL,
  amount numeric(19,2) NOT NULL,
  creation_date timestamp without time zone,
  CONSTRAINT orders_pkey PRIMARY KEY (id)
);

CREATE TABLE line_items
(
  id integer NOT NULL,
  price numeric(19,2) NOT NULL,
  quantity integer NOT NULL,
  item integer NOT NULL,
  ord integer NOT NULL,
  CONSTRAINT line_item_pkey PRIMARY KEY (id),
  CONSTRAINT fkakmvwmt6j9vqycubuyo95lo07 FOREIGN KEY (item)
      REFERENCES items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkjtif4tvu8sx84idapb2hk5obv FOREIGN KEY (ord)
      REFERENCES orders (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE SEQUENCE hibernate_sequence
  START 1;
  
INSERT INTO items(id, description, price) VALUES (0, 'item0', 1.0);
INSERT INTO items(id, description, price) VALUES (1, 'item1', 2.0);
INSERT INTO items(id, description, price) VALUES (2, 'item2', 3.0);
INSERT INTO items(id, description, price) VALUES (3, 'item3', 4.0);
INSERT INTO items(id, description, price) VALUES (4, 'item4', 5.0);
INSERT INTO items(id, description, price) VALUES (5, 'item5', 6.0);