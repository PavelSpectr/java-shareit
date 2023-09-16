package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDatesDto;
import ru.practicum.shareit.item.dto.CommentDetailsInfoDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

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

    @ManyToOne
    private User owner;

    @OneToOne
    private ItemRequest request;

    @Transient
    private BookingDatesDto lastBooking;

    @Transient
    private BookingDatesDto nextBooking;

    @Transient
    private List<CommentDetailsInfoDto> comments;
}