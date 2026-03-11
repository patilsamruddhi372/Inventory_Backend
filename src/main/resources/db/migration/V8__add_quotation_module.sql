-- =========================================================
-- V8__add_quotation_module.sql
-- Adds quotation module and links with sales_invoices
-- =========================================================


-- =========================================================
-- 1. QUOTATIONS TABLE
-- =========================================================

CREATE TABLE quotations (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,

    quotation_number VARCHAR(100) NOT NULL,
    revision_number INT DEFAULT 1,

    party_id BIGINT NOT NULL,

    quotation_date DATE NOT NULL,
    valid_until DATE,

    subtotal DECIMAL(15,2) DEFAULT 0,
    tax_amount DECIMAL(15,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,

    shipping_charges DECIMAL(15,2) DEFAULT 0,
    round_off DECIMAL(10,2) DEFAULT 0,

    total_amount DECIMAL(15,2) DEFAULT 0,

    currency VARCHAR(10),

    status VARCHAR(20) DEFAULT 'pending',

    payment_terms VARCHAR(255),
    delivery_time VARCHAR(255),

    notes TEXT,
    internal_notes TEXT,
    terms_and_conditions TEXT,

    converted_to_invoice_id BIGINT,
    converted_at TIMESTAMP,

    approved_by BIGINT,
    approved_at TIMESTAMP,

    created_by BIGINT DEFAULT 1,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_quotation_number
        UNIQUE (business_id, quotation_number, revision_number),

    CONSTRAINT fk_quotation_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id),

    CONSTRAINT fk_quotation_party
        FOREIGN KEY (party_id)
        REFERENCES party(id)
);

CREATE INDEX idx_quotation_business ON quotations(business_id);
CREATE INDEX idx_quotation_party ON quotations(party_id);



-- =========================================================
-- 2. QUOTATION ITEMS
-- =========================================================

CREATE TABLE quotation_items (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,

    quotation_id BIGINT NOT NULL,
    item_id BIGINT,

    item_name VARCHAR(255),
    description TEXT,

    quantity NUMERIC(15,3) NOT NULL,
    unit VARCHAR(50),

    rate NUMERIC(15,2) NOT NULL,

    discount_percent NUMERIC(5,2),
    discount_amount NUMERIC(15,2),

    gst_rate NUMERIC(5,2),
    tax_amount NUMERIC(15,2),

    amount NUMERIC(15,2),

    hsn_code VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quotation_items_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id),

    CONSTRAINT fk_quotation_items_quotation
        FOREIGN KEY (quotation_id)
        REFERENCES quotations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_quotation_items_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
);

CREATE INDEX idx_quotation_items_quotation ON quotation_items(quotation_id);
CREATE INDEX idx_quotation_items_item ON quotation_items(item_id);



-- =========================================================
-- 3. ALTER SALES_INVOICES TABLE
-- =========================================================

ALTER TABLE sales_invoices
ADD COLUMN quotation_id BIGINT DEFAULT NULL;

ALTER TABLE sales_invoices
ADD CONSTRAINT fk_sales_invoice_quotation
FOREIGN KEY (quotation_id)
REFERENCES quotations(id)
ON DELETE SET NULL;