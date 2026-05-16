package com.tirana.events.dto;

import lombok.Data;
import java.util.Set;

@Data
public class OnboardingRequest {
    private Set<Long> categoryIds;
}
