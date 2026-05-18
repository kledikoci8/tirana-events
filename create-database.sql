-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS tiranaevents 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Verify the database was created
SHOW DATABASES LIKE 'tiranaevents';

-- Use the database
USE tiranaevents;

-- Show that it's empty (no tables yet - Spring Boot will create them)
SHOW TABLES;
