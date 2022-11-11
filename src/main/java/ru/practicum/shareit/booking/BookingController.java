package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoTwo addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoTwo updateStatusBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.updateStatusBooking(userId, bookingId, approved);

    }

    @GetMapping("/{bookingId}")
    public BookingDtoTwo getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoTwo> findBookingByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(required = false)
                                                         String state) {
        return bookingService.getBookingByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoTwo> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(required = false)
                                                        String state) {
        return bookingService.getBookingByOwner(userId, state);
    }

}
