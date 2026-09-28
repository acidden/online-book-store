INSERT INTO orders (id, user_id, status, total, order_date, shipping_address)
VALUES (1, 2, 'PENDING', 39.98, '2026-09-28 12:00:00', '123 Test Street');

INSERT INTO order_items (id, order_id, book_id, quantity, price)
VALUES (1, 1, 1, 2, 19.99);