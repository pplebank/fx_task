CREATE TABLE market_price (
    id SERIAL PRIMARY KEY,
    instrument_name VARCHAR(10) NOT NULL,
    bid_price DECIMAL(10, 4) NOT NULL,
    ask_price DECIMAL(10, 4) NOT NULL,
    time TIMESTAMP NOT NULL
);

CREATE INDEX idx_record_type_time ON market_price (instrument_name, time DESC);

INSERT INTO market_price (instrument_name, bid_price, ask_price, time)
VALUES
    ('EUR/USD', 1.1000, 1.2000, '2023-06-13 10:01:00'),
    ('EUR/USD', 1.1000, 1.2000, '2023-05-13 10:01:00'),
    ('EUR/USD', 1.1000, 1.2000, '2022-06-13 10:01:00'),
    ('GBP/USD', 1.2500, 1.2560, '2023-06-13 10:02:00'),
    ('GBP/USD', 1.2500, 1.2560, '2023-06-13 09:02:00'),
    ('EUR/JPY', 119.6100, 119.9100, '2023-06-13 10:03:00');