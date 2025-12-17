package com.team3.forum.services;

import com.team3.forum.models.MediaMetaData;
import com.team3.forum.repositories.MediaMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaMetaDataServiceImpl implements MediaMetaDataService {
    private final MediaMetaDataRepository mediaMetaDataRepository;


    @Override
    public void saveMetaData(MediaMetaData metaData) {
        mediaMetaDataRepository.save(metaData);
    }

    @Override
    public MediaMetaData findByFolderImdbId(String imdbId) {
        return mediaMetaDataRepository.findById(imdbId)
                .orElse(null);
    }
}
