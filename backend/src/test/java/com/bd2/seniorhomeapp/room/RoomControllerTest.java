package com.bd2.seniorhomeapp.room;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bd2.seniorhomeapp.room.controller.RoomController;
import com.bd2.seniorhomeapp.room.dto.request.RoomRequest;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipment;
import com.bd2.seniorhomeapp.room.entity.GameRoomEquipmentType;
import com.bd2.seniorhomeapp.room.entity.Room;
import com.bd2.seniorhomeapp.room.entity.RoomType;
import com.bd2.seniorhomeapp.room.service.RoomService;
import com.bd2.seniorhomeapp.senior.entity.SeniorStatus;
import com.bd2.seniorhomeapp.senior.repository.SeniorRepository;
import com.bd2.seniorhomeapp.seniorHome.entity.SeniorHome;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private SeniorRepository seniorRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RoomController(roomService, seniorRepository)).build();
    }

    @Test
    void getRoomsBySeniorHomeShouldReturnRoomResponses() throws Exception {
        Room room = sampleResidentialRoom(1L);
        when(roomService.findRooms(null, 1L)).thenReturn(List.of(room));
        when(seniorRepository.countByRoom_IdAndStatus(1L, SeniorStatus.ACTIVE)).thenReturn(1L);

        mockMvc.perform(get("/api/rooms").param("seniorHomeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("101"))
                .andExpect(jsonPath("$[0].type").value("RESIDENTIAL"))
                .andExpect(jsonPath("$[0].seniorHome").value("Złota Jesień"));

        verify(roomService).findRooms(null, 1L);
    }

    @Test
    void createRoomShouldReturnCreatedRoomResponse() throws Exception {
        Room room = sampleResidentialRoom(1L);
        when(roomService.createRoom(any(RoomRequest.class))).thenReturn(room);
        when(seniorRepository.countByRoom_IdAndStatus(1L, SeniorStatus.ACTIVE)).thenReturn(0L);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomNumber": "101",
                                  "floor": 1,
                                  "inUse": true,
                                  "seniorHomeId": 1,
                                  "type": "RESIDENTIAL",
                                  "capacity": 2,
                                  "standard": "Podstawowy"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.number").value("101"))
                .andExpect(jsonPath("$.capacity").value(2));

        verify(roomService).createRoom(any(RoomRequest.class));
    }

    @Test
    void updateRoomShouldReturnUpdatedRoomResponse() throws Exception {
        Room room = sampleResidentialRoom(1L);
        room.setStandard("Podwyższony");
        when(roomService.updateRoom(eq(1L), any())).thenReturn(room);
        when(seniorRepository.countByRoom_IdAndStatus(1L, SeniorStatus.ACTIVE)).thenReturn(0L);

        mockMvc.perform(patch("/api/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "standard": "Podwyższony"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.standard").value("Podwyższony"));

        verify(roomService).updateRoom(eq(1L), any());
    }

    @Test
    void createGameRoomShouldReturnGameEquipmentList() throws Exception {
        Room room = sampleGameRoom(2L);
        when(roomService.createRoom(any(RoomRequest.class))).thenReturn(room);
        when(seniorRepository.countByRoom_IdAndStatus(2L, SeniorStatus.ACTIVE)).thenReturn(0L);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomNumber": "SG1",
                                  "floor": 0,
                                  "inUse": true,
                                  "seniorHomeId": 1,
                                  "type": "GAME_ROOM",
                                  "gameEquipment": [
                                    {"type": "TV", "quantity": 1},
                                    {"type": "COMPUTER", "quantity": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameEquipment[0].type").value("TV"))
                .andExpect(jsonPath("$.gameEquipment[1].quantity").value(2));

        verify(roomService).createRoom(any(RoomRequest.class));
    }

    @Test
    void deleteRoomShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());

        verify(roomService).deleteRoom(1L);
    }

    private static Room sampleResidentialRoom(Long id) {
        SeniorHome seniorHome = new SeniorHome();
        seniorHome.setId(1L);
        seniorHome.setName("Złota Jesień");

        Room room = new Room();
        room.setId(id);
        room.setRoomNumber("101");
        room.setFloor(1);
        room.setInUse(true);
        room.setSeniorHome(seniorHome);
        room.setType(RoomType.RESIDENTIAL);
        room.setCapacity(2);
        room.setStandard("Podstawowy");
        return room;
    }

    private static Room sampleGameRoom(Long id) {
        SeniorHome seniorHome = new SeniorHome();
        seniorHome.setId(1L);
        seniorHome.setName("Złota Jesień");

        Room room = new Room();
        room.setId(id);
        room.setRoomNumber("SG1");
        room.setFloor(0);
        room.setInUse(true);
        room.setSeniorHome(seniorHome);
        room.setType(RoomType.GAME_ROOM);
        room.setGameEquipment(List.of(
                new GameRoomEquipment(GameRoomEquipmentType.TV, 1),
                new GameRoomEquipment(GameRoomEquipmentType.COMPUTER, 2)));
        return room;
    }
}
