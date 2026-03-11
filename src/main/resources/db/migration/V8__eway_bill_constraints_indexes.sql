-- Ensure one EWay Bill per Sales Invoice
ALTER TABLE eway_bills
ADD CONSTRAINT unique_sales_invoice UNIQUE (sales_invoice_id);

--Performance indexes
CREATE INDEX idx_eway_bill_business ON eway_bills(business_id);

CREATE INDEX idx_eway_bill_invoice ON eway_bills(sales_invoice_id);

CREATE INDEX idx_eway_bill_status ON eway_bills(status);