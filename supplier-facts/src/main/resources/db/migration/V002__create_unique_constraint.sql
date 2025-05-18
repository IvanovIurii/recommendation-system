ALTER TABLE supplier_product
    ADD CONSTRAINT supplier_product_all_cols_unique
        UNIQUE (
                product_name,
                product_description,
                product_type,
                supplier_name,
                delivery_area
            );
