package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Текст комментария не должн быть пустым или содержать только пробельные символы")
    private String text;

    @ManyToOne
    @NotNull(message = "Вещь, к которой относится комментарий не должна быть null")
    private Item item;

    @ManyToOne
    @NotNull(message = "Автор комментария не должн быть null")
    private User author;

    @Column(name = "created_at")
    private LocalDateTime created;
}