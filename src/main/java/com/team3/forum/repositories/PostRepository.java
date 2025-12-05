package com.team3.forum.repositories;

import com.team3.forum.models.Folder;
import com.team3.forum.models.Post;
import com.team3.forum.models.enums.PostSortField;
import com.team3.forum.models.enums.SortDirection;

import java.util.List;

public interface PostRepository {
    Post save(Post entity);

    Post findById(int id);

    boolean existsById(int id);

    List<Post> findAll();

    void deleteById(int id);

    void delete(Post entity);

    Post findByAndIsDeleted(int id);

    List<Post> findPostsInFolderPaginated(int page, int size, Folder parent, PostSortField orderBy, SortDirection direction);

    List<Post> findPostsInFolderWithTagPaginated(int page, int size, String searchQuery, Folder parent, PostSortField orderBy, SortDirection direction, int tagId);

    int countPostsInFolderWithTag(Folder parent, int tagId);

    int countPostsInFolderWithTagAndSearch(Folder parent, int tagId, String searchQuery);

    List<Post> findAllSortedByViewsLastDays(int limit, int days);

    int getPostsCount();
}
