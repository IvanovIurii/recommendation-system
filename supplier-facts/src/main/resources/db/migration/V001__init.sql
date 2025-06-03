CREATE TABLE IF NOT EXISTS supplier
(
    supplier_id   uuid      NOT NULL PRIMARY KEY,
    supplier_name TEXT      NOT NULL,
    delivery_area TEXT[]    NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS supplier_product
(
    product_id          uuid      NOT NULL PRIMARY KEY,
    product_name        TEXT      NOT NULL,
    product_description TEXT      NOT NULL,
    product_type        TEXT      NOT NULL,
    supplier_id         uuid      NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP
);

CREATE INDEX supplier_id_idx ON supplier (supplier_id);
