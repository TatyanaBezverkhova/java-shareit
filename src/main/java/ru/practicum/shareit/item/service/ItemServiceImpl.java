package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) throws BadRequestException {
        Optional<UserDto> user = userStorage.getUser(userId);
        if (user.isPresent()) {
            return itemStorage.addItem(UserMapper.toUser(user.get()), itemDto);

        }
        throw new NotFoundException("Пользователя с id " + userId + " не существует");
    }

    @Override
    public ItemDto changeItem(Long userId, Long itemId, ItemDto itemDto) throws BadRequestException {
        Item item = itemStorage.getItemFromMap(itemId).get();
        if (item.getOwner().getId() == userId) {
            return itemStorage.changeItem(userId, itemId, itemDto);
        }
        throw new ForbiddenException("Для пользователя с id " + userId + " нет доступа");
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        if (itemStorage.getItem(null, itemId).isPresent()) {
            return itemStorage.getItem(null, itemId).get();
        }
        throw new NotFoundException("Предмета с id " + itemId + " не существует");
    }

    @Override
    public Collection<ItemDto> getAllOwnItems(Long userId) {
        return itemStorage.getAllOwnItems(userId);
    }

    @Override
    public Collection<ItemDto> getItemsForRent(Long userId, String substring) {
        return itemStorage.getItemsForRent(userId, substring);
    }
}
