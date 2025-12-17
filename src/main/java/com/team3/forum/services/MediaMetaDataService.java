package com.team3.forum.services;

import com.team3.forum.models.MediaMetaData;

public interface MediaMetaDataService {
    void saveMetaData(MediaMetaData metaData);

    MediaMetaData findByFolderImdbId(String imdbId);
}
