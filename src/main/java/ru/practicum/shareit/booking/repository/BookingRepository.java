package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker(User user, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerAndStatus(User user, Status status, Pageable pageable);

    List<Booking> findAllByItemOwner(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatus(User owner, Status status, Pageable pageable);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long userId, Long itemId, Status status, LocalDateTime end);

    Booking findFirstByItemAndStatusEqualsAndStartLessThan(Item item, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemAndStatusEqualsAndStartAfter(Item item, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStatusEqualsAndStartLessThan(Long id, Status status, LocalDateTime end, Sort sort);

    Booking findFirstByItemIdAndStatusEqualsAndStartAfter(Long id, Status status, LocalDateTime end, Sort sort);

}
