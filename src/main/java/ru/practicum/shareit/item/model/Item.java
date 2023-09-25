package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.dto.CommentDetailsInfoDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "items", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToOne
    private ItemRequest request;

    @Transient
    private BookingDatesDto lastBooking;

    @Transient
    private BookingDatesDto nextBooking;

    @Transient
    private List<CommentDetailsInfoDto> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        if (id == null || item.id == null) return false;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, 42);
    }
}