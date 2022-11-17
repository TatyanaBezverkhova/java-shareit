package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

public interface ItemStorage {

    ItemDto addItem(User user, ItemDto itemDto);

    ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto);

    Optional<ItemDto> getItem(Long userId, Long itemId);

    Collection<ItemDto> getAllOwnItems(Long userId);

    Collection<ItemDto> getItemsForRent(String substring);

    Optional<Item> getItemFromMap(Long itemId);
}
