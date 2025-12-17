package com.team3.forum.helpers;

import com.team3.forum.external.dto.ExternalMediaDataDto;
import com.team3.forum.models.MediaMetaData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MediaMetaDataMapper {

    public MediaMetaData toEntity(ExternalMediaDataDto dto) {
        if (dto == null) return null;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate releaseDate = dto.getReleaseDate() == null ? null : LocalDate.parse(dto.getReleaseDate(), formatter);

        return MediaMetaData.builder()
                .country(dto.getCountry())
                .language(dto.getLanguage())
                .genres(dto.getGenre())
                .imdbId(dto.getImdbID())
                .imdbRating(dto.getImdbRating())
                .plot(dto.getPlot())
                .poster(dto.getPoster())
                .title(dto.getTitle())
                .releaseDate(releaseDate)
                .type(dto.getType())
                .year(Integer.parseInt(dto.getYear()))
                .totalSeasons(dto.getTotalSeasons() != null ? dto.getTotalSeasons() : null)
                .build();
    }
}
