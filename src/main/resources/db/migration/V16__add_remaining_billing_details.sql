ALTER TABLE businesses
ADD COLUMN billing_address TEXT,
ADD COLUMN billing_city VARCHAR(100),
ADD COLUMN billing_state VARCHAR(100),
ADD COLUMN billing_zipcode VARCHAR(6),
ADD COLUMN billing_country VARCHAR(100);