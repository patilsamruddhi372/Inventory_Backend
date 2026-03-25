CREATE TABLE business (
    business_id BIGSERIAL PRIMARY KEY,

    business_name VARCHAR(255),
    gstin VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),

    gst_enabled BOOLEAN DEFAULT TRUE,
    stock_enabled BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscription (
    subscription_id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,
    plan VARCHAR(50) NOT NULL,
    billing_cycle VARCHAR(20), -- MONTHLY / YEARLY

    start_date DATE,
    end_date DATE,

    status VARCHAR(20), -- ACTIVE / EXPIRED

    CONSTRAINT fk_subscription_business
        FOREIGN KEY (business_id)
        REFERENCES business(business_id)
        ON DELETE CASCADE
);

CREATE TABLE billing_details (
    billing_id BIGSERIAL PRIMARY KEY,

    subscription_id BIGINT NOT NULL,

    billing_name VARCHAR(255),
    phone VARCHAR(20),
    country VARCHAR(100),
    street_address VARCHAR(255),
    state VARCHAR(100),
    city VARCHAR(100),
    zip VARCHAR(20),

    gst_registered BOOLEAN,
    gstin VARCHAR(20),

    payment_type VARCHAR(50),

    CONSTRAINT fk_billing_subscription
        FOREIGN KEY (subscription_id)
        REFERENCES subscription(subscription_id)
        ON DELETE CASCADE
);