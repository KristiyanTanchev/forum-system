package helpers;

import com.team3.forum.models.Folder;

import java.time.LocalDateTime;

public class FolderHelpers {
    public static Folder createMockFolder() {
        Folder folder = new Folder();
        folder.setId(1);
        folder.setParentFolder(null);
        folder.setName("Test folder");
        folder.setSlug("Test slug");
        folder.setCreatedAt(LocalDateTime.now());
        return folder;
    }
}
