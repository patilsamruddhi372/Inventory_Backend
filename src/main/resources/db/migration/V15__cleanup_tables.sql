-- Step 1: Drop deepest child first
DROP TABLE IF EXISTS billing_details;

-- Step 2: Then middle table
DROP TABLE IF EXISTS subscription;

-- Step 3: Then parent table
DROP TABLE IF EXISTS business;