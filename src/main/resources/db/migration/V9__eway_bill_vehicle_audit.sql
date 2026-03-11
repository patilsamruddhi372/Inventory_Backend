CREATE TABLE eway_bill_vehicle_audit(
    id BIGSERIAL PRIMARY KEY,
    eway_bill_id BIGINT,
    old_vehicle_number VARCHAR(50),
    new_vehicle_number VARCHAR(50),
    updated_at TIMESTAMP
);