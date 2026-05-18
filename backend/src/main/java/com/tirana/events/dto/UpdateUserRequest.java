package com.tirana.events.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String profileImage;
}
