CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(200),
    last_name VARCHAR(200),
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(10),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users(first_name, last_name, email, phone, password, role)
VALUES ('John', 'Doe', 'john.d@ex.com', '9345872431', '123456', 'Owner');