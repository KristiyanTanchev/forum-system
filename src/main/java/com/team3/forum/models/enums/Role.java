package com.team3.forum.models.enums;

public enum Role {
    USER,
    MODERATOR,
    ADMIN;

    public boolean isModerator() {
        return this == MODERATOR || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
