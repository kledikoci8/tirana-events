package com.tirana.events.dto;

import lombok.Data;

@Data
public class CuratorDTO {
    private Long id;
    private Long userId;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String coverImageUrl;
    private Boolean isVerified;
    private Integer followersCount;
    private Integer curatedListsCount;
    private Integer totalTicketsSold;
    private Boolean isFollowedByCurrentUser;
}
