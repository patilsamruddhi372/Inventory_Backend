-- Soft delete for purchase invoices
ALTER TABLE purchase_invoices
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Soft delete for purchase invoice items
ALTER TABLE purchase_invoice_items
ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;