INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'Fantasy', 'Fantasy books', false);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted) VALUES (1, 'Title', 'Author', '987-6-54321-0', 19.99, 'Desc', 'image.jpg', false);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
