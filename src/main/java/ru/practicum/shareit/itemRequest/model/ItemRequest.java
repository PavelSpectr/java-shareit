package ru.practicum.shareit.itemRequest.model;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemShortInfoDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "item_requests", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;

    @Column(name = "created_at")
    private LocalDateTime created;

    @Transient
    private List<ItemShortInfoDto> items;
}