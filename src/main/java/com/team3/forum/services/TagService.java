package com.team3.forum.services;

import com.team3.forum.models.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(String name, int userId);
    Tag updateTag(int id, String name, int userId);
    Tag findById(int id);
    List<Tag> findAll();
    void deleteById(int id, int userId);

    List<Tag> findTopByOrderByPostsCountDesc(int limit);
}