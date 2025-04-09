-- Create restaurants table
CREATE TABLE IF NOT EXISTS restaurants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Create meals table
CREATE TABLE IF NOT EXISTS meals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    restaurant_id BIGINT NOT NULL,
    available_date DATE NOT NULL,
    CONSTRAINT fk_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    token VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    meal_id BIGINT NOT NULL,
    CONSTRAINT fk_meal FOREIGN KEY (meal_id) REFERENCES meals (id)
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_reservation_token ON reservations (token);
CREATE INDEX IF NOT EXISTS idx_meal_date ON meals (available_date);
CREATE INDEX IF NOT EXISTS idx_meal_restaurant ON meals (restaurant_id);

-- Insert sample restaurants
INSERT INTO restaurants (name, description) VALUES 
('Tasca do Manel', 'Traditional Portuguese cuisine'),
('Marisqueira Atlântico', 'Fresh seafood restaurant'),
('Pizzaria Bella Italia', 'Authentic Italian pizza');

-- Insert sample meals for today and next days
INSERT INTO meals (name, description, restaurant_id, available_date) VALUES 
('Francesinha (Today)', 'Traditional Portuguese sandwich', 1, CURRENT_DATE),
('Bacalhau à Brás (Today)', 'Codfish with potatoes and eggs', 1, CURRENT_DATE),
('Feijoada (Today)', 'Bean stew with meat', 1, CURRENT_DATE),
('Arroz de Marisco (Today)', 'Seafood rice', 2, CURRENT_DATE),
('Cataplana de Peixe (Today)', 'Fish stew', 2, CURRENT_DATE),
('Amêijoas à Bulhão Pato (Today)', 'Clams with garlic and coriander', 2, CURRENT_DATE),
('Margarita Pizza (Today)', 'Classic pizza with tomato and mozzarella', 3, CURRENT_DATE),
('Quattro Stagioni (Today)', 'Four sections pizza', 3, CURRENT_DATE),
('Carbonara Pizza (Today)', 'Cream and bacon pizza', 3, CURRENT_DATE),

('Francesinha (Tomorrow)', 'Traditional Portuguese sandwich', 1, CURRENT_DATE + 1),
('Bacalhau à Brás (Tomorrow)', 'Codfish with potatoes and eggs', 1, CURRENT_DATE + 1),
('Arroz de Marisco (Tomorrow)', 'Seafood rice', 2, CURRENT_DATE + 1),
('Margarita Pizza (Tomorrow)', 'Classic pizza with tomato and mozzarella', 3, CURRENT_DATE + 1); 