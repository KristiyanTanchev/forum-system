package com.team3.forum.external.client;


import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.external.dto.ExternalMediaDataDto;
import com.team3.forum.helpers.MediaMetaDataMapper;
import com.team3.forum.models.Folder;
import com.team3.forum.models.MediaMetaData;
import com.team3.forum.services.MediaMetaDataServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@RequiredArgsConstructor
@Service
public class ExternalMetaDataClient {
    private final WebClient omdbWebClient;
    private final MediaMetaDataServiceImpl mediaMetaDataService;
    private final MediaMetaDataMapper mediaMetaDataMapper;

    @Value("${external.omdb.apikey}")
    private String apiKey;

    public ExternalMediaDataDto getMetaData(String imdbId) {
        ExternalMediaDataDto dto = omdbWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("apikey", apiKey)
                        .queryParam("i", imdbId)
                        .build())
                .retrieve()
                .bodyToMono(ExternalMediaDataDto.class)
                .block();

        if (dto == null) {
            throw new EntityNotFoundException("Movie or series", "imdb id", imdbId);
        }

        return dto;
    }

    @Async
    public void syncMetaData(Folder folder) {
        try {
            ExternalMediaDataDto mediaDataDto = getMetaData(folder.getImdbId());
            MediaMetaData mediaMetaData = mediaMetaDataMapper.toEntity(mediaDataDto);
            mediaMetaDataService.saveMetaData(mediaMetaData);
        } catch (Exception e) {
            log.warn("Movie {} failed to sync", folder.getName());
            log.warn(e.getMessage());
            log.warn(e.getStackTrace().toString());
        }
    }
}
