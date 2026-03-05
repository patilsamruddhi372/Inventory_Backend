-- src/main/resources/db/migration/V3__create_purchase_module_tables.sql

-- ============================================
-- Purchase Module
-- ============================================

CREATE TABLE IF NOT EXISTS purchase_invoices (
    id              BIGSERIAL       PRIMARY KEY,
    business_id     BIGINT          NOT NULL,
    party_id        BIGINT          NOT NULL,

    bill_number     VARCHAR(50)     NOT NULL,
    bill_date       DATE            NOT NULL,
    due_date        DATE,

    subtotal        DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    total_tax       DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    grand_total     DECIMAL(15,2)   NOT NULL DEFAULT 0.00,

    amount_paid     DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    balance         DECIMAL(15,2)   NOT NULL DEFAULT 0.00,

    status          VARCHAR(20)     NOT NULL DEFAULT 'pending'
                    CHECK (status IN ('pending','partial','paid')),

    notes           TEXT,

    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    -- ============================================
    -- Foreign Keys
    -- ============================================

    CONSTRAINT fk_pi_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_pi_party
        FOREIGN KEY (party_id)
        REFERENCES party(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- ============================================
    -- Constraints
    -- ============================================

    CONSTRAINT uq_business_bill_number
        UNIQUE (business_id, bill_number),

    CONSTRAINT chk_pi_amounts
        CHECK (
            subtotal >= 0 AND
            total_tax >= 0 AND
            grand_total >= 0 AND
            amount_paid >= 0 AND
            balance >= 0
        ),

    CONSTRAINT chk_pi_due_date
        CHECK (due_date IS NULL OR due_date >= bill_date)
);


-- ============================================
-- Purchase Invoice Items
-- ============================================

CREATE TABLE IF NOT EXISTS purchase_invoice_items (
    id                      BIGSERIAL       PRIMARY KEY,
    business_id             BIGINT          NOT NULL,
    purchase_invoice_id     BIGINT          NOT NULL,
    item_id                 BIGINT          NOT NULL,

    quantity                DECIMAL(15,3)   NOT NULL,
    unit                    VARCHAR(50),

    rate                    DECIMAL(15,2)   NOT NULL,
    gst_rate                DECIMAL(5,2)    NOT NULL DEFAULT 0.00,

    total                   DECIMAL(15,2)   NOT NULL DEFAULT 0.00,

    -- ============================================
    -- Foreign Keys
    -- ============================================

    CONSTRAINT fk_pii_business
        FOREIGN KEY (business_id)
        REFERENCES businesses(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_pii_invoice
        FOREIGN KEY (purchase_invoice_id)
        REFERENCES purchase_invoices(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_pii_item
        FOREIGN KEY (item_id)
        REFERENCES items(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- ============================================
    -- Constraints
    -- ============================================

    CONSTRAINT uq_invoice_item
        UNIQUE (purchase_invoice_id, item_id),

    CONSTRAINT chk_pii_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_pii_rate
        CHECK (rate >= 0),

    CONSTRAINT chk_pii_gst_rate
        CHECK (gst_rate >= 0 AND gst_rate <= 100)
);


-- ============================================
-- Indexes
-- ============================================

CREATE INDEX IF NOT EXISTS idx_pi_business_id
ON purchase_invoices(business_id);

CREATE INDEX IF NOT EXISTS idx_pi_party_id
    ON purchase_invoices(party_id);

CREATE INDEX IF NOT EXISTS idx_pi_business_status
    ON purchase_invoices(business_id, status);

CREATE INDEX IF NOT EXISTS idx_pi_business_billdate
    ON purchase_invoices(business_id, bill_date);

CREATE INDEX IF NOT EXISTS idx_pi_business_created
    ON purchase_invoices(business_id, created_at DESC);


CREATE INDEX IF NOT EXISTS idx_pii_invoice_id
    ON purchase_invoice_items(purchase_invoice_id);

CREATE INDEX IF NOT EXISTS idx_pii_item_id
    ON purchase_invoice_items(item_id);

CREATE INDEX IF NOT EXISTS idx_pii_business_id
    ON purchase_invoice_items(business_id);


-- ============================================
-- Trigger for updated_at
-- ============================================

CREATE OR REPLACE FUNCTION update_pi_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_pi_updated_at
BEFORE UPDATE ON purchase_invoices
FOR EACH ROW
EXECUTE FUNCTION update_pi_updated_at();