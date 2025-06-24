-- Drop the orders table if it exists
DROP TABLE IF EXISTS orders CASCADE;

-- Create the orders table with auto-generated order_id
CREATE TABLE orders (
    order_id BIGSERIAL PRIMARY KEY,
    order_status VARCHAR(255),
    total_value DOUBLE PRECISION,
    version BIGINT
);

-- Create the join table for order_products
CREATE TABLE order_products (
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);
