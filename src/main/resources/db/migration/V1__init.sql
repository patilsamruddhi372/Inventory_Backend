-- ===============================
-- BUSINESSES TABLE
-- ===============================
CREATE TABLE businesses (
    id BIGSERIAL PRIMARY KEY,

    -- Basic Info
    name VARCHAR(255) NOT NULL DEFAULT 'Default Business',
    business_type VARCHAR(100) NOT NULL DEFAULT 'RETAIL',
    industry VARCHAR(100),

    -- GST Info
    gst_registered BOOLEAN NOT NULL DEFAULT FALSE,
    gst_number VARCHAR(20),

    -- Address
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(6),
    country VARCHAR(100) DEFAULT 'India',

    -- Financial
    financial_year VARCHAR(20),
    currency VARCHAR(10) DEFAULT 'INR',
    enable_stock_management BOOLEAN DEFAULT FALSE,

    -- Media
    logo_url VARCHAR(255),

    -- Subscription
    subscription_plan VARCHAR(50),
    subscription_start DATE,
    subscription_end DATE,

    -- Flags
    is_active BOOLEAN DEFAULT TRUE,

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT gst_number_length_check
        CHECK (gst_number IS NULL OR LENGTH(gst_number) = 15),
    CONSTRAINT pincode_length_check
        CHECK (pincode IS NULL OR LENGTH(pincode) = 6)
);

-- Default Business
INSERT INTO businesses (id, name, business_type, gst_registered)
VALUES (1, 'Default Business', 'RETAIL', FALSE)
ON CONFLICT (id) DO NOTHING;

-- ===============================
-- ITEMS TABLE (GOODS + SERVICES)
-- ===============================
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    business_id BIGINT REFERENCES businesses(id) DEFAULT 1,

    -- Type: 'GOODS' or 'SERVICE'
    type VARCHAR(20) NOT NULL DEFAULT 'GOODS',

    -- ========== COMMON FIELDS ==========
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    sku VARCHAR(100),
    description TEXT,

    -- Pricing
    sale_price NUMERIC(12,2),
    purchase_price NUMERIC(12,2),
    gst_rate NUMERIC(5,2) DEFAULT 0,
    tax_inclusive BOOLEAN DEFAULT FALSE,

    -- Discounts
    sale_discount_percent NUMERIC(5,2) DEFAULT 0,
    sale_discount_amount NUMERIC(12,2) DEFAULT 0,
    purchase_discount_percent NUMERIC(5,2) DEFAULT 0,
    purchase_discount_amount NUMERIC(12,2) DEFAULT 0,

    -- Tags
    tags TEXT[],

    -- ========== GOODS ONLY ==========
    barcode VARCHAR(255),
    brand VARCHAR(100),
    mrp_price NUMERIC(12,2),
    hsn_code VARCHAR(20),

    -- Stock
    stock INTEGER DEFAULT 0,
    opening_stock NUMERIC(12,3) DEFAULT 0,
    current_stock NUMERIC(12,3) DEFAULT 0,
    low_stock_alert INTEGER,
    unit VARCHAR(50),
    weight NUMERIC(10,3),
    weight_unit VARCHAR(20),

    -- Media
    image_url TEXT,
    gallery_urls TEXT[],

    -- Computed
    stock_value NUMERIC(12,2) GENERATED ALWAYS AS (
        CASE
            WHEN type = 'GOODS' THEN current_stock * COALESCE(purchase_price, 0)
            ELSE 0
        END
    ) STORED,

    profit_margin NUMERIC(5,2) GENERATED ALWAYS AS (
        CASE
            WHEN COALESCE(purchase_price, 0) > 0
            THEN ((sale_price - purchase_price) / purchase_price * 100)
            ELSE 0
        END
    ) STORED,

    -- ========== SERVICE ONLY ==========
    sac_code VARCHAR(20),
    service_duration VARCHAR(50),

    -- ========== FLAGS ==========
    is_favorite BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_items_name ON items(name);
CREATE INDEX idx_items_sku ON items(sku);
CREATE INDEX idx_items_barcode ON items(barcode);
CREATE INDEX idx_items_business ON items(business_id);
CREATE INDEX idx_items_type ON items(type);