ALTER TABLE businesses
ADD COLUMN user_id BIGINT;

ALTER TABLE businesses
ADD CONSTRAINT fk_business_user
FOREIGN KEY (user_id) REFERENCES users(id);