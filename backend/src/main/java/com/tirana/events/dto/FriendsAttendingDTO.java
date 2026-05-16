package com.tirana.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class FriendsAttendingDTO {
    private Long count;
    private List<UserDTO> friends;
}
