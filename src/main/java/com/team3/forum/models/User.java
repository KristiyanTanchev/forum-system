package com.team3.forum.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team3.forum.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "user_id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Builder.Default
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany(mappedBy = "likedBy")
    @JsonIgnore
    @Builder.Default
    private Set<Post> likedPosts = new HashSet<>();

    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isModerator() {
        return role == Role.MODERATOR || role == Role.ADMIN;
    }
}
