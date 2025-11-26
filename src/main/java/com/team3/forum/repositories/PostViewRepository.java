package com.team3.forum.repositories;

import java.time.LocalDate;

public interface PostViewRepository {
    void registerView(int postId, int userId);

    long getTotalViewsForPost(int postId);

    boolean existsForDate(int postId, int userId, LocalDate viewDate);
}
