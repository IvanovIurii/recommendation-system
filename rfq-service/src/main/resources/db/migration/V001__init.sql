CREATE TABLE IF NOT EXISTS rfq_core
(
    rfq_id            uuid      NOT NULL PRIMARY KEY, -- generated
    title             TEXT      NOT NULL,
    description       TEXT      NOT NULL,
    delivery_location TEXT      NOT NULL,
    product_type      TEXT      NOT NULL,
    status            TEXT      NOT NULL,             -- NEW, PROCESSED
    created           TIMESTAMP NOT NULL,
    updated           TIMESTAMP
);

CREATE TABLE IF NOT EXISTS matching_core
(
    matching_id uuid      NOT NULL PRIMARY KEY, -- generated
    rfq_id      uuid      NOT NULL,
    supplier_id uuid      NOT NULL,
    status      TEXT      NOT NULL,             -- not used for now: CREATED only, PUBLISHED
    created     TIMESTAMP NOT NULL,
    updated     TIMESTAMP
);

ALTER TABLE rfq_core
    ADD CONSTRAINT rfq_core_unique
        UNIQUE (
                title,
                description,
                delivery_location,
                product_type
            );

ALTER TABLE matching_core
    ADD CONSTRAINT matching_core_unique
        UNIQUE (
                rfq_id,
                supplier_id
            );
