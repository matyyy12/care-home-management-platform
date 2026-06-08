package com.bd2.seniorhomeapp.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRoomEquipment {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false, length = 20)
    private GameRoomEquipmentType type;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity = 1;
}
