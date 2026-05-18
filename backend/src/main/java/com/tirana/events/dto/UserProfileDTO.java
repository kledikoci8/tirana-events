package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileDTO {
    private Long id;
    private String email;
    private String fullName;
    private String profileImage;
    private long eventsCount;
    private long ticketsCount;
    private long savedCount;
    private long followingCount;
    private List<String> interests;
}
