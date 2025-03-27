-- Insert brands
INSERT INTO brand (name) VALUES 
    ('A'), ('B'), ('C'), ('D'), ('E'), ('F'), ('G'), ('H'), ('I');

-- Insert sample products for each brand and category
INSERT INTO product (brand_id, category, price) VALUES
    -- Brand A products
    (1, 'TOP', 11200),
    (1, 'PANTS', 5500),
    (1, 'OUTER', 4200),
    (1, 'SNEAKERS', 9000),
    (1, 'BAG', 2000),
    (1, 'HAT', 1700),
    (1, 'SOCKS', 1800),
    (1, 'ACCESSORY', 2300),

    -- Brand B products
    (2, 'TOP', 10500),
    (2, 'PANTS', 5900),
    (2, 'OUTER', 3800),
    (2, 'SNEAKERS', 9100),
    (2, 'BAG', 2100),
    (2, 'HAT', 2000),
    (2, 'SOCKS', 2000),
    (2, 'ACCESSORY', 2200),

    -- Brand C products
    (3, 'TOP', 10000),
    (3, 'PANTS', 6200),
    (3, 'OUTER', 3300),
    (3, 'SNEAKERS', 9200),
    (3, 'BAG', 2200),
    (3, 'HAT', 1900),
    (3, 'SOCKS', 2200),
    (3, 'ACCESSORY', 2100),

    -- Brand D products
    (4, 'TOP', 10100),
    (4, 'PANTS', 5100),
    (4, 'OUTER', 3000),
    (4, 'SNEAKERS', 9500),
    (4, 'BAG', 2500),
    (4, 'HAT', 1500),
    (4, 'SOCKS', 2400),
    (4, 'ACCESSORY', 2000),

    -- Brand E products
    (5, 'TOP', 10700),
    (5, 'PANTS', 5000),
    (5, 'OUTER', 3800),
    (5, 'SNEAKERS', 9900),
    (5, 'BAG', 2300),
    (5, 'HAT', 1800),
    (5, 'SOCKS', 2100),
    (5, 'ACCESSORY', 2100),

    -- Brand F products
    (6, 'TOP', 11200),
    (6, 'PANTS', 7200),
    (6, 'OUTER', 4000),
    (6, 'SNEAKERS', 9300),
    (6, 'BAG', 2100),
    (6, 'HAT', 1600),
    (6, 'SOCKS', 2300),
    (6, 'ACCESSORY', 1900),

    -- Brand G products
    (7, 'TOP', 10500),
    (7, 'PANTS', 5800),
    (7, 'OUTER', 3900),
    (7, 'SNEAKERS', 9000),
    (7, 'BAG', 2200),
    (7, 'HAT', 1700),
    (7, 'SOCKS', 2100),
    (7, 'ACCESSORY', 2000),

    -- Brand H products
    (8, 'TOP', 10800),
    (8, 'PANTS', 6300),
    (8, 'OUTER', 3100),
    (8, 'SNEAKERS', 9700),
    (8, 'BAG', 2100),
    (8, 'HAT', 1600),
    (8, 'SOCKS', 2000),
    (8, 'ACCESSORY', 2000),

    -- Brand I products
    (9, 'TOP', 11400),
    (9, 'PANTS', 6700),
    (9, 'OUTER', 3200),
    (9, 'SNEAKERS', 9500),
    (9, 'BAG', 2400),
    (9, 'HAT', 1700),
    (9, 'SOCKS', 1700),
    (9, 'ACCESSORY', 2400);