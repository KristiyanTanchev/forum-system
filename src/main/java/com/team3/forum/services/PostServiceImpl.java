package com.team3.forum.services;

import com.team3.forum.exceptions.AuthorizationException;
import com.team3.forum.exceptions.DuplicateEntityException;
import com.team3.forum.exceptions.EntityNotFoundException;
import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.User;
import com.team3.forum.models.enums.PostSortField;
import com.team3.forum.models.enums.SortDirection;
import com.team3.forum.models.postDtos.PostUpdateDto;
import com.team3.forum.repositories.PostRepository;
import com.team3.forum.repositories.PostViewRepository;
import com.team3.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    public static final String ALREADY_LIKED_ERROR = "This post is already liked by this user.";
    public static final String NOT_LIKED_ERROR = "This post is not liked by this user.";
    public static final String EDIT_AUTHORIZATION_ERROR = "You cannot edit this post.";
    public static final String DELETE_AUTHORIZATION_ERROR = "You cannot delete this post.";
    public static final String RESTORE_AUTHORIZATION_ERROR = "You cannot restore this post.";
    public static final int POSTS_PAGE_SIZE = 20;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostViewRepository postViewRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, PostViewRepository postViewRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postViewRepository = postViewRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Post findById(int id) {
        return postRepository.findById(id);
    }

    @Override
    public void deleteById(int id, int requesterId) {
        User requester = userRepository.findById(requesterId);
        Post persistent = postRepository.findById(id);
        if (persistent == null) {
            throw new EntityNotFoundException("Post", id);
        }
        verifyAdminOrOwner(persistent,
                requester,
                new AuthorizationException(DELETE_AUTHORIZATION_ERROR));
        persistent.setDeleted(true);
        persistent.setDeletedAt(LocalDateTime.now());
        postRepository.save(persistent);
    }

    @Override
    public Post restoreById(int id, int requesterId) {
        User requester = userRepository.findById(requesterId);
        Post persistent = postRepository.findByAndIsDeleted(id);
        if (persistent == null) {
            throw new EntityNotFoundException("Post", id);
        }

        verifyAdminOrOwner(persistent,
                requester,
                new AuthorizationException(RESTORE_AUTHORIZATION_ERROR));

        persistent.setDeleted(false);
        persistent.setDeletedAt(null);
        return postRepository.save(persistent);
    }

    @Override
    public Post create(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post update(int postId, PostUpdateDto postUpdateDto, int requesterId) {
        User requester = userRepository.findById(requesterId);
        Post persistent = postRepository.findById(postId);

        verifyAdminOrOwner(persistent,
                requester,
                new AuthorizationException(EDIT_AUTHORIZATION_ERROR));

        persistent.setTitle(postUpdateDto.getTitle());
        persistent.setContent(postUpdateDto.getContent());

        return postRepository.save(persistent);
    }

    @Override
    @Transactional(readOnly = true)
    public int getLikes(int postId) {
        Post post = postRepository.findById(postId);
        return post.getLikedBy().size();
    }

    @Override
    public void likePost(int postId, int userId) {
        Post post = postRepository.findById(postId);
        User user = userRepository.findById(userId);

        if (post.getLikedBy().contains(user)) {
            throw new DuplicateEntityException(ALREADY_LIKED_ERROR);
        }

        post.getLikedBy().add(user);
        user.getLikedPosts().add(post);

        postRepository.save(post);
    }

    @Override
    public void unlikePost(int postId, int userId) {
        Post post = postRepository.findById(postId);
        User user = userRepository.findById(userId);

        if (!post.getLikedBy().contains(user)) {
            throw new EntityNotFoundException(NOT_LIKED_ERROR);
        }

        post.getLikedBy().remove(user);
        user.getLikedPosts().remove(post);

        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsInFolderPaginated(Folder folder, int page, String orderBy, String direction) {
        PostSortField sortField = getSortField(orderBy);
        SortDirection sortDirection = getSortDirection(direction);
        if (page < 1) {
            page = 1;
        }
        return postRepository.findPostsInFolderPaginated(page, POSTS_PAGE_SIZE, folder, sortField, sortDirection);
    }

    private void verifyAdminOrOwner(Post post, User requester, RuntimeException error) {
        if (!requester.isAdmin() && post.getUser().getId() != requester.getId()) {
            throw error;
        }
    }

    @Override
    public void registerView(int postId, int userId) {
        LocalDate now = LocalDateTime.now().toLocalDate();
        if (!postViewRepository.existsForDate(postId, userId, now)) {
            postViewRepository.registerView(postId, userId);
        }
    }

    @Override
    public long getPostViews(int postId) {
        return postViewRepository.getTotalViewsForPost(postId);
    private PostSortField getSortField(String orderBy) {
        try {
            return PostSortField.valueOf(orderBy.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return PostSortField.CREATED_AT;
        }
    }

    private SortDirection getSortDirection(String direction) {
        try {
            return SortDirection.valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SortDirection.DESC;
        }
    }
}
