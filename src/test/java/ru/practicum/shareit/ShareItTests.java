package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import ru.practicum.shareit.booking.dto.BookingDtoTwo;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusBadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@TestExecutionListeners({DirtiesContextBeforeModesTestExecutionListener.class})
class ShareItTests {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final ItemRequestService requestService;

    @Test
    void contextLoads() {
    }

    @Test
    void getAllOwnItemTest() {
        List<ItemDtoWithBooking> items = itemService.getAllOwnItems(1L, null, null);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("Wings");
        assertThat(items.get(0).getOwner().getId()).isEqualTo(1);
    }

    @Test
    void getAllOwnItemNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> itemService.getAllOwnItems(5L, null, null));
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");

    }

    @Test
    void getAllOwnItemFromOrSizeLessThanZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getAllOwnItems(1L, -1, 0));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());
    }

    @Test
    void getAllOwnItemSizeEqualToZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getAllOwnItems(1L, 1, 0));

        Assertions.assertEquals("Size не может принимать значение 0", exception.getMessage());
    }

    @Test
    void getItemsForRentTest() {
        List<ItemDto> items = (List<ItemDto>) itemService.getItemsForRent("Fo", null, null);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("Fork");
        assertThat(items.get(0).getOwner().getName()).isEqualTo("Kuzya");
        items = (List<ItemDto>) itemService.getItemsForRent("need", 0, 2);
        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(2);
    }

    @Test
    void getItemForRentEqualToZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getItemsForRent("F", 0, 0));

        Assertions.assertEquals("Size не может принимать значение 0", exception.getMessage());

    }

    @Test
    void getItemsForRentFromOrSizeLessThanZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> itemService.getItemsForRent("F", -1, 0));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());

    }

    @Test
    void getBookingByBookerStateAllTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByBooker(3L,
                null, null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
        bookings = (List<BookingDtoTwo>) bookingService.getBookingByBooker(3L, "ALL", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingByBookerStateCurrentTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByBooker(3L,
                "CURRENT", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingByBookerStateFutureTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByBooker(2L,
                "FUTURE", null, null);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(3);
    }

    @Test
    void getBookingByBookerStateWaitingOrRejectedTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByBooker(1L,
                "WAITING", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getBookingsByBookerUnknownStatePageableTest() {
        final StatusBadRequestException exception = Assertions.assertThrows(
                StatusBadRequestException.class,
                () -> bookingService.getBookingByBooker(3L, "RED", 0, 1));

        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());

    }

    @Test
    void getBookingsByBookerNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByBooker(5L, null, 0, 1));

        Assertions.assertEquals("Для пользователя нет доступа", exception.getMessage());

    }

    @Test
    void getBookingsByBookerSizeOrPageLessZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getBookingByBooker(3L, null, -1, 1));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());

    }

    @Test
    void getBookingsByBookerSizeEqualZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getBookingByBooker(3L, null, 0, 0));

        Assertions.assertEquals("Size не может принимать значение 0", exception.getMessage());

    }

    @Test
    void getBookingsByOwnerNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingByOwner(8L, null, 0, 1));

        Assertions.assertEquals("Для пользователя нет доступа", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerSizeOrPageLessZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingService.getBookingByOwner(1L, null, -1, 1));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());

    }

    @Test
    void getBookingsByOwnerByStateAllTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(1L,
                null, 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
        bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(1L, "ALL", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingsByOwnerByStatePastTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(3L,
                "PAST", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(1);
    }

    @Test
    void getBookingsByOwnerByStateCurrentTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(1L,
                "CURRENT", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(2);
    }

    @Test
    void getBookingsByOwnerByStateFutureTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(2L,
                "FUTURE", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getBookingsByOwnerByStateWaitingOrRejectedTest() {
        List<BookingDtoTwo> bookings = (List<BookingDtoTwo>) bookingService.getBookingByOwner(2L,
                "WAITING", 0, 1);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getId()).isEqualTo(4);
    }

    @Test
    void getAllOwnRequestTest() {
        List<RequestDto> requests = requestService.findAllOwnRequest(3L);
        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isEqualTo(1);
        assertThat(requests.get(0).getId()).isEqualTo(3);
    }

    @Test
    void findAllOwnRequestsNotFoundUserTest() {
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> requestService.findAllOwnRequest(5L));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void findAllRequestsTest() {
        List<RequestDto> requests = requestService.findAllRequest(1L, null, null);
        assertThat(requests).isNotEmpty();
        assertThat(requests.size()).isEqualTo(4);
        assertThat(requests.get(0).getId()).isEqualTo(1);
    }

    @Test
    void findAllRequestsSizeOrPageLessZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAllRequest(1L, 1, -1));

        Assertions.assertEquals("From или size не могут принимать отрицательноге значение",
                exception.getMessage());
    }

    @Test
    void findAllRequestsSizeEqualZeroTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.findAllRequest(1L, 1, 0));

        Assertions.assertEquals("Size не может принимать значение 0",
                exception.getMessage());

    }
}
