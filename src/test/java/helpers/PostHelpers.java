package helpers;

import com.team3.forum.models.Post;

import java.time.LocalDateTime;
import java.util.HashSet;

import static helpers.UserHelpers.createMockUser;
import static helpers.FolderHelpers.createMockFolder;

public class PostHelpers {
    public static Post createMockPost() {
        Post post = new Post();
        post.setId(1);
        post.setUser(createMockUser());
        post.setTitle("Test title");
        post.setContent("Test content");
        post.setComments(new HashSet<>());
        post.setFolder(createMockFolder());
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        return post;
    }
}
