package com.team3.forum.services;

import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.folderDtos.FolderCreateDto;
import com.team3.forum.models.folderDtos.FolderResponseDto;
import com.team3.forum.models.folderDtos.FolderUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface FolderService {
    List<Folder> findAll();

    Folder findById(int id);

    void deleteById(int id, int requesterId);

    Folder create(FolderCreateDto folderCreateDto, List<String> slugs, int requesterId);

    Folder create(FolderCreateDto folderCreateDto, int requesterId);

    Folder update(List<String> slugs, FolderUpdateDto folderUpdateDto, int requesterId);

    Folder update(FolderUpdateDto folderUpdateDto, int requesterId);

    List<Post> getPostsInFolder(Folder folder);

    List<Folder> findHomeFolders();

    Folder getFolderByPath(List<String> slugs);

    List<Folder> getSiblingFolders(Folder folder);

    LocalDateTime getLastActivity(Folder folder);

    List<String> buildSlugPath(Folder folder);

    FolderResponseDto buildFolderResponseDto(Folder folder);

}
