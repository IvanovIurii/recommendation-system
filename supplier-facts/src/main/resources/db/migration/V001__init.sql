CREATE TABLE IF NOT EXISTS supplier_product
(
    id                  uuid   NOT NULL PRIMARY KEY, -- generated
    product_name        TEXT   NOT NULL,
    product_description TEXT   NOT NULL,
    product_type        TEXT   NOT NULL,
    supplier_name       TEXT   NOT NULL,
    delivery_area       TEXT[] NOT NULL
);
