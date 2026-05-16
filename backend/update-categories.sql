-- Update category icons from emojis to Ionicon names
-- Run this in phpMyAdmin to update existing data

UPDATE categories SET icon = 'musical-notes' WHERE name = 'Music';
UPDATE categories SET icon = 'school' WHERE name = 'University';
UPDATE categories SET icon = 'color-palette' WHERE name = 'Culture';
UPDATE categories SET icon = 'people' WHERE name = 'Volunteering';
UPDATE categories SET icon = 'ellipsis-horizontal' WHERE name = 'More';
