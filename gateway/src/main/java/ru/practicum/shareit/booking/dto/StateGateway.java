package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum StateGateway {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static Optional<StateGateway> from(String stringState) {
        for (StateGateway stateGateway : values()) {
            if (stateGateway.name().equalsIgnoreCase(stringState)) {
                return Optional.of(stateGateway);
            }
        }
        return Optional.empty();
    }
}
