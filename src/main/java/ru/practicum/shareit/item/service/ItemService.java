package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithComment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDtoWithDate getItem(Long userId, Long itemId);

    ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDtoWithDate> getAllOwnItems(Long userId);

    Collection<ItemDto> getItemsForRent(String substring);

    ItemDtoWithComment addComment(Long authorId, Long itemId, ItemDtoWithComment comment);
}
