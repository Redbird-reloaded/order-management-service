CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id VARCHAR(100) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price NUMERIC(19, 2) NOT NULL CHECK (price > 0),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE CASCADE
);

INSERT INTO order_items (
    id,
    order_id,
    product_id,
    product_name,
    quantity,
    price
)
SELECT
    gen_random_uuid(),
    id,
    product_code,
    product_code,
    quantity,
    unit_price
FROM orders;

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

ALTER TABLE orders
    DROP COLUMN product_code,
    DROP COLUMN quantity,
    DROP COLUMN unit_price,
    DROP COLUMN total_amount;
