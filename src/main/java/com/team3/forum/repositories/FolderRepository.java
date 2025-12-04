package com.team3.forum.repositories;

import com.team3.forum.models.Folder;

import java.time.LocalDateTime;
import java.util.List;

public interface FolderRepository {
    Folder save(Folder entity);

    Folder findById(int id);

    boolean existsById(int id);

    List<Folder> findAll();

    void deleteById(int id);

    void delete(Folder entity);

    Folder findBySlug(String slug);

    List<Folder> getFoldersByParentFolder(Folder parentFolder);

    Folder findByParentFolderAndSlug(Folder parentFolder, String slug);

    LocalDateTime getLastPostDate(Folder folder);

    LocalDateTime getLastCommentDate(Folder folder);
}
