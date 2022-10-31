package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllOwnItems(Long userId);

    Collection<ItemDto> getItemsForRent(Long userId, String substring);
}
