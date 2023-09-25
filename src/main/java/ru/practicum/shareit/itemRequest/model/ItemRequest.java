package ru.practicum.shareit.itemRequest.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Описание не должно быть пустым или содержать только пробельные символы")
    private String description;

    @ManyToOne
    @NotNull(message = "Пользователь, создавший запрос не должен быть null")
    private User requestor;

    @Column(name = "created_at")
    private LocalDateTime created;
}