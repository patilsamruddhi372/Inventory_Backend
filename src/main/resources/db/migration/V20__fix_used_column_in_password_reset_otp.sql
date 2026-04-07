--set existing NULL value to false
UPDATE password_reset_otp
SET used = false
WHERE used IS NULL;

--Set default value to false
ALTER TABLE password_reset_otp
ALTER COLUMN used SET DEFAULT false;

--Make column NOT NULL
ALTER TABLE password_reset_otp
ALTER COLUMN used SET NOT NULL;