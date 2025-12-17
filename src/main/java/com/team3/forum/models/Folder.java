package com.team3.forum.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "folders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "folder_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder")
    @Builder.Default
    private Set<Folder> childFolders = new HashSet<>();

    @OneToMany(mappedBy = "folder")
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    private String name;

    private String slug;

    private String description;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "imdb_id")
    private String imdbId;
}
