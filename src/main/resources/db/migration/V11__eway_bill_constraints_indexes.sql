-- Ensure one EWay Bill per Sales Invoice
ALTER TABLE eway_bills
DROP CONSTRAINT IF EXISTS unique_sales_invoice;

ALTER TABLE eway_bills
ADD CONSTRAINT unique_sales_invoice UNIQUE (sales_invoice_id);

--Performance indexes
DROP INDEX IF EXISTS idx_eway_bill_business;
CREATE INDEX idx_eway_bill_business ON eway_bills(business_id);

DROP INDEX IF EXISTS idx_eway_bill_invoice;
CREATE INDEX idx_eway_bill_invoice ON eway_bills(sales_invoice_id);

DROP INDEX IF EXISTS idx_eway_bill_invoice;
CREATE INDEX idx_eway_bill_status ON eway_bills(status);