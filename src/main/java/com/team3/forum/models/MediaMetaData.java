package com.team3.forum.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "media_data")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class MediaMetaData {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "imdb_id")
    private String imdbId;

    private String title;

    private int year;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private String genres;

    private String plot;

    private String language;

    private String country;

    private String poster;

    @Column(name = "imdb_rating")
    private Float imdbRating;

    private String type;

    @Column(name = "total_seasons")
    private Integer totalSeasons;


}
