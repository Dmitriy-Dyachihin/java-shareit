package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByBookerAndStatus(User user, Status status, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime localDateTime, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User owner, Status status, Sort sort);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status status, LocalDateTime end);

    Booking findFirstByItemAndStatusEqualsAndStartLessThan(Item item, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemAndStatusEqualsAndStartAfter(Item item, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStatusEqualsAndStartLessThan(Long id, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStatusEqualsAndStartAfter(Long id, Status status, LocalDateTime end, Sort sort);

}
