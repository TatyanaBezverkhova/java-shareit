package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemDbStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 0;

    @Override
    public ItemDto addItem(User user, ItemDto itemDto) {
        id = id + 1;
        itemDto.setId(id);
        items.put(id, ItemMapper.toItem(itemDto, user));
        log.info("Добавлена новая вещь: {},", itemDto.getName());
        return itemDto;
    }

    @Override
    public ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        items.put(itemId, item);
        log.info("Обновлены данные для вещи: {}", itemDto.getName());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Optional<ItemDto> getItem(Long userId, Long itemId) {
        return Optional.ofNullable(ItemMapper.toItemDto(items.get(itemId)));
    }

    @Override
    public Collection<ItemDto> getAllOwnItems(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        log.info("Колличество найденных вещей: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> getItemsForRent(Long userId, String substring) {
        List<ItemDto> itemsDto = new ArrayList<>();
        if (!substring.equals("")) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(substring.toLowerCase())
                        || item.getDescription().toLowerCase().contains(substring.toLowerCase()))
                        && item.getAvailable()) {
                    itemsDto.add(ItemMapper.toItemDto(item));
                }
            }
        }
        log.info("Колличество найденных вещей: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Optional<Item> getItemFromMap(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }
}
