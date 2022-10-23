package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.storage.UserDbStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserDbStorage userDbStorage;

    @Override
    public UserDto addUser(UserDto userDto) throws BadRequestException {
        for (UserDto userDto1 : getAllUsers()) {
            if (userDto.getEmail().equals(userDto1.getEmail())) {
                throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
            }
        }
        return userDbStorage.addUser(userDto);
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
        if (userDbStorage.getUser(userId).isPresent()) {
            return userDbStorage.updateUser(userId, userDto);
        }
        throw new NotFoundException("Пользователя с id " + userDto.getId() + " не существует");
    }

    @Override
    public void deleteUser(Long id) {
        if (userDbStorage.getUser(id).isPresent()) {
            userDbStorage.deleteUser(id);
            return;
        }
        throw new NotFoundException("Пользователя с id " + id + " не существует");
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    @Override
    public UserDto getUser(Long id) {
        if (userDbStorage.getUser(id).isPresent()) {
            return userDbStorage.getUser(id).get();
        }
        throw new NotFoundException("Пользователя с id " + id + " не существует");
    }
}
