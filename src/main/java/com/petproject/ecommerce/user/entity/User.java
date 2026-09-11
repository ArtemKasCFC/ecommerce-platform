package com.petproject.ecommerce.user.entity;

import com.petproject.ecommerce.user.enums.Roles;
import com.petproject.ecommerce.user.enums.Statuses;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Statuses status;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(String email, String name, String password, Statuses status, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = Roles.USER;
        this.status = status;
        this.createdAt = createdAt;
    }
}
