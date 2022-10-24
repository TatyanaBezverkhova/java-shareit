package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto addUser(UserDto userDto) throws BadRequestException {
        for (UserDto userDto1 : getAllUsers()) {
            if (userDto.getEmail().equals(userDto1.getEmail())) {
                throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
            }
        }
        return userStorage.addUser(userDto);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws BadRequestException {
        if (userDto.getEmail() != null) {
            for (UserDto userDto1 : getAllUsers()) {
                if (userDto1.getId() != userId && userDto.getEmail().equals(userDto1.getEmail())) {
                    throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
                }
            }
        }
        if (userStorage.getUser(userId).isPresent()) {
            return userStorage.updateUser(userId, userDto);
        }
        throw new NotFoundException("Пользователя с id " + userDto.getId() + " не существует");
    }

    @Override
    public void deleteUser(Long id) {
        if (userStorage.getUser(id).isPresent()) {
            userStorage.deleteUser(id);
            return;
        }
        throw new NotFoundException("Пользователя с id " + id + " не существует");
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public UserDto getUser(Long id) {
        if (userStorage.getUser(id).isPresent()) {
            return userStorage.getUser(id).get();
        }
        throw new NotFoundException("Пользователя с id " + id + " не существует");
    }
}
