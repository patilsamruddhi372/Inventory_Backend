CREATE TABLE eway_bills (
    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,
    sales_invoice_id BIGINT NOT NULL,

    eway_bill_number VARCHAR(255),

    invoice_number VARCHAR(255),
    invoice_date DATE,
    customer_name VARCHAR(255),
    total_invoice_value NUMERIC(12, 2),

    seller_gstin VARCHAR(15),
    seller_business_name VARCHAR(255),
    seller_state VARCHAR(100),

    buyer_gstin VARCHAR(15),
    buyer_business_name VARCHAR(255),
    buyer_state VARCHAR(100),

    transporter_id VARCHAR(255),
    transporter_name VARCHAR(255),
    transporter_document_no VARCHAR(255),
    transporter_document_date DATE,

    transport_mode VARCHAR(50),
    vehicle_number VARCHAR(50),

    distance_km INTEGER,

    valid_from TIMESTAMP,
    valid_until TIMESTAMP,

    status VARCHAR(50) DEFAULT 'ACTIVE',

    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);