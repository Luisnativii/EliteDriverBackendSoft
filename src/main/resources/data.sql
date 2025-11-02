-- Crear tabla users
CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE,
    dui VARCHAR(20),
    phone_number VARCHAR(20),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50)
    );

