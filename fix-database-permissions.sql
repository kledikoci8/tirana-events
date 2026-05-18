-- ========================================
-- FIX DATABASE PERMISSIONS
-- ========================================
-- Run this in phpMyAdmin SQL tab to grant permissions to root user

-- Grant all privileges on tiranaevents database to root user
GRANT ALL PRIVILEGES ON tiranaevents.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON tiranaevents.* TO 'root'@'127.0.0.1';

-- Flush privileges to apply changes immediately
FLUSH PRIVILEGES;

-- Verify the database exists
SHOW DATABASES LIKE 'tiranaevents';

-- Show current user privileges (optional - for verification)
SHOW GRANTS FOR 'root'@'localhost';
