package com.team3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "action", length = 255)
    private String action;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
