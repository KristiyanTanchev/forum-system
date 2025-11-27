package com.team3.forum.services;

import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.folderDtos.FolderUpdateDto;

import java.util.List;

public interface FolderService {
    List<Folder> findAll();

    Folder findById(int id);

    void deleteById(int id, int requesterId);

    Folder create(Folder folder, List<String> slugs, int requesterId);

    Folder update(List<String> slugs, FolderUpdateDto folderUpdateDto, int requesterId);

    List<Post> getPostsInFolder(Folder folder);

    List<Folder> findHomeFolders();

    Folder getFolderByPath(List<String> slugs);

    List<Folder> getSiblingFolders(Folder folder);
}
