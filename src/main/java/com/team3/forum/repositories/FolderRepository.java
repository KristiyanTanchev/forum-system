package com.team3.forum.repositories;

import com.team3.forum.models.Folder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends ListCrudRepository<Folder, Integer> {
    Folder findBySlug(String slug);

    List<Folder> getFoldersByParentFolder(Folder parentFolder);

    Optional<Folder> findByParentFolderAndSlug(Folder parentFolder, String slug);
}
