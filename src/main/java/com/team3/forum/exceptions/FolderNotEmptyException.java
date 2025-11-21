package com.team3.forum.exceptions;

public class FolderNotEmptyException extends RuntimeException {
    public FolderNotEmptyException(String message) {
        super(message);
    }

    public FolderNotEmptyException(int folderId) {
        this("Folder " + folderId + " cannot be deleted because it is not empty.");
    }
}
