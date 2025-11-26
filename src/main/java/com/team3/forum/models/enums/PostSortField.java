package com.team3.forum.models.enums;

import lombok.Getter;

@Getter
public enum PostSortField {
    ID("p.id"),
    CREATED_AT("p.createdAt"),
    UPDATED_AT("p.updatedAt"),
    COMMENTS_COUNT("SIZE(p.comments)"),
    TITLE("p.title");

    private final String jpqlField;

    PostSortField(String jpqlField) {
        this.jpqlField = jpqlField;
    }
}
