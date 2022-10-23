package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    String description;

    @NotNull
    private Boolean available;

    private User owner;

    ItemRequest request;
}
