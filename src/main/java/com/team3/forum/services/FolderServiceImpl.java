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
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FolderServiceImpl implements FolderService {
    public static final String CREATE_NO_SLUGS_ERROR = "You cannot create a folder on root level.";
    public static final String EDIT_NO_SLUGS_ERROR = "You cannot edit a folder on root level.";
    public static final String CREATE_AUTHORIZATION_ERROR = "Only admins can create folders.";
    public static final String EDIT_AUTHORIZATION_ERROR = "You cannot edit this folder.";
    public static final String DELETE_AUTHORIZATION_ERROR = "You cannot delete this folder.";
    public static final String CREATE_UNIQUE_SLUG_ERROR = "The slug must be unique among sibling folders.";


    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Folder findById(int id) {
        return folderRepository.findById(id);
    }

    @Override
    public void deleteById(int id, int requesterId) {
        User requester = userRepository.findById(requesterId);
        if (!requester.isAdmin()) {
            throw new AuthorizationException(DELETE_AUTHORIZATION_ERROR);
        }
        Folder persistent = folderRepository.findById(id);
        if (!persistent.getPosts().isEmpty()
                || !persistent.getChildFolders().isEmpty()) {
            throw new FolderNotEmptyException(id);
        }
        folderRepository.delete(persistent);
    }

    @Override
    public Folder create(Folder folder, List<String> slugs, int requesterId) {
        if (slugs.isEmpty()) {
            throw new EntityUpdateConflictException(CREATE_NO_SLUGS_ERROR);
        }
        User requester = userRepository.findById(requesterId);
        if (!requester.isAdmin()) {
            throw new AuthorizationException(CREATE_AUTHORIZATION_ERROR);
        }

        Folder parent = getFolderByPath(slugs);
        folder.setParentFolder(parent);

        validateUniqueSlug(parent, folder);

        return folderRepository.save(folder);
    }

    @Override
    public Folder update(List<String> slugs, FolderUpdateDto folderUpdateDto, int requesterId) {
        if (slugs.isEmpty()) {
            throw new EntityUpdateConflictException(EDIT_NO_SLUGS_ERROR);
        }

        Folder folder = getFolderByPath(slugs);
        if (folder.getParentFolder() == null) {
            throw new EntityUpdateConflictException(EDIT_NO_SLUGS_ERROR);
        }
        User requester = userRepository.findById(requesterId);

        if (!requester.isAdmin()) {
            throw new AuthorizationException(EDIT_AUTHORIZATION_ERROR);
        }

        folder.setName(folderUpdateDto.getName());
        folder.setSlug(folderUpdateDto.getSlug());

        validateUniqueSlug(folder.getParentFolder(), folder);

        return folderRepository.save(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsInFolder(Folder folder) {
        Folder persistent = folderRepository.findById(folder.getId());
        return new ArrayList<>(persistent.getPosts());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findHomeFolders() {
        return folderRepository.getFoldersByParentFolder(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderByPath(List<String> slugs) {
        if (slugs.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        Folder current = folderRepository.findBySlug(slugs.get(0));
        if (current == null) {
            throw new EntityNotFoundException("folder", "slug", slugs.get(0));
        }

        for (int i = 1; i < slugs.size(); i++) {
            String slug = slugs.get(i);
            current = folderRepository.findByParentFolderAndSlug(current, slug);
        }

        return current;
    }

    private void validateUniqueSlug(Folder parent, Folder child) {
        boolean hasSiblingWithTheSameSlug = parent.getChildFolders().stream()
                .anyMatch(f ->
                        Objects.equals(f.getSlug(), child.getSlug()) &&
                                !Objects.equals(f.getId(), child.getId())
                );

        if (hasSiblingWithTheSameSlug) {
            throw new EntityUpdateConflictException(CREATE_UNIQUE_SLUG_ERROR);
        }
    }

}
