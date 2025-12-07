package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.exceptions.EntityUpdateConflictException;
import com.team3.forum.exceptions.FolderNotEmptyException;
import com.team3.forum.helpers.FolderMapper;
import com.team3.forum.helpers.TimeAgo;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.folderDtos.*;
import com.team3.forum.repositories.FolderRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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
    private final FolderMapper folderMapper;

    @Autowired
    public FolderServiceImpl(FolderRepository folderRepository, UserRepository userRepository, FolderMapper folderMapper) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.folderMapper = folderMapper;
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
        if (!requester.isModerator()) {
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
    public Folder create(FolderCreateDto folderCreateDto, List<String> slugs, int requesterId) {
        if (slugs.isEmpty()) {
            throw new EntityUpdateConflictException(CREATE_NO_SLUGS_ERROR);
        }
        User requester = userRepository.findById(requesterId);
        if (!requester.isModerator()) {
            throw new AuthorizationException(CREATE_AUTHORIZATION_ERROR);
        }
        Folder parent = getFolderByPath(slugs);
        Folder folder = folderMapper.toEntity(folderCreateDto);
        folder.setParentFolder(parent);

        validateUniqueSlug(parent, folder);

        return folderRepository.save(folder);
    }

    @Override
    public Folder create(FolderCreateDto folderCreateDto, int requesterId) {
        int parentId = folderCreateDto.getParentFolderId();
        if (parentId == 0) {
            throw new EntityUpdateConflictException(CREATE_NO_SLUGS_ERROR);
        }
        User requester = userRepository.findById(requesterId);
        if (!requester.isModerator()) {
            throw new AuthorizationException(CREATE_AUTHORIZATION_ERROR);
        }
        Folder parent = findById(parentId);
        Folder folder = folderMapper.toEntity(folderCreateDto);
        folder.setParentFolder(parent);

        folder.setParentFolder(parent);

        validateUniqueSlug(parent, folder);

        return folderRepository.save(folder);
    }

    @Override
    public Folder update(List<String> slugs, FolderUpdateDto folderUpdateDto, int requesterId) {
        if (slugs.isEmpty()) {
            throw new EntityUpdateConflictException(EDIT_NO_SLUGS_ERROR);
        }
        folderUpdateDto.setId(getFolderByPath(slugs).getId());
        return update(folderUpdateDto, requesterId);
    }


    @Override
    public Folder update(FolderUpdateDto folderUpdateDto, int requesterId) {

        Folder folder = findById(folderUpdateDto.getId());
        if (folder.getParentFolder() == null) {
            throw new EntityUpdateConflictException(EDIT_NO_SLUGS_ERROR);
        }
        User requester = userRepository.findById(requesterId);

        if (!requester.isModerator()) {
            throw new AuthorizationException(EDIT_AUTHORIZATION_ERROR);
        }

        folder.setName(folderUpdateDto.getName());
        folder.setSlug(folderUpdateDto.getSlug());
        folder.setDescription(folderUpdateDto.getDescription());

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
            throw new EntityNotFoundException("Empty path");
        }

        Folder current = folderRepository.findBySlug(slugs.get(0));
        Folder parent;
        if (current.getParentFolder() != null) {
            throw new EntityNotFoundException("Incorrect path: " + slugs);
        }

        for (int i = 1; i < slugs.size(); i++) {
            String slug = slugs.get(i);
            parent = current;
            current = folderRepository.findByParentFolderAndSlug(parent, slug);
        }
        return current;
    }

    @Override
    public List<Folder> getSiblingFolders(Folder folder) {
        List<Folder> folders = folderRepository.getFoldersByParentFolder(folder.getParentFolder());
        folders.remove(folder);
        return folders;
    }

    @Override
    @Transactional(readOnly = true)
    public LocalDateTime getLastActivity(Folder folder) {
        Folder persistent = folderRepository.findById(folder.getId());
        LocalDateTime lastPost = folderRepository.getLastPostDate(persistent);
        LocalDateTime lastComment = folderRepository.getLastCommentDate(persistent);
        if (lastPost == null) {
            return lastComment;
        }
        if (lastComment == null) {
            return lastPost;
        }
        if (lastPost.isAfter(lastComment)) {
            return lastPost;
        } else {
            return lastComment;
        }
    }

    @Override
    public List<String> buildSlugPath(Folder folder) {
        List<String> slugs = new ArrayList<>();

        Folder current = folder;
        while (current != null) {
            slugs.add(current.getSlug());
            current = current.getParentFolder();
        }

        Collections.reverse(slugs);
        return slugs;
    }

    @Transactional(readOnly = true)
    public FolderResponseDto buildFolderResponseDto(Folder folder) {
        Folder persistent = folderRepository.findById(folder.getId());
        return folderMapper.toResponseDto(folder, buildFolderCalculatedStatsDto(persistent));
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

    private FolderCalculatedStatsDto buildFolderCalculatedStatsDto(Folder folder) {
        LocalDateTime lastActivity = getLastActivity(folder);
        String lastActivityString = lastActivity != null ? TimeAgo.toTimeAgo(lastActivity) : "";
        return FolderCalculatedStatsDto.builder()
                .postCount(folder.getPosts().size())
                .folderCount(folder.getChildFolders().size())
                .lastActivity(lastActivityString)
                .path(buildPath(folder, ""))
                .pathFolders(buildPathFolders(folder, new ArrayList<>()))
                .postCountWithSubfolders(getFolderPostsCount(folder))
                .build();
    }

    private String buildPath(Folder folder, String path) {
        StringBuilder sb = new StringBuilder();
        if (folder.getParentFolder() != null) {
            sb.append(buildPath(folder.getParentFolder(), path)).append("/");
        }
        return sb.append(path).append(folder.getSlug()).toString();
    }

    private List<FolderPathDto> buildPathFolders(Folder folder, List<FolderPathDto> result) {
        if (folder.getParentFolder() != null) {
            buildPathFolders(folder.getParentFolder(), result);
        }
        result.add(folderMapper.toPathDto(folder));
        return result;
    }

    private int getFolderPostsCount(Folder folder) {
        int sum = folder.getPosts().size();
        for (Folder child : folder.getChildFolders()) {
            sum += getFolderPostsCount(child);
        }
        return sum;
    }
}
