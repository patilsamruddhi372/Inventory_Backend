ALTER TABLE purchase_invoices
ADD COLUMN IF NOT EXISTS payment_type VARCHAR(20);

ALTER TABLE sales_invoices
ADD COLUMN IF NOT EXISTS payment_type VARCHAR(20);