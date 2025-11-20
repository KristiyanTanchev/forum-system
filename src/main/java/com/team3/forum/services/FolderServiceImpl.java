package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.exceptions.FolderNotEmptyException;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.folderDtos.FolderUpdateDto;
import com.team3.forum.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FolderServiceImpl implements FolderService{
    public static final String EDIT_AUTHORIZATION_ERROR = "You cannot edit this folder.";
    public static final String DELETE_AUTHORIZATION_ERROR = "You cannot delete this folder.";


    private final FolderRepository folderRepository;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Folder findById(int id) {
        return folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("folder", id));
    }

    @Override
    public void deleteById(int id, User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException(DELETE_AUTHORIZATION_ERROR);
        }
        Folder persistent = folderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("folder", id));
        if (!persistent.getPosts().isEmpty()
                || !persistent.getChildFolders().isEmpty()){
            throw new FolderNotEmptyException(id);
        }
        folderRepository.delete(persistent);
    }

    @Override
    public Folder create(Folder folder, User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException(DELETE_AUTHORIZATION_ERROR);
        }
        Folder parent = folder.getParentFolder();
        List<String> siblingsSlugs = parent.getChildFolders().stream()
                .map(Folder::getSlug)
                .toList();
        if (siblingsSlugs.contains(folder.getSlug())){
            throw new EntityUpdateConflictException("The slug must be unique for the subfolder");
        }
        return folderRepository.save(folder);
    }

    @Override
    public Folder update(Folder folder, FolderUpdateDto folderUpdateDto, User requester) {
        if (!requester.isAdmin()) {
            throw new AuthorizationException(EDIT_AUTHORIZATION_ERROR);
        }
        Folder persistent = folderRepository.findById(folder.getId())
                .orElseThrow(() -> new EntityNotFoundException("folder", folder.getId()));
        persistent.setName(folderUpdateDto.getName());
        persistent.setSlug(folderUpdateDto.getSlug());

        return folderRepository.save(persistent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsInFolder(Folder folder){
        Folder persistent = folderRepository.findById(folder.getId())
                .orElseThrow(() -> new EntityNotFoundException("folder", folder.getId()));
        return persistent.getPosts();
    }

    @Override
    public List<Folder> findHomeFolders(){
        return folderRepository.getFoldersByParentFolder(null);
    }

    @Override
    public Folder getFolderByPath(List<String> slugs) {
        if (slugs.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        Folder current = folderRepository
                .findBySlug(slugs.get(0));
        if (current == null){
            throw new EntityNotFoundException("folder", "slug", slugs.get(0));
        }

        for (int i = 1; i < slugs.size(); i++) {
            String slug = slugs.get(i);
            current = folderRepository
                    .findByParentFolderAndSlug(current, slug)
                    .orElseThrow(() -> new EntityNotFoundException("Folder path not found"));
        }

        return current;
    }

}
