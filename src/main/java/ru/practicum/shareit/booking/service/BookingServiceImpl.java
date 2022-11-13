package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDtoTwo addBooking(Long userId, BookingDto bookingDto) {
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        Optional<User> user = userRepository.findById(userId);
        LocalDateTime now = LocalDateTime.now();
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователя с " + userId + " не существует");
        }
        if (item.isPresent() && !item.get().getAvailable()) {
            throw new BadRequestException("Невозможно забронировать вещь: " + item.get().getName());
        }
        if (item.isEmpty()) {
            throw new NotFoundException("Невозможно создать запрос на бронирование");
        }
        if (Objects.equals(item.get().getOwner().getId(), userId)) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        if (bookingDto.getEnd().isBefore(now) ||
                bookingDto.getStart().isBefore(now) ||
                (bookingDto.getEnd().isBefore(bookingDto.getStart()) &&
                        !bookingDto.getEnd().equals(bookingDto.getStart()))) {

            throw new BadRequestException("Невозможно создать запрос на бронирование c данной датой");
        }
        bookingDto.setBookerId(userId);
        bookingDto.setStatus(State.WAITING);
        Booking booking = repository.save(BookingMapper.toBooking(bookingDto, item.get(), user.get()));
        return BookingMapper.toBookingDtoFrom(booking);
    }

    @Transactional
    @Override
    public BookingDtoTwo updateStatusBooking(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> booking = repository.findById(bookingId);
        Booking booking1;
        if (booking.isEmpty()) {
            throw new NotFoundException("Отсутсвуют данные о бронировании");
        }
        booking1 = booking.get();
        Long ownerId = booking1.getItem().getOwner().getId();
        if (!Objects.equals(userId, ownerId)) {
            throw new NotFoundException("Для пользователя с id " + userId + " нет доступа");
        }
        if (!Objects.equals(String.valueOf(booking1.getStatus()), "WAITING")) {
            throw new BadRequestException("Статус бронирования уже изменен");
        }
        if (approved) {
            booking1.setStatus(State.APPROVED);
        } else {
            booking1.setStatus(State.REJECTED);
        }
        return BookingMapper.toBookingDtoFrom(repository.save(booking1));
    }

    @Override
    public BookingDtoTwo getBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = repository.findById(bookingId);
        Long ownerId;
        Long bookerId;
        if (booking.isPresent()) {
            ownerId = booking.get().getItem().getOwner().getId();
            bookerId = booking.get().getBooker().getId();
            if (Objects.equals(ownerId, userId) || Objects.equals(bookerId, userId)) {
                return BookingMapper.toBookingDtoFrom(booking.get());
            }
            throw new NotFoundException("Для пользователя с id " + userId + " нет доступа");
        }
        throw new NotFoundException("Запрос на бронирование не найден");
    }

    @Override
    public Collection<BookingDtoTwo> getBookingByBooker(Long usersId, String status) {
        Optional<User> booker = userRepository.findById(usersId);
        List<Booking> bookingsByBooker;
        if (booker.isEmpty()) {
            throw new NotFoundException("Для пользователя нет доступа");
        }
        if (status == null || status.equals("")) {
            status = "ALL";
        }
        switch (status) {
            case "ALL":
                bookingsByBooker = repository.findByBookerOrderByStartDesc(booker.get());
                break;
            case "CURRENT":
                bookingsByBooker = repository.findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(booker.get(),
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookingsByBooker = repository.findByBookerAndStartBeforeAndEndBeforeOrderByStartDesc(booker.get(),
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByBooker = repository.findByBookerAndStartAfterOrderByStartDesc(booker.get(), LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByBooker = repository.findByBookerAndStatusOrderByStartDesc(booker.get(), State.WAITING);
                break;
            case "REJECTED":
                bookingsByBooker = repository.findByBookerAndStatusOrderByStartDesc(booker.get(), State.REJECTED);
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByBooker);
    }

    @Override
    public Collection<BookingDtoTwo> getBookingByOwner(Long usersId, String status) {
        Optional<User> owner = userRepository.findById(usersId);
        List<Booking> bookingsByOwner;
        if (owner.isEmpty()) {
            throw new NotFoundException("Для пользователя нет доступа");
        }
        if (status == null || status.equals("")) {
            status = "ALL";
        }
        switch (status) {
            case "ALL":
                bookingsByOwner = repository.findByBookingForOwnerWithAll(usersId);
                break;
            case "CURRENT":
                bookingsByOwner = repository.findByBookingForOwnerWithCurrent(usersId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookingsByOwner = repository.findByBookingForOwnerWithPast(usersId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case "FUTURE":
                bookingsByOwner = repository.findByBookingForOwnerWithFuture(usersId, LocalDateTime.now());
                break;
            case "WAITING":
                bookingsByOwner = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "WAITING");
                break;
            case "REJECTED":
                bookingsByOwner = repository.findByBookingForOwnerWithWaitingOrRejected(usersId, "REJECTED");
                break;
            default:
                throw new StatusBadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoFrom(bookingsByOwner);
    }
}
