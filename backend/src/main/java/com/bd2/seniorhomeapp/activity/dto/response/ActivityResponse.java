package com.bd2.seniorhomeapp.activity.dto.response;

import com.bd2.seniorhomeapp.activity.entity.ActivityType;

import java.math.BigDecimal;

public record ActivityResponse(
        Long id,
        String name,
        ActivityType type,
        BigDecimal price,
        String seniorHome,
        String description,
        int instructors,
        int participants
) {
}
