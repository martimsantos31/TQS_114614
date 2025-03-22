CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20)
    );


INSERT INTO customers (name, email, address, phone) VALUES
                                                        ('Alice Johnson', 'alice@example.com', '123 Street, City', '123456789'),
                                                        ('Bob Smith', 'bob@example.com', '456 Avenue, Town', '987654321');