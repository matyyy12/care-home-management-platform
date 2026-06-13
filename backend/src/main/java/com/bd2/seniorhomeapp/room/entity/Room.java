package com.bd2.seniorhomeapp.room.entity;

import java.util.ArrayList;
import java.util.List;

import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_number", "senior_home_id"})
)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @NotNull
    @Column(nullable = false)
    private Integer floor;

    @NotNull
    @Column(name = "in_use", nullable = false)
    private Boolean inUse = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "senior_home_id", nullable = false)
    private SeniorHome seniorHome;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoomType type;

    @Min(1)
    private Integer capacity;

    @Convert(converter = RoomStandardConverter.class)
    @Column(length = 20)
    private RoomStandard standard;

    @Size(max = 100)
    @Column(name = "office_kind", length = 100)
    private String officeKind;

    @Size(max = 200)
    @Column(name = "equipment_description", length = 200)
    private String equipmentDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_game_equipment", joinColumns = @JoinColumn(name = "room_id"))
    private List<GameRoomEquipment> gameEquipment = new ArrayList<>();
}
