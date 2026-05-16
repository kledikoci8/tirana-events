package com.tirana.events.dto;

import lombok.Data;

@Data
public class UpdateNotificationPreferencesRequest {
    private Boolean notifyEventReminder = true;
    private Boolean notifyPriceDrop = true;
    private Boolean notifyFriendActivity = true;
    private Boolean notifyNearbyEvents = true;
    private Integer quietHoursStart = 23;
    private Integer quietHoursEnd = 8;
}
