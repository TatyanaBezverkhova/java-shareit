package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
public class BookingDtoForItem {
    private long id;
    private Long bookerId;
    private LocalDateTime dateTime;
}
