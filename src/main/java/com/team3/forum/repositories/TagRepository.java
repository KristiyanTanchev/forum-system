package com.team3.forum.repositories;

import com.team3.forum.models.Tag;

import java.util.List;

public interface TagRepository {
    Tag save(Tag entity);
    Tag findById(int id);
    boolean existsById(int id);
    List<Tag> findAll();
    void deleteById(int id);
    void delete(Tag entity);

    List<Tag> findTopByOrderByPostsCountDesc(int limit);

    Tag findByName(String name);

    boolean existsByName(String name);

    List<Tag> findAllWithPostsCount();
}
