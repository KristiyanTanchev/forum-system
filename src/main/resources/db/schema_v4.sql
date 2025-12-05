-- Schema V4: Add Role enum to replace is_admin boolean

-- Add new role column with default value
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Migrate existing data
UPDATE users SET role = 'ADMIN' WHERE is_admin = 1;
UPDATE users SET role = 'USER' WHERE is_admin = 0;

-- Drop old is_admin column
ALTER TABLE users DROP COLUMN is_admin;

-- Add index for performance on role queries
CREATE INDEX idx_users_role ON users(role);
