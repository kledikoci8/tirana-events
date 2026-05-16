package com.tirana.events.dto;

import com.tirana.events.model.DeviceToken;
import lombok.Data;

@Data
public class RegisterTokenRequest {
    private String token;
    private DeviceToken.DeviceType deviceType;
}
