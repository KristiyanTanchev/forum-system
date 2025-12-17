package com.team3.forum.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ExternalMediaDataDto {

    private String imdbID;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Released")
    private String releaseDate;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("imdbRating")
    private float imdbRating;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("totalSeasons")
    private Integer totalSeasons;

}