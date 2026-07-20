package com.manolo.jobtracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@ToString(of = {"id", "token", "expiryDate"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column
    private Instant revokedAt;
}