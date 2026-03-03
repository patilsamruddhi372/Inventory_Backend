-- V2__create_party_table.sql
CREATE TABLE party (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    gstin VARCHAR(20),
    since_date DATE,
    credit_limit DECIMAL(15,2),
    phone VARCHAR(20),
    email VARCHAR(255),
    address_line1 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_party_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
);