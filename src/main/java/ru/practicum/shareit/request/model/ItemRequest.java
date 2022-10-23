package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private Long id;

    private String description;

    private User requestor;

    @NotNull
    private LocalDateTime created;
}
