DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS sellers;

CREATE TABLE sellers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    region VARCHAR(50)
);

CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL REFERENCES sellers(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    returned BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_sales_seller_id ON sales(seller_id);
CREATE INDEX idx_sales_date ON sales(date);

INSERT INTO sellers (name, region) VALUES
    ('TechZone', 'North America'),
    ('GadgetHub', 'Europe'),
    ('EcoMart', 'Asia');

INSERT INTO sales (seller_id, date, quantity, price, returned)
SELECT
    s.id,
    CURRENT_DATE - (random() * 30)::int AS date,           -- Random date within last 30 days
    (random() * 5 + 1)::int AS quantity,                   -- Quantity between 1-5
    (random() * 100 + 20)::numeric(10,2) AS price,         -- Price between $20-$120
    (random() < 0.1)::boolean AS returned                  -- Approximately 10% return rate
FROM sellers s
CROSS JOIN generate_series(1, 100) g;

SELECT 
    s.name AS merchant_name,
    COUNT(sa.id) AS total_sales,
    SUM(sa.quantity) AS total_units,
    ROUND(AVG(sa.price), 2) AS avg_price,
    COUNT(CASE WHEN sa.returned THEN 1 END) AS returns
FROM sellers s
LEFT JOIN sales sa ON s.id = sa.seller_id
GROUP BY s.id, s.name
ORDER BY s.id;
