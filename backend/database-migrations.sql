-- ============================================
-- TIRANA EVENTS - DATABASE MIGRATIONS
-- Priority 1 Features Implementation
-- ============================================

-- [1] PERSONALISED FEED ALGORITHM
-- ============================================

-- User Interactions Table
CREATE TABLE IF NOT EXISTS user_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    weight INT,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_event (event_id),
    INDEX idx_type (type)
);

-- User Preferences Table
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    notify_event_reminder BOOLEAN DEFAULT TRUE,
    notify_price_drop BOOLEAN DEFAULT TRUE,
    notify_friend_activity BOOLEAN DEFAULT TRUE,
    notify_nearby_events BOOLEAN DEFAULT TRUE,
    quiet_hours_start INT DEFAULT 23,
    quiet_hours_end INT DEFAULT 8,
    home_latitude DOUBLE,
    home_longitude DOUBLE,
    home_address VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- User Preference Categories (Many-to-Many)
CREATE TABLE IF NOT EXISTS user_preference_categories (
    preference_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (preference_id, category_id),
    FOREIGN KEY (preference_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- [2] FRIEND ACTIVITY FEED & SOCIAL LAYER
-- ============================================

-- Friend Activities Table
CREATE TABLE IF NOT EXISTS friend_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_user_timestamp (user_id, timestamp),
    INDEX idx_event (event_id),
    INDEX idx_activity_type (activity_type)
);

-- Event Chats Table
CREATE TABLE IF NOT EXISTS event_chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    message VARCHAR(1000) NOT NULL,
    timestamp DATETIME NOT NULL,
    reply_to_id BIGINT,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_id) REFERENCES event_chats(id) ON DELETE SET NULL,
    INDEX idx_event_timestamp (event_id, timestamp)
);

-- Event Invites Table
CREATE TABLE IF NOT EXISTS event_invites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    inviter_id BIGINT NOT NULL,
    invitee_id BIGINT,
    invitee_email VARCHAR(255),
    invitee_phone VARCHAR(50),
    invite_token VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    accepted_at DATETIME,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (invitee_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_token (invite_token),
    INDEX idx_status (status)
);

-- [3] ADVANCED FILTER SYSTEM
-- ============================================

-- Add new columns to events table
ALTER TABLE events 
ADD COLUMN IF NOT EXISTS price DOUBLE,
ADD COLUMN IF NOT EXISTS is_free BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS tickets_available INT,
ADD COLUMN IF NOT EXISTS is_outdoor BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS wheelchair_accessible BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS hearing_loop_available BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS seated_venue BOOLEAN DEFAULT FALSE;

-- Filter Presets Table
CREATE TABLE IF NOT EXISTS filter_presets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    min_price DOUBLE,
    max_price DOUBLE,
    include_free BOOLEAN,
    max_distance DOUBLE,
    start_hour INT,
    end_hour INT,
    date_range_type VARCHAR(50),
    custom_start_date DATETIME,
    custom_end_date DATETIME,
    require_wheelchair_access BOOLEAN,
    require_hearing_loop BOOLEAN,
    require_seated_venue BOOLEAN,
    indoor_only BOOLEAN,
    outdoor_only BOOLEAN,
    category_ids VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id)
);

-- [4] PUSH NOTIFICATION SYSTEM
-- ============================================

-- Device Tokens Table
CREATE TABLE IF NOT EXISTS device_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) UNIQUE NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    last_used_at DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_token (token),
    INDEX idx_active (is_active)
);

-- Push Notifications Table
CREATE TABLE IF NOT EXISTS push_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    body VARCHAR(500) NOT NULL,
    deep_link VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    sent_at DATETIME,
    scheduled_for DATETIME,
    error_message VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_scheduled (scheduled_for),
    INDEX idx_type (type)
);

-- [5] OFFLINE TICKET ACCESS & WALLET INTEGRATION
-- ============================================

-- Add new columns to tickets table
ALTER TABLE tickets
ADD COLUMN IF NOT EXISTS wallet_pass_url VARCHAR(500),
ADD COLUMN IF NOT EXISTS nfc_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS nfc_data VARCHAR(500),
ADD COLUMN IF NOT EXISTS downloaded_at DATETIME,
ADD COLUMN IF NOT EXISTS is_downloaded BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS transferred_to_id BIGINT,
ADD COLUMN IF NOT EXISTS transferred_at DATETIME,
ADD COLUMN IF NOT EXISTS checked_in_at DATETIME,
ADD COLUMN IF NOT EXISTS checked_in_by VARCHAR(255);

-- Add foreign key for transferred_to
ALTER TABLE tickets
ADD CONSTRAINT fk_tickets_transferred_to 
FOREIGN KEY (transferred_to_id) REFERENCES users(id) ON DELETE SET NULL;

-- [6] REAL-TIME ORGANISER ANALYTICS DASHBOARD
-- ============================================

-- Event Analytics Table
CREATE TABLE IF NOT EXISTS event_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL UNIQUE,
    total_views BIGINT DEFAULT 0,
    unique_views BIGINT DEFAULT 0,
    total_saves BIGINT DEFAULT 0,
    ticket_page_views BIGINT DEFAULT 0,
    purchase_attempts BIGINT DEFAULT 0,
    completed_purchases BIGINT DEFAULT 0,
    view_to_save_rate DOUBLE DEFAULT 0.0,
    save_to_ticket_rate DOUBLE DEFAULT 0.0,
    ticket_to_purchase_rate DOUBLE DEFAULT 0.0,
    overall_conversion_rate DOUBLE DEFAULT 0.0,
    from_search BIGINT DEFAULT 0,
    from_home_feed BIGINT DEFAULT 0,
    from_map BIGINT DEFAULT 0,
    from_friend_share BIGINT DEFAULT 0,
    from_direct_link BIGINT DEFAULT 0,
    total_revenue DOUBLE DEFAULT 0.0,
    daily_revenue DOUBLE DEFAULT 0.0,
    weekly_revenue DOUBLE DEFAULT 0.0,
    age_group_distribution TEXT,
    neighborhood_distribution TEXT,
    category_interests TEXT,
    last_updated DATETIME NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_event (event_id)
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================

-- Events table indexes
CREATE INDEX IF NOT EXISTS idx_events_start_date ON events(start_date);
CREATE INDEX IF NOT EXISTS idx_events_category ON events(category_id);
CREATE INDEX IF NOT EXISTS idx_events_organizer ON events(organizer_id);
CREATE INDEX IF NOT EXISTS idx_events_price ON events(price);
CREATE INDEX IF NOT EXISTS idx_events_location ON events(latitude, longitude);

-- Tickets table indexes
CREATE INDEX IF NOT EXISTS idx_tickets_user ON tickets(user_id);
CREATE INDEX IF NOT EXISTS idx_tickets_event ON tickets(event_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_tickets_purchase_date ON tickets(purchase_date);

-- Users table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- ============================================
-- SAMPLE DATA FOR TESTING
-- ============================================

-- Update existing events with new fields
UPDATE events SET 
    price = 1000.0,
    is_free = FALSE,
    tickets_available = 100,
    is_outdoor = FALSE,
    wheelchair_accessible = TRUE,
    hearing_loop_available = FALSE,
    seated_venue = TRUE
WHERE price IS NULL;

-- Set some events as free
UPDATE events SET 
    price = 0.0,
    is_free = TRUE
WHERE id % 3 = 0;

-- Set some events as outdoor
UPDATE events SET 
    is_outdoor = TRUE
WHERE id % 2 = 0;

-- ============================================
-- VIEWS FOR REPORTING
-- ============================================

-- View: Event Performance Summary
CREATE OR REPLACE VIEW event_performance_summary AS
SELECT 
    e.id,
    e.name,
    e.start_date,
    ea.total_views,
    ea.completed_purchases,
    ea.total_revenue,
    ea.overall_conversion_rate,
    COUNT(t.id) as tickets_sold,
    u.full_name as organizer_name
FROM events e
LEFT JOIN event_analytics ea ON e.id = ea.event_id
LEFT JOIN tickets t ON e.id = t.event_id
LEFT JOIN users u ON e.organizer_id = u.id
GROUP BY e.id, e.name, e.start_date, ea.total_views, ea.completed_purchases, 
         ea.total_revenue, ea.overall_conversion_rate, u.full_name;

-- View: User Engagement Summary
CREATE OR REPLACE VIEW user_engagement_summary AS
SELECT 
    u.id,
    u.full_name,
    u.email,
    COUNT(DISTINCT ui.event_id) as events_viewed,
    COUNT(DISTINCT CASE WHEN ui.type = 'SAVE' THEN ui.event_id END) as events_saved,
    COUNT(DISTINCT t.event_id) as tickets_purchased,
    COUNT(DISTINCT f.id) as friends_count
FROM users u
LEFT JOIN user_interactions ui ON u.id = ui.user_id
LEFT JOIN tickets t ON u.id = t.user_id
LEFT JOIN user_following f ON u.id = f.follower_id
GROUP BY u.id, u.full_name, u.email;

-- ============================================
-- STORED PROCEDURES
-- ============================================

DELIMITER //

-- Procedure: Calculate Event Conversion Rates
CREATE PROCEDURE IF NOT EXISTS calculate_conversion_rates(IN event_id_param BIGINT)
BEGIN
    UPDATE event_analytics
    SET 
        view_to_save_rate = CASE WHEN total_views > 0 THEN (total_saves / total_views) * 100 ELSE 0 END,
        save_to_ticket_rate = CASE WHEN total_saves > 0 THEN (ticket_page_views / total_saves) * 100 ELSE 0 END,
        ticket_to_purchase_rate = CASE WHEN ticket_page_views > 0 THEN (completed_purchases / ticket_page_views) * 100 ELSE 0 END,
        overall_conversion_rate = CASE WHEN total_views > 0 THEN (completed_purchases / total_views) * 100 ELSE 0 END,
        last_updated = NOW()
    WHERE event_id = event_id_param;
END //

DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

DELIMITER //

-- Trigger: Auto-create analytics when event is created
CREATE TRIGGER IF NOT EXISTS after_event_insert
AFTER INSERT ON events
FOR EACH ROW
BEGIN
    INSERT INTO event_analytics (event_id, last_updated)
    VALUES (NEW.id, NOW());
END //

-- Trigger: Update analytics on ticket purchase
CREATE TRIGGER IF NOT EXISTS after_ticket_insert
AFTER INSERT ON tickets
FOR EACH ROW
BEGIN
    UPDATE event_analytics
    SET 
        completed_purchases = completed_purchases + 1,
        last_updated = NOW()
    WHERE event_id = NEW.event_id;
END //

DELIMITER ;

-- ============================================
-- GRANTS (if using separate application user)
-- ============================================

-- GRANT ALL PRIVILEGES ON tirana_events.* TO 'tirana_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Check all tables exist
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;

-- Check row counts
SELECT 
    'users' as table_name, COUNT(*) as row_count FROM users
UNION ALL
SELECT 'events', COUNT(*) FROM events
UNION ALL
SELECT 'tickets', COUNT(*) FROM tickets
UNION ALL
SELECT 'categories', COUNT(*) FROM categories
UNION ALL
SELECT 'user_interactions', COUNT(*) FROM user_interactions
UNION ALL
SELECT 'user_preferences', COUNT(*) FROM user_preferences
UNION ALL
SELECT 'friend_activities', COUNT(*) FROM friend_activities
UNION ALL
SELECT 'event_chats', COUNT(*) FROM event_chats
UNION ALL
SELECT 'event_invites', COUNT(*) FROM event_invites
UNION ALL
SELECT 'filter_presets', COUNT(*) FROM filter_presets
UNION ALL
SELECT 'device_tokens', COUNT(*) FROM device_tokens
UNION ALL
SELECT 'push_notifications', COUNT(*) FROM push_notifications
UNION ALL
SELECT 'event_analytics', COUNT(*) FROM event_analytics;

-- ============================================
-- END OF MIGRATIONS
-- ============================================
