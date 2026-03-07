-- =========================================================
-- V5__inventory_stock_sales_schema.sql
-- Adds stock system + sales module
-- =========================================================


-- =========================================================
-- 1. STOCK TABLE (CURRENT STOCK PER ITEM)
-- =========================================================

CREATE TABLE stock (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,

    quantity NUMERIC(15,3) DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_stock_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_stock_business_item
        UNIQUE (business_id, item_id)
);

CREATE INDEX idx_stock_business ON stock(business_id);
CREATE INDEX idx_stock_item ON stock(item_id);



-- =========================================================
-- 2. STOCK MOVEMENTS TABLE (STOCK LEDGER)
-- =========================================================

CREATE TABLE stock_movements (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,

    quantity NUMERIC(15,3) NOT NULL,

    movement_type VARCHAR(20) NOT NULL,
    -- PURCHASE_IN | SALE_OUT | ADJUSTMENT | OPENING

    reference_type VARCHAR(50),
    -- PURCHASE_INVOICE | SALES_INVOICE

    reference_id BIGINT,

    remarks TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_stock_mov_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_stock_mov_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_stock_mov_item ON stock_movements(item_id);
CREATE INDEX idx_stock_mov_business ON stock_movements(business_id);



-- =========================================================
-- 3. REMOVE STOCK FIELDS FROM ITEMS
-- =========================================================

ALTER TABLE items DROP COLUMN IF EXISTS stock_value;
ALTER TABLE items DROP COLUMN IF EXISTS stock;
ALTER TABLE items DROP COLUMN IF EXISTS current_stock;



-- =========================================================
-- 4. SALES INVOICES
-- =========================================================

CREATE TABLE sales_invoices (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,
    party_id BIGINT NOT NULL,

    invoice_number VARCHAR(50) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE,

    subtotal DECIMAL(15,2) DEFAULT 0,
    total_discount DECIMAL(15,2) DEFAULT 0,

    total_cgst DECIMAL(15,2) DEFAULT 0,
    total_sgst DECIMAL(15,2) DEFAULT 0,
    total_igst DECIMAL(15,2) DEFAULT 0,

    total_tax DECIMAL(15,2) DEFAULT 0,

    grand_total DECIMAL(15,2) DEFAULT 0,
    amount_paid DECIMAL(15,2) DEFAULT 0,
    balance DECIMAL(15,2) DEFAULT 0,

    status VARCHAR(20) DEFAULT 'pending',

    is_deleted BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_sales_invoice
        UNIQUE (business_id, invoice_number),

    CONSTRAINT fk_sales_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id),

    CONSTRAINT fk_sales_party
        FOREIGN KEY (party_id)
        REFERENCES party(id)
);



-- =========================================================
-- 5. SALES INVOICE ITEMS
-- =========================================================

CREATE TABLE sales_invoice_items (

    id BIGSERIAL PRIMARY KEY,

    business_id BIGINT NOT NULL,

    sales_invoice_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,

    quantity NUMERIC(15,3) NOT NULL,
    unit VARCHAR(50),

    rate NUMERIC(15,2) NOT NULL,
    discount NUMERIC(15,2) DEFAULT 0,

    gst_rate NUMERIC(5,2),

    cgst_amount NUMERIC(15,2) DEFAULT 0,
    sgst_amount NUMERIC(15,2) DEFAULT 0,
    igst_amount NUMERIC(15,2) DEFAULT 0,

    total NUMERIC(15,2) NOT NULL,

    CONSTRAINT fk_sales_invoice
        FOREIGN KEY (sales_invoice_id)
        REFERENCES sales_invoices(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sales_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
);

CREATE INDEX idx_sales_items_invoice ON sales_invoice_items(sales_invoice_id);
CREATE INDEX idx_sales_items_item ON sales_invoice_items(item_id);